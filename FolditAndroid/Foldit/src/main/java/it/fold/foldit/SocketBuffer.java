package it.fold.foldit;

//import java.awt.*;
import android.net.TrafficStats;
import android.os.Build;
import android.util.Log;

import java.net.*;
import java.io.*;
//import java.util.*;

public class SocketBuffer {

	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;

	public SocketBuffer(String host, int port) throws Exception {

        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        if (Build.VERSION.SDK_INT >= 14) { // for debugging network usage
            TrafficStats.setThreadStatsTag(0xF00D);
            TrafficStats.tagSocket(socket);
        }
	}

	public BufferedReader getIn() {
		return in;
	}
	
	public BufferedWriter getOut() {
		return out;
	}
    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}
