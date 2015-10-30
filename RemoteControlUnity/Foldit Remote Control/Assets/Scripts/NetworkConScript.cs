using UnityEngine;
using UnityEngine.UI;
using System.Net.Sockets;


public class NetworkConScript : MonoBehaviour {
	int port = 1230;
	string host = "169.254.226.223";

	int screenwidth = 1120;
	int screenheight = 630;
	Socket socket;
	byte[] bytes = new byte[10000000];
	double timeWaited = 0.0;
	// Use this for initialization
	void Start() {
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
			receiveToBytes();
			timeWaited -= 2.0f;
		}
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
		int bytesReceived;
		do {
			bytesReceived = socket.Receive(bytes);
			Debug.Log("****Received " + bytesReceived + " bytes for the screen****");
			string s = "";
			int byteCount = bytesReceived < 256 ? bytesReceived : 256;
			for (int q = 0; q < byteCount; q++)
				s += ((int)(bytes[q])).ToString() + ", ";
			Debug.Log(s);
		} while (bytes[1] > 2);
	}

	public void ZoomIn() {
		Debug.Log("Sending zoom in");
		Debug.Log("Sent " + socket.Send(new byte[] { 88, 4, 0, 1, 1, 1, 1 }).ToString() + " bytes");
		receiveToBytes();
	}

	public void ZoomOut() {
		Debug.Log("Sending zoom out");
		Debug.Log("Sent " + socket.Send(new byte[] { 88, 3, 0, 1, 1, 1, 1 }).ToString() + " bytes");
		receiveToBytes();
	}
}