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
		Debug.Log(socket.Send(buf).ToString());
		Debug.Log(socket.Receive(bytes).ToString());
		dothing();
		Debug.Log("Connected");
	}

	// Update is called once per frame
	void Update() {
		if (timeWaited >= 2.0f) {
			Debug.Log("Sending refresh");
			Debug.Log(socket.Send(new byte[] { 88, 1, 0, 0, 0, 0, 0 }).ToString());
			Debug.Log("Sent refresh");
			Debug.Log(socket.Receive(bytes).ToString());
			Debug.Log("Received screen");
			dothing();
			timeWaited -= 2.0f;
		}
		timeWaited += Time.deltaTime;
	}

	void dothing() {
		string s = "";
		for (int q = 0; q < 256; q++)
			s += ((int)(bytes[q])).ToString() + ", ";
		Debug.Log(s);
	}

	public void ZoomIn() {
		Debug.Log("Sending zoom in");
		Debug.Log(socket.Send(new byte[] { 88, 4, 0, 1, 1, 1, 1 }).ToString());
		Debug.Log("Sent zoom in");
		Debug.Log(socket.Receive(bytes).ToString());
		Debug.Log("Received screen");
		dothing();
	}

	public void ZoomOut() {
		Debug.Log("Sending zoom out");
		Debug.Log(socket.Send(new byte[] { 88, 3, 0, 1, 1, 1, 1 }).ToString());
		Debug.Log("Sent zoom out");
		Debug.Log(socket.Receive(bytes).ToString());
		Debug.Log("Received screen");
		dothing();
	}
}