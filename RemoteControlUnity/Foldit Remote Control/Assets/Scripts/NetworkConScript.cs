using UnityEngine;
using UnityEngine.UI;
using System.Net.Sockets;

// Sean Moss
// Gregory Loden

public class NetworkConScript : MonoBehaviour
{
    //named constants
    public enum keys : int { Ctrl = 0, Alt = 1, Shift = 2 };
    public enum ptr : int { Down = 11, Up = 12, Move = 13 };
    public enum events: int { ZoomOut = 3, ZoomIn = 4, ModKeyDown = 5, ModKeyUp = 6, CharSend = 7, MousePress = 8, MouseRelease = 9, MouseMove = 10, Down = 11, Up = 12, Move = 13 }
    public enum ServerMessageType : int {
        FLUSH = 1, TERMINATE = 2,
        TILE = 3, SOLID_TILE = 4,
        RLE24_TILE = 5, RLE16_TILE = 6, RLE8_TILE = 7
    };

    //network connection stuff
    Socket socket;
    int port = 1230;
    bool isConnected = false; //whether or not the connection is active
    const byte MAGIC_CHARACTER = (byte)'X'; //this is the start of all messages

    //receiving bytes
    const int BYTE_BUFFER_SIZE = 10000000;
    byte[] bytes = new byte[BYTE_BUFFER_SIZE];
    //if network messages are incomplete, save the incomplete message in the bytes array
    int bytesSaved = 0;

    double timeWaited = 0.0; //send a refresh signal every REFRESH_INTERVAL seconds
    const double REFRESH_INTERVAL = 2.0;

    public TileRenderController tileRenderController;
    public static Color32[] tileColors = new Color32[TileRenderController.TILE_SIZE_SQUARED];

