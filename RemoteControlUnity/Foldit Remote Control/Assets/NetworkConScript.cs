using UnityEngine;
using UnityEngine.UI;
using System.Net.Sockets;


public class NetworkConScript : MonoBehaviour {
    public enum keys : int { Ctrl = 0, Alt = 1, Shift = 2 };
    public enum ptr : int { Down = 11, Up = 12, Move = 13 };
    int port = 1230;
    public string host = "169.254.226.223";

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
    public void Tap(bool down, int x, int y){
        int val = (down) ? 8 : 9;
        string log = (down) ? "Mouse down" : "Mouse up";
        Debug.Log(log);
        SendPack(x, y, val, 0);
        receiveToBytes();
    }

    public void Zoom(bool zoomIn){
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