package it.fold.remotecontrolandroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Arrays;

/**
 * Handles connection between mobile device and game by initializing stream
 */
public class StreamThread extends Thread {
    private Bitmap mImage;
    /**
     * image to add incoming image data do
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * surface to draw image on
     */

    private Handler mHandler;
    /**
     * input event handler
     */

    private SocketBuffer mSocket;
    /**
     * communication socket
     */
    private String mAddress;
    private int mPort;
    private String mKey;

    private char mRecvBuf[];
    /**
     * buffer for receiving data
     */
    private int mRecvBufOffset;
    private int mRecvBufMsgLen;

    private char mSendBuf[];
    /**
     * buffer for sending data
     */
    private char mRefrBuf[];
    /**
     * buffer for sending data
     */
    private int mColorArray[];
    /**
     * array of colors to display to screen
     */

    private Rect rs;
    private Rect rd;

    public boolean mLowRes;
    /**
     * using low resolution?
     */
    private boolean mRun;
    /**
     * keep running?
     */
    private long mLastReceiveTime; /** last time */

    /**
     * Constructs a new StreamThread
     *
     * @param surfaceHolder surfaceHolder interface for a display surface
     */
    public StreamThread(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;

        mRun = false;
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mLowRes = false;//sharedPref.getBoolean("reduce_bandwidth", false);
        Log.i("streamdebug", "reduce_bandwith is " + mLowRes);

        rs = new Rect();
        rd = new Rect();
        rs.left = rs.top = rd.left = rd.top = 0;
        rd.right = Constants.CUR_IMG_WIDTH;
        rd.bottom = Constants.CUR_IMG_HEIGHT;
        if (mLowRes) {
            Constants.CUR_IMG_WIDTH /= 2;
            Constants.CUR_IMG_HEIGHT /= 2;
        }
        rs.right = Constants.CUR_IMG_WIDTH;
        rs.bottom = Constants.CUR_IMG_HEIGHT;
        mImage = Bitmap.createBitmap(Constants.CUR_IMG_WIDTH, Constants.CUR_IMG_HEIGHT, Bitmap.Config.ARGB_8888);

        mRecvBuf = new char[4096];
        mRecvBufOffset = 0;
        mRecvBufMsgLen = Constants.SE_MSG_HDR;

        mSendBuf = new char[Constants.CL_MSG_SIZE];
        mSendBuf[0] = Constants.MAGIC;

        mRefrBuf = new char[Constants.CL_MSG_SIZE];
        mRefrBuf[0] = Constants.MAGIC;
        mRefrBuf[1] = Constants.CLEV_REFRESH;

        mColorArray = new int[Constants.TILE_SIZE * Constants.TILE_SIZE];

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message m) {
                int event = m.what;

                mSendBuf[1] = (char) event;
                mSendBuf[2] = 0;
                mSendBuf[3] = 0;
                mSendBuf[4] = 0;
                mSendBuf[5] = 0;
                mSendBuf[6] = 0;
                if (event == Constants.CLEV_MODKEY_DOWN || event == Constants.CLEV_MODKEY_UP || event == Constants.CLEV_SCROLL_DOWN || event == Constants.CLEV_SCROLL_UP) {
                    // nothing
                } else if (event == Constants.CLEV_CHAR) {
                    // character
                    mSendBuf[2] = (char) m.obj;
                } else if (event == Constants.CLEV_MOUSE_DOWN || event == Constants.CLEV_MOUSE_UP || event == Constants.CLEV_MOUSE_MOVE || event == Constants.CLEV_AUX_PTR_DOWN || event == Constants.CLEV_AUX_PTR_UP || event == Constants.CLEV_AUX_PTR_MOVE) {
                    // pointer id
                    mSendBuf[2] = (char) m.obj;

                    // x coord
                    int x = m.arg1;
                    mSendBuf[3] = (char) (x / 128);
                    mSendBuf[4] = (char) (x % 128);

                    // y coord
                    int y = m.arg2;
                    int inv_yy = Constants.REAL_IMG_HEIGHT - 1 - y;
                    if (inv_yy < 0) {
                        inv_yy = 0;
                    }
                    mSendBuf[5] = (char) (inv_yy / 128);
                    mSendBuf[6] = (char) (inv_yy % 128);
                }
                sendProcess(mSendBuf, Constants.CL_MSG_SIZE);
            }
        };
    }

    /**
     * abstracted method to return private field
     *
     * @return mHandler of object
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * Initializes streaming connection with mobile device
     *
     * @param address address IP address of mobile device
     * @param port    port constant used for port
     * @param key key unclear maybe used for passing with arguments
     * @throws IllegalArgumentException if address or key is null or port = 0
     */
    public void initialize(String address, int port, String key) {
        if (address == null || port == 0 || key == null) {
            throw new IllegalArgumentException();
        }
        mAddress = address;
        mPort = port;
        mKey = key;
    }

    /**
     * confirms whether StreamThread is active, i.e. are we connected to device
     *
     * @return boolean of whether we are connected or not
     */
    public boolean connected() {
        return mSocket != null;
    }

    /**
     * abstracted way to call lostConnection(String) without string
     */
    public void lostConnection() {
        lostConnection(null);
    }

    /**
     * handles process of losing connection by printing to log and killing game
     *
     * @param msg any additional message to be passed to user
     */
    public void lostConnection(String msg) {
        Log.d("streamdebug", "LOST CONNECTION: " + msg);

        setRunning(false);

        /*
        Intent broadcastIntent = new Intent();
        if (msg != null) {
            broadcastIntent.putExtra("msg", msg);
        } else {
            broadcastIntent.putExtra("msg", "Lost connection.");
        }
        broadcastIntent.setAction(GameActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        GameActivity.instance().sendBroadcast(broadcastIntent);
        */
    }

    /**
     * @param send_buf
     * @param length
     * @author Conor
     * Send the process through the stream
     */
    private void sendProcess(char[] send_buf, int length) {
        if (mSocket == null) {
            Log.e("streamdebug", "socket was null, could not send data.");
            return;
        }
        Log.d("streamdebug", String.format("Sending :: Send_buf: %S - Length: %S", ((int) (send_buf[1])), length));
        // uncomment
        try {
            mSocket.getOut().write(send_buf, 0, length);
            mSocket.getOut().flush();
//            Log.d("streamdebug", String.format("Successfully sent :: Send_buf: %S - Length: %S", ((int) (send_buf[1])), length));
        } catch (Exception e) {
            Log.e("streamerror", "error sending");
            lostConnection();
            e.printStackTrace();
        }
    }

    @Override
    /**
     * Begins streaming process with mobile device and monitors for errors, e.g.
     * connection time outs
     */
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);

        Log.d("streamdebug", "trying to create socket buffer");
        Thread socketThread = new Thread() {
            public void run() {
                try {
                    Log.d("streamdebug", mAddress);
                    Log.d("streamdebug", "" + mPort);
                    mSocket = new SocketBuffer(mAddress, mPort);
                } catch (Exception e) {
                    Log.d("streamdebug", e.getMessage());
                    mSocket = null;
                }
            }
        };

        socketThread.start();

        try {
            socketThread.join(5000); // socket timeout in construction
            sleep(30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mSocket == null) {
            lostConnection("Couldn't connect.");
            return;
        }

        mLastReceiveTime = System.currentTimeMillis();

        // Send VERSION, RESIZE, KEY
        char[] first_buf = new char[Constants.CL_FIRST_MSG_SIZE];
        first_buf[0] = Constants.MAGIC;
        first_buf[1] = Constants.VERSION;

        // Key
        if (mKey != null && mKey.length() == Constants.KEY_LENGTH) {
            first_buf[2] = mKey.charAt(0);
            first_buf[3] = mKey.charAt(1);
            first_buf[4] = mKey.charAt(2);
            first_buf[5] = mKey.charAt(3);
            first_buf[6] = mKey.charAt(4);
        } else {
            first_buf[2] = 0;
            first_buf[3] = 0;
            first_buf[4] = 0;
            first_buf[5] = 0;
            first_buf[6] = 0;
        }

        // Resolution
        first_buf[7] = (char) (Constants.REAL_IMG_WIDTH / 128);
        first_buf[8] = (char) (Constants.REAL_IMG_WIDTH % 128);
        first_buf[9] = (char) (Constants.REAL_IMG_HEIGHT / 128);
        first_buf[10] = (char) (Constants.REAL_IMG_HEIGHT % 128);
        if (mLowRes) {
            first_buf[11] = 1;
        } else {
            first_buf[11] = 0;
        }
        sendProcess(first_buf, Constants.CL_FIRST_MSG_SIZE);



        mLastReceiveTime = System.currentTimeMillis(); // last time a complete frame has been received
        long lastPingTime = System.currentTimeMillis();
        boolean isDiff = false; // keeps track of whether the bitmap has changed since the last draw
        while (mRun) {
            try {
                /* We're still listening; notify server */
                if (System.currentTimeMillis() - lastPingTime > 1000) {
                    sendProcess(mRefrBuf, Constants.CL_MSG_SIZE);
                    lastPingTime = System.currentTimeMillis();
                }
                while (mSocket.getIn().ready()) {
                    int read = mSocket.getIn().read(mRecvBuf, mRecvBufOffset, mRecvBufMsgLen - mRecvBufOffset);
                    mRecvBufOffset += read;

                    if (mRecvBufOffset == Constants.SE_MSG_HDR) {
                        char magic = mRecvBuf[0];
                        if (magic != Constants.MAGIC) {
                            Log.e("streamerror", "Bad magic number");
                            setRunning(false);
                            break;
                        }
                        int additional = mRecvBuf[2] * 128 + mRecvBuf[3];
                        mRecvBufMsgLen += (additional - Constants.SE_MSG_HDR);
                    }

                    if (mRecvBufOffset == mRecvBufMsgLen) {
                        int this_msg_len = mRecvBufMsgLen;
                        mRecvBufOffset = 0;
                        mRecvBufMsgLen = Constants.SE_MSG_HDR;
                        char type = mRecvBuf[1];

                        // Commented out to prevent crazy verbose logging
                        // Log.d("stream", "GOT TYPE: " + (int)(type));

                        if (type == Constants.SEEV_FLUSH) {
                            mLastReceiveTime = System.currentTimeMillis();
                            if (isDiff) {
                                Canvas canvas = mSurfaceHolder.lockCanvas();
                                synchronized (mSurfaceHolder) {
                                    doDraw(canvas);
                                }
                                mSurfaceHolder.unlockCanvasAndPost(canvas);
                                isDiff = false;
                            }
                        } else if (type == Constants.SEEV_TILE || type == Constants.SEEV_SOLID_TILE || type == Constants.SEEV_RLE24_TILE || type == Constants.SEEV_RLE16_TILE || type == Constants.SEEV_RLE8_TILE) {
                            int xx = mRecvBuf[4] * 128 + mRecvBuf[5];
                            int yy = mRecvBuf[6] * 128 + mRecvBuf[7];
                            int sz = Constants.TILE_SIZE;

                            if (0 <= xx && xx + sz <= Constants.CUR_IMG_WIDTH &&
                                    0 <= yy && yy + sz <= Constants.CUR_IMG_HEIGHT) {
                                if (type == Constants.SEEV_TILE) {
                                    for (int ii = 0; ii < sz; ++ii) {
                                        for (int jj = 0; jj < sz; ++jj) {
                                            mColorArray[ii + jj * sz] = Color.rgb(mRecvBuf[8 + 3 * (sz - jj - 1 + ii * sz) + 0] * 2,
                                                    mRecvBuf[8 + 3 * (sz - jj - 1 + ii * sz) + 1] * 2,
                                                    mRecvBuf[8 + 3 * (sz - jj - 1 + ii * sz) + 2] * 2);
                                        }
                                    }
                                } else if (type == Constants.SEEV_SOLID_TILE) {
                                    Arrays.fill(mColorArray, Color.rgb(mRecvBuf[8] * 2, mRecvBuf[9] * 2, mRecvBuf[10] * 2));
                                } else if (type == Constants.SEEV_RLE24_TILE || type == Constants.SEEV_RLE16_TILE || type == Constants.SEEV_RLE8_TILE) {
                                    int rxx = 0;
                                    int ryy = Constants.TILE_SIZE - 1;

                                    int stride = this_msg_len;
                                    if (type == Constants.SEEV_RLE24_TILE) {
                                        stride = 4;
                                    } else if (type == Constants.SEEV_RLE16_TILE) {
                                        stride = 3;
                                    } else if (type == Constants.SEEV_RLE8_TILE) {
                                        stride = 2;
                                    }

                                    for (int ii = 8; ii < this_msg_len; ii += stride) {
                                        int run = mRecvBuf[ii + 0];
                                        int rr;
                                        int gg;
                                        int bb;
                                        if (type == Constants.SEEV_RLE24_TILE) {
                                            rr = mRecvBuf[ii + 1] * 2;
                                            gg = mRecvBuf[ii + 2] * 2;
                                            bb = mRecvBuf[ii + 3] * 2;
                                        } else if (type == Constants.SEEV_RLE16_TILE) {
                                            int clr0 = mRecvBuf[ii + 1];
                                            int clr1 = mRecvBuf[ii + 2];
                                            rr = ((clr0 & 0x007C) >> 2) * 8;
                                            gg = (((clr0 & 0x0003) << 3) | ((clr1 & 0x0070) >> 4)) * 8;
                                            bb = ((clr1 & 0x000F) >> 0) * 16;
                                        } else {
                                            int clr = mRecvBuf[ii + 1];
                                            rr = ((clr & 0x0060) >> 5) * 64;
                                            gg = ((clr & 0x0018) >> 3) * 64;
                                            bb = ((clr & 0x0007) >> 0) * 32;
                                        }

                                        for (int jj = 0; jj < run; ++jj) {
                                            mColorArray[rxx + ryy * sz] = Color.rgb(rr, gg, bb);
                                            ryy -= 1;
                                            if (ryy < 0) {
                                                ryy = Constants.TILE_SIZE - 1;
                                                rxx += 1;
                                            }
                                        }
                                    }
                                }
                                int inv_yy = Constants.CUR_IMG_HEIGHT - yy - sz;
                                mImage.setPixels(mColorArray, 0, sz, xx, inv_yy, sz, sz);
                                isDiff = true; // bitmap has been changed
                            }
                        } else if (type == Constants.SEEV_TERMINATE) {
                            int terminateType = mRecvBuf[4];
                            if (terminateType == 1) {
                                lostConnection("Client and server versions don't match.");
                            } else if (terminateType == 2) {
                                lostConnection("Key is incorrect.");
                            } else {
                                lostConnection("Server closed connection.");
                            }
                            setRunning(false);
                            closeSocket();
                            return;
                        } else {
                            Log.e("streamerror", "Bad server event: " + (int) type);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("streamerror", "Exception: " + e.getMessage() + e.toString());
                lostConnection();
            }

            if (System.currentTimeMillis() - mLastReceiveTime > 10000) {
                lostConnection("Connection timed out.");
            }
        }
        if (mSocket != null) {
            Log.d("streamdebug", "CLOSING");
            mSendBuf[1] = Constants.CLEV_TERMINATE;
            sendProcess(mSendBuf, Constants.CL_MSG_SIZE);
            mSocket.close();
        }
    }

    /**
     * changes field to desired value so we can know whether we are currently
     * running or not
     */
    public void setRunning(boolean b) {
        mRun = b;
    }

    /**
     * closes connection when we are done streaming
     */
    private void closeSocket() {
        if (mSocket != null) {
            mSocket.close();
        }
    }

    /**
     * renders bitmap and streams
     */
    private void doDraw(Canvas canvas) {
        //Log.d("streamdebug", "doDraw");
        canvas.drawBitmap(mImage, rs, rd, null);
    }

}
