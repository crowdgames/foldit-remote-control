using UnityEngine;
using UnityEngine.UI;
using System.Net.Sockets;
public class NetworkConScript : MonoBehaviour
{
    public enum keys : int { Ctrl = 0, Alt = 1, Shift = 2 };
    public enum ptr : int { Down = 11, Up = 12, Move = 13 };
    int port = 1230;
	string host = "169.254.226.223";
	const int BYTE_BUFFER_SIZE = 10000000;

	//This will get passed to us after it is started up
	private TileRenderController tileRenderController;

	Socket socket;
	byte[] bytes = new byte[BYTE_BUFFER_SIZE];
	double timeWaited = 0.0;

	//Pass in the render controller so that we can call it to render things and get the screen size
	public void StartWithTileRenderController(TileRenderController trc) {
		tileRenderController = trc;
		int screenwidth = tileRenderController.Width;
		int screenheight = tileRenderController.Height;
		Debug.Log("Start");
		socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
		socket.Connect(host, port);
		byte[] buf = { 88, 3, 0, 0, 0, 0, 0, (byte)(screenwidth / 128), (byte)(screenwidth % 128), (byte)(screenheight / 128), (byte)(screenheight % 128), 0 };
		Debug.Log("Sent " + socket.Send(buf).ToString() + " bytes");
		receiveToBytes();
		Debug.Log("Connected");
	}

	// Update is called once per frame
	void Update() {
		if (timeWaited >= 2.0f) {
			Debug.Log("Sending refresh");
			Debug.Log("Sent " + socket.Send(new byte[] { 88, 1, 0, 0, 0, 0, 0 }).ToString() + " bytes");
			timeWaited -= 2.0f;
		}
		receiveToBytes();
		timeWaited += Time.deltaTime;
	}

	void OnApplicationQuit() {
		Debug.Log("Sending terminate");
		Debug.Log("Sent " + socket.Send(new byte[] { 88, 2, 0, 0, 0, 0, 0 }).ToString() + " bytes");
		int bytesReceived;
		do {
			bytesReceived = socket.Receive(bytes);
			Debug.Log("****Received " + bytesReceived + " bytes for the screen****");
		} while (bytesReceived > 0);
		socket.Close();
	}

	void receiveToBytes() {
		int bytesReceived = socket.Receive(bytes);
		Debug.Log("****Received " + bytesReceived + " bytes for the screen****");
		string s = "";
		int byteCount = bytesReceived < 256 ? bytesReceived : 256;
		for (int q = 0; q < byteCount; q++)
			s += bytes[q].ToString() + ", ";
		Debug.Log(s);

		//start parsing the bytes
		for (int i = 0; i < bytesReceived;) {
			if (bytes[i] != 'X')
				throw new System.Exception("Bad network message");

			int type = bytes[i + 1];
			int len = (int)(bytes[i + 2]) * 128 + bytes[i + 3];
			//the server is done sending us image data, render the completed image
			if (type == 1) {
				tileRenderController.Flush();
			//the server told us to terminate the connection
			} else if (type == 2) {
				Debug.Log("****Server said terminate connection for reason " + bytes[i + 4] + "****");
			//the server sent image data for a 16x16 square
			} else {
				int tileX = (bytes[i + 4] * 128 + bytes[i + 5]) / TileRenderController.TILE_SIZE;
				int tileY = (bytes[i + 6] * 128 + bytes[i + 7]) / TileRenderController.TILE_SIZE;
				//uncompressed tile
				if (type == 3) {
					throw new System.Exception("Rendering uncompressed data not yet implemented");
				//solid color tile
				} else if (type == 4) {
					tileRenderController.SetTile(tileX, tileY, new Color32(
						(byte)(bytes[i + 8] << 1),
						(byte)(bytes[i + 9] << 1),
						(byte)(bytes[i + 10] << 1), 255));
				//run-length encoding with color in 3 bytes
				} else if (type == 5) {
					throw new System.Exception("Rendering 3-byte-run-length-encoded data not yet implemented");
				//run-length encoding with color in 2 bytes
				} else if (type == 6) {
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
					tileRenderController.SetTile(tileX, tileY, tileColors, false);
				//run-length encoding with color in 2 bytes
				} else if (type == 7) {
					throw new System.Exception("Rendering 1-byte-run-length-encoded data not yet implemented");
				}
			}
			i += len;
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
        Debug.Log("Sent " + socket.Send(new byte[] { 88, (byte)type, (byte)info, (byte)(x / 128), (byte)(x % 128), (byte)(y / 128), (byte)(y % 128) }).ToString() + " bytes");
    }
}
