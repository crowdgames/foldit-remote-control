package it.fold.remotecontrolandroid;

import android.net.TrafficStats;
import android.os.Build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketBuffer
{
    private Socket mSocket;
    private BufferedReader mIn;
    private BufferedWriter mOut;

    public SocketBuffer(String host, int port) throws Exception
    {
        mSocket = new Socket(host, port);
        mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        mOut = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));

        // for debugging network usage
        if (Build.VERSION.SDK_INT >= 14)
        {
            TrafficStats.setThreadStatsTag(0xF00D);
            TrafficStats.tagSocket(mSocket);
        }
    }

    public BufferedReader getIn()
    {
        return mIn;
    }

    public BufferedWriter getOut()
    {
        return mOut;
    }

    public void close()
    {
        try
        {
            mOut.close();
            mIn.close();
            mSocket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}

