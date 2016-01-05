package it.fold.remotecontrolandroid;

import android.content.Intent;
import android.net.TrafficStats;
import android.os.Build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Handles buffer reading
 */
public class SocketBuffer {
    private Socket mSocket;
    private BufferedReader mIn;
    private BufferedWriter mOut;

    /**
     * Constructor for SocketBuffer
     *
     * @param host host address of StreamThread
     * @param port port of StreamThread
     */
    public SocketBuffer(String host, int port) throws Exception {
        mSocket = new Socket(host, port);
        mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        mOut = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));

        // for debugging network usage
        if (Build.VERSION.SDK_INT >= 14) {
            TrafficStats.setThreadStatsTag(0xF00D);
            TrafficStats.tagSocket(mSocket);
        }
    }

    /**
     * Abstraction to return private field
     *
     * @return mIn
     */
    public BufferedReader getIn() {
        return mIn;
    }

    /**
     * Abstraction to return private field
     *
     * @return mOut
     */
    public BufferedWriter getOut() {
        return mOut;
    }

    /**
     * Safely closes buffer
     */
    public void close() {
        try {
            mOut.close();
            mIn.close();
            mSocket.close();
        }
        // Exception handling
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
