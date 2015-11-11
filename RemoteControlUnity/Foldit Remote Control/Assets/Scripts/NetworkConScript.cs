using UnityEngine;
using UnityEngine.UI;
using System.Net.Sockets;
public class NetworkConScript : MonoBehaviour
{
    public enum keys : int { Ctrl = 0, Alt = 1, Shift = 2 };
    public enum ptr : int { Down = 11, Up = 12, Move = 13 };
    public enum ServerMessageType : int {
        FLUSH = 1, TERMINATE = 2,
        TILE = 3, SOLID_TILE = 4,
        RLE24_TILE = 5, RLE16_TILE = 6, RLE8_TILE = 7
    };
    int port = 1230;
    const int BYTE_BUFFER_SIZE = 10000000;
    bool isConnected = false;
    const byte MAGIC_CHARACTER = (byte)'X';

    //This will get passed to us after it is started up
    private TileRenderController tileRenderController;

    Socket socket;
    byte[] bytes = new byte[BYTE_BUFFER_SIZE];
    //if network messages are incomplete, save the incomplete message in the bytes array
    int bytesSaved = 0;
    double timeWaited = 0.0;
    const double REFRESH_INTERVAL = 2.0;

    //Pass in the render controller so that we can call it to render things and get the screen size
    public void StartWithTileRenderController(TileRenderController trc) {
        tileRenderController = trc;
    }

    public void connect(string host, string requiredKey) {
        int screenwidth = tileRenderController.Width;
        int screenheight = tileRenderController.Height;
        Debug.Log("Start");
        socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        socket.Connect(host, port);
        while (requiredKey.Length < 5) {
            requiredKey += "\0";
        }
        char[] key = requiredKey.ToCharArray();

        byte[] buf = { MAGIC_CHARACTER, 3, (byte)key[0], (byte)key[1], (byte)key[2], (byte)key[3], (byte)key[4],
                        (byte)(screenwidth / 128), (byte)(screenwidth % 128),
                        (byte)(screenheight / 128), (byte)(screenheight % 128), 0 };
        int bytesSent = socket.Send(buf);
        Debug.Log("Sent " + bytesSent.ToString() + " bytes");
        receiveToBytes();
        Debug.Log("Connected");
        isConnected = true;
    }

    // Update is called once per frame
    void Update() {
        if (isConnected) {
            if (timeWaited >= REFRESH_INTERVAL) {
                //Debug.Log ("Sending refresh");
                int bytesSent = socket.Send(new byte[] { MAGIC_CHARACTER, 1, 0, 0, 0, 0, 0 });
                //Debug.Log ("Sent " + bytesSent.ToString() + " bytes");
                timeWaited -= REFRESH_INTERVAL;
            }
            receiveToBytes();
            timeWaited += Time.deltaTime;
        }
    }

    void OnApplicationQuit() {
        if (isConnected) {
            Debug.Log("Sending terminate");
            int bytesSent = socket.Send(new byte[] { MAGIC_CHARACTER, 2, 0, 0, 0, 0, 0 });
            Debug.Log("Sent " + bytesSent.ToString() + " bytes");
            int bytesReceived;
            do {
                bytesReceived = socket.Receive(bytes);
                Debug.Log("****Received " + bytesReceived + " bytes for the screen****");
            } while (bytesReceived > 0);
            socket.Close();
            isConnected = false;
        }
    }