    //open the connection to Foldit
    public void connect(string host, string requiredKey) {
        Debug.Log("Start");
        socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        Debug.Log("Connecting to host \"" + host + "\" port " + port);
        socket.Connect(host, port);
        Debug.Log("Connected");
        //get the screen width to send to Foldit
        int screenwidth = tileRenderController.Width;
        int screenheight = tileRenderController.Height;
Debug.Log("Requesting screen size " + screenwidth + "x" + screenheight);
        //the key needs to be 5 characters, extend the string until it has enough
        while (requiredKey.Length < 5) {
            requiredKey += "\0";
        }
        char[] key = requiredKey.ToCharArray();

        //send an opening message including the magic character, the version we're using (3), the passkey, and the screen size
        byte[] buf = { MAGIC_CHARACTER, 3, (byte)key[0], (byte)key[1], (byte)key[2], (byte)key[3], (byte)key[4],
                        (byte)(screenwidth / 128), (byte)(screenwidth % 128),
                        (byte)(screenheight / 128), (byte)(screenheight % 128), (byte)(tileRenderController.lowres ? 1 : 0) };
        int bytesSent = socket.Send(buf);
        Debug.Log("Sent " + bytesSent.ToString() + " bytes");
        receiveToBytes();
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
                Color32 solidColor = new Color32(
                    (byte)(bytes[i + 8] << 1),
                    (byte)(bytes[i + 9] << 1),
                    (byte)(bytes[i + 10] << 1), 255);
                for (int j = 0; j < TileRenderController.TILE_SIZE_SQUARED; j++)
                    tileColors[j] = solidColor;
                break;
            //run-length encoding with color in 3 bytes
            case ServerMessageType.RLE24_TILE:
                throw new System.Exception("Rendering 3-byte-run-length-encoded data not yet implemented");
            //run-length encoding with color in 2 bytes
            case ServerMessageType.RLE16_TILE:
                int max = i + len;
                int runindex = 0;
                for (int j = i + 8; j < max; j += 3) {
                    //get the byte values from the array
                    int runlength = bytes[j];
                    int byte1 = bytes[j + 1], byte2 = bytes[j + 2];
                    //build the color from the bit values
                    Color32 color = new Color32(
                        (byte)((byte1 >> 2 & 0x1F) << 3),
                        (byte)((((byte1 & 3) << 3) | (byte2 >> 4 & 7)) << 3),
                        (byte)((byte2 & 0xF) << 4), 255);
                    //fill the colors that will be used in the texture
                    for (int runmax = runindex + runlength; runindex < runmax; runindex++) {
                        //fix up the index that we get from foldit, swap x and y
                        tileColors[runindex % TileRenderController.TILE_SIZE * TileRenderController.TILE_SIZE +
                            (runindex / TileRenderController.TILE_SIZE)] = color;
                    }
                }
                //Debug.Log(runindex);
                break;
            //run-length encoding with color in 1 byte
            case ServerMessageType.RLE8_TILE:
                throw new System.Exception("Rendering 1-byte-run-length-encoded data not yet implemented");
        }
        tileRenderController.SetTile(tileX, tileY, tileColors);
    }
    public void PtrMoved(int x, int y, ptr val)
    {
        string log = "Auxilary pointer moved";
        Debug.Log(log);
        SendPack(x, y, (int)val, 0);
        receiveToBytes();
    }
    public void ModKey(int x, int y, bool down, keys key)
    {
        events val = (down) ? events.ModKeyDown : events.ModKeyUp;
        string log = "Modifier Key pressed";
        Debug.Log(log);
        SendPack(x, y, (int)val, (int)key);
        receiveToBytes();
    }
    public void CharSend(int x, int y, char snt)
    {
        int val = (int)(events.CharSend);
        string log = "Char sent";
        Debug.Log(log);
        SendPack(x, y, val, snt);
        receiveToBytes();
    }
    public void MouseMove(int x, int y)
    {
        int val = (int)events.MouseMove;
        string log = "Mouse move";
        Debug.Log(log);
        SendPack(x, y, val, 0);
        receiveToBytes();
    }
    public void Tap(bool down, int x, int y)
    {
        events val = (down) ? events.MousePress: events.MouseRelease;
        string log = (down) ? "Mouse down" : "Mouse up";
        Debug.Log(log);
        SendPack(x, y, (int)val, 0);
        receiveToBytes();
    }

    public void Zoom(bool zoomIn)
    {
        events val = (zoomIn) ? events.ZoomIn : events.ZoomOut;
        string log = (zoomIn) ? "Sending zoom in" : "Sending zoom out";
        Debug.Log(log);
        SendPack(129, 129, (int)val, 0);
        receiveToBytes();
    }
    public void SendPack(int x, int y, int type, int info)
    {
        //before we send the coordinates to Foldit, we need to readjust it to match the location in the Foldit window
        //convert the coorindates to a 0-1 range
        float xf = (float)(x) / (float)(Screen.width);
        float yf = (float)(y) / (float)(Screen.height);

        //in order to fit 32x32 multiples, we have black bars on one pair of sides
        //when you click, 0,0 on the Foldit image is not 0,0 on the game screen
        //scale the position based on how much of the panel covers the game window, then offset by the size of the black bars
        xf = xf / tileRenderController.PanelWidthCovered - (1.0f - tileRenderController.PanelWidthCovered) / 2;
        yf = yf / tileRenderController.PanelHeightCovered - (1.0f - tileRenderController.PanelHeightCovered) / 2;

        //finally, restore the coordinates to the Foldit window range
        x = (int)(xf * tileRenderController.Width);
        y = (int)(yf * tileRenderController.Height);

    int bytesSent = socket.Send(new byte[] { MAGIC_CHARACTER, (byte)type, (byte)info, (byte)(x / 128), (byte)(x % 128), (byte)(y / 128), (byte)(y % 128) });
        Debug.Log("Sent " + bytesSent.ToString() + " bytes");
    }
    public void SendText(string text) {
        byte[] message = new byte[text.Length * 7];
        for (int i = 0; i < text.Length; i++) {
            message[i * 7] = MAGIC_CHARACTER;
            message[i * 7 + 1] = (byte)(events.CharSend);
            message[i * 7 + 2] = (byte)(text[i]);
        }
        int bytesSent = socket.Send(message);
        Debug.Log("Sent " + bytesSent.ToString() + " bytes");
    }
}