    void receiveToBytes() {
        //receive to the byte array, appending to existing bytes if there are any
        int bytesReceived = socket.Receive(bytes, bytesSaved, BYTE_BUFFER_SIZE - bytesSaved, SocketFlags.None);
        bytesReceived += bytesSaved;
        bytesSaved = 0;
        //Debug.Log("****Received " + bytesReceived + " bytes for the screen****");
        //string s = "";
        //int byteCount = bytesReceived < 256 ? bytesReceived : 256;
        //for (int q = 0; q < byteCount; q++)
        //    s += bytes[q].ToString() + ", ";
        //Debug.Log(s);

        //start parsing the bytes
        for (int i = 0; i < bytesReceived;) {
            if (bytes[i] != 'X')
                throw new System.Exception("Bad network message");

            ServerMessageType type = (ServerMessageType)bytes[i + 1];
            int len = (int)(bytes[i + 2]) * 128 + bytes[i + 3];
            //either the message got cut off before the length could be determined or
            //the message got cut off before the whole message was received
            if (i + 4 > bytesReceived || i + len > bytesReceived) {
                bytesSaved = bytesReceived - i;
                for (int j = 0; j < bytesSaved; j++)
                    bytes[j] = bytes[j + i];
                break;
            }

            switch (type) {
                //the server is done sending us image data, render the completed image
                case ServerMessageType.FLUSH:
                    tileRenderController.Flush();
                    break;
                //the server told us to terminate the connection
                case ServerMessageType.TERMINATE:
                    Debug.Log("****Server said terminate connection for reason " + bytes[i + 4] + "****");
                    OnApplicationQuit();
                    break;
                //the server sent image data for a 16x16 square
                default:
                    handleRenderMessage(type, i, len);
                    break;
            }
            i += len;
        }
    }
    void handleRenderMessage(ServerMessageType type, int i, int len) {
        int tileX = bytes[i + 4] * 128 + bytes[i + 5];
        int tileY = bytes[i + 6] * 128 + bytes[i + 7];

        switch (type) {
            //uncompressed tile
            case ServerMessageType.TILE:
                throw new System.Exception("Rendering uncompressed data not yet implemented");
            //solid color tile
            case ServerMessageType.SOLID_TILE:
                tileRenderController.SetTile(tileX, tileY, new Color32(
                    (byte)(bytes[i + 8] << 1),
                    (byte)(bytes[i + 9] << 1),
                    (byte)(bytes[i + 10] << 1), 255));
                break;
            //run-length encoding with color in 3 bytes
            case ServerMessageType.RLE24_TILE:
                throw new System.Exception("Rendering 3-byte-run-length-encoded data not yet implemented");
            //run-length encoding with color in 2 bytes
            case ServerMessageType.RLE16_TILE:
                Color32[] tileColors = new Color32[TileRenderController.TILE_SIZE_SQUARED];
                int max = i + len;
                int runindex = 0;
                for (int j = i + 8; j < max; j += 3) {
                    int runlength = bytes[j];
                    int byte1 = bytes[j + 1], byte2 = bytes[j + 2];
                    Color32 color = new Color32(
                        (byte)((byte1 >> 2 & 0x1F) << 3),
                        (byte)((((byte1 & 3) << 3) | (byte1 >> 4 & 7)) << 3),
                        (byte)((byte2 & 0xF) << 4), 255);
                    for (int runmax = runindex + runlength; runindex < runmax; runindex++) {
                        //fix up the index that we get from foldit, swap x and y
                        tileColors[runindex % TileRenderController.TILE_SIZE * TileRenderController.TILE_SIZE +
                            (runindex / TileRenderController.TILE_SIZE)] = color;
                    }
                }
                //Debug.Log(runindex);
                tileRenderController.SetTile(tileX, tileY, tileColors, false);
                break;
            //run-length encoding with color in 1 byte
            case ServerMessageType.RLE8_TILE:
                throw new System.Exception("Rendering 1-byte-run-length-encoded data not yet implemented");
        }
    }
    public void PtrMoved(int x, int y, ptr val)
    {
        string log = "Modifier Key pressed";
        Debug.Log(log);
        SendPack(x, y, (int)val, 0);
        receiveToBytes();
    }
    public void ModKey(int x, int y, bool down, keys key)
    {
        int val = (down) ? 5 : 6;
        string log = "Modifier Key pressed";
        Debug.Log(log);
        SendPack(x, y, val, (int)key);
        receiveToBytes();
    }
    public void CharSend(int x, int y, char snt)
    {
        int val = 7;
        string log = "Char sent";
        Debug.Log(log);
        SendPack(x, y, val, snt);
        receiveToBytes();
    }
    public void MouseMove(int x, int y)
    {
        int val = 10;
        string log = "Mouse move";
        Debug.Log(log);
        SendPack(x, y, val, 0);
        receiveToBytes();
    }
    public void Tap(bool down, int x, int y)
    {
        int val = (down) ? 8 : 9;
        string log = (down) ? "Mouse down" : "Mouse up";
        Debug.Log(log);
        SendPack(x, y, val, 0);
        receiveToBytes();
    }

    public void Zoom(bool zoomIn)
    {
        int val = (zoomIn) ? 4 : 3;
        string log = (zoomIn) ? "Sending zoom in" : "Sending zoom out";
        Debug.Log(log);
        SendPack(129, 129, val, 0);
        receiveToBytes();
    }
    public void SendPack(int x, int y, int type, int info)
    {
	int bytesSent = socket.Send(new byte[] { MAGIC_CHARACTER, (byte)type, (byte)info, (byte)(x / 128), (byte)(x % 128), (byte)(y / 128), (byte)(y % 128) });
        Debug.Log("Sent " + bytesSent.ToString() + " bytes");
    }
}
