package it.fold.foldit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.util.Arrays;

/**
 * Created by jeffpyke on 7/23/13.
 * The custom Surface for displaying the game
 */
public class StreamView extends SurfaceView implements SurfaceHolder.Callback {
    public static long touchTime;
    class StreamThread extends Thread {
        private Bitmap image;
        private Handler mHandler;
        private long mLastReceiveTime;
        private boolean mRun = false;
        private SurfaceHolder mSurfaceHolder;

        private SocketBuffer s;
        private ImageView view;
        private char recv_buf[]; // buffer for receiving data
        private int recv_buf_offset;
        private int recv_buf_msg_len;
        private char send_buf[]; // buffer for sending data
        private char refr_buf[]; // buffer for sending data
        private int color_array[]; // array of colors to display to screen
        private String address;
        private int port;
        private Rect rs;
        private Rect rd;
        public boolean lowRes;
        private String key;

        public StreamThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
            address = null;
            key = "";
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            lowRes = sharedPref.getBoolean("reduce_bandwidth", false);
            Log.i("streamdebug", "reduce_bandwith is " + lowRes);
            Resources res = context.getResources();
            rs = new Rect();
            rd = new Rect();
            rs.left = rs.top = rd.left = rd.top = 0;
            rd.right = Constants.CUR_IMG_WIDTH;
            rd.bottom = Constants.CUR_IMG_HEIGHT;
            if (lowRes) {
                Constants.CUR_IMG_WIDTH /= 2;
                Constants.CUR_IMG_HEIGHT /= 2;
            }
            rs.right = Constants.CUR_IMG_WIDTH;
            rs.bottom = Constants.CUR_IMG_HEIGHT;
            image = Bitmap.createBitmap(Constants.CUR_IMG_WIDTH, Constants.CUR_IMG_HEIGHT, Bitmap.Config.ARGB_8888);

            recv_buf = new char[4096];
            recv_buf_offset = 0;
            recv_buf_msg_len = Constants.SE_MSG_HDR;
            send_buf = new char[Constants.CL_MSG_SIZE];
            refr_buf = new char[Constants.CL_MSG_SIZE];
            send_buf[0] = Constants.MAGIC;
            refr_buf[0] = Constants.MAGIC;
            refr_buf[1] = Constants.CLEV_REFRESH;

            color_array = new int[Constants.TILE_SIZE * Constants.TILE_SIZE];
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message m) {
                    int event = m.what;
                    send_buf[1] = (char) event;
                    int x = m.arg1;
                    if (event == Constants.CLEV_CHAR) { // keyboard
                        send_buf[2] = (char) x;
                    } else {
                        int y = m.arg2;
                        send_buf[2] = (char)(x / 128);
                        send_buf[3] = (char)(x % 128);
                        int inv_yy = Constants.REAL_IMG_HEIGHT - 1 - y;
                        if (inv_yy < 0) {
                            inv_yy = 0;
                        }
                        send_buf[4] = (char)(inv_yy / 128);
                        send_buf[5] = (char)(inv_yy % 128);
                    }
                    sendProcess(send_buf, Constants.CL_MSG_SIZE);
                }
            };
        }
        public Handler getHandler() {
            return mHandler;
        }
        // starts the thread
        public void doStart(String address, int port, String key) {
            if (address == null || key == null) {
                throw new IllegalArgumentException();
            }
            mLastReceiveTime = System.currentTimeMillis() + 100;
            this.address = address;
            this.port = port;
            this.key = key;
        }
        public boolean connected() {
            return s != null;
        }

        public void lostConnection() {
            lostConnection(null);
        }
        public void lostConnection(String msg) {
            setRunning(false);
            Intent broadcastIntent = new Intent();
            if (msg != null) {
                broadcastIntent.putExtra("msg", msg);
            } else {
                broadcastIntent.putExtra("msg", "Lost connection.");
            }
            broadcastIntent.setAction(GameActivity.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            GameActivity.instance().sendBroadcast(broadcastIntent);
        }
        private void sendProcess(char[] send_buf, int length) {
            if (s == null) {
                return;
            }
            try {
                s.getOut().write(send_buf, 0, length);
                s.getOut().flush();
            } catch (Exception e) {
                Log.e("streamerror", "error sending");
                lostConnection();
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
//            int tid = android.os.Process.myTid();
//            Log.d("streamdebug", "streamthread pid: " + tid);
//            Log.d("streamdebug", "priority before change = " + android.os.Process.getThreadPriority(tid));
//            Log.d("streamdebug", "priority before change = "+Thread.currentThread().getPriority());
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
//            Log.d("streamdebug", "priority after change = " + android.os.Process.getThreadPriority(tid));
//            Log.d("streamdebug", "priority after change = " + Thread.currentThread().getPriority());
            long time = System.currentTimeMillis();
            while (mRun && address == null) {
                // busy wait
                if (time > 3000) { // sanity check
                    return;
                }
                Log.d("streamdebug", "busy wait");
            }
            Log.d("streamdebug", "trying to create socket buffer");
            Thread socketThread = new Thread() {
                public void run() {
                    try {
                        int tid = android.os.Process.myTid();
                        Log.d("streamdebug", "creating socket pid: " + tid);
                        s = new SocketBuffer(address, port);
                    } catch (Exception e) {
                        s = null;
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
            if (s == null) {
                lostConnection("Couldn't connect.");
                return;
            }

            GameActivity.instance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GameActivity.instance().setLoadingDone();
                    // disable loading gif
                }
            });

            // Send VERSION, RESIZE, KEY
            char[] first_buf = new char[Constants.CL_FIRST_MSG_SIZE];
            first_buf[0] = Constants.MAGIC;
            first_buf[1] = Constants.CLEV_VERSION;
            first_buf[2] = Constants.VERSION;

            // Key
            if (key != null && key.length() == Constants.KEY_LENGTH) {
                for (int i = 0; i < Constants.KEY_LENGTH; i++) {
                    first_buf[i + 3] = key.charAt(i);
                }
            }

            // Resolution
            first_buf[8] = (char)(Constants.REAL_IMG_WIDTH / 128);
            first_buf[9] = (char)(Constants.REAL_IMG_WIDTH % 128);
            first_buf[10] = (char)(Constants.REAL_IMG_HEIGHT / 128);
            first_buf[11] = (char)(Constants.REAL_IMG_HEIGHT % 128);
            if (lowRes) {
                first_buf[12] = 1;
            } else {
                first_buf[12] = 0;
            }
            sendProcess(first_buf, Constants.CL_FIRST_MSG_SIZE);
            mLastReceiveTime = System.currentTimeMillis(); // last time a complete frame has been received
            long lastPingTime = System.currentTimeMillis();
            boolean isDiff = false; // keeps track of whether the bitmap has changed since the last draw
            while (mRun) {
                try {
                    /* We're still listening; notify server */
                    if (System.currentTimeMillis() - lastPingTime > 1000) {
                        s.getOut().write(refr_buf, 0, Constants.CL_MSG_SIZE);
                        s.getOut().flush();
                        lastPingTime = System.currentTimeMillis();
                    }
                    while (s.getIn().ready()) {
                        int read = s.getIn().read(recv_buf, recv_buf_offset, recv_buf_msg_len - recv_buf_offset);
                        recv_buf_offset += read;

                        if (recv_buf_offset == Constants.SE_MSG_HDR) {
                            char magic = recv_buf[0];
                            if (magic != Constants.MAGIC) {
                                Log.e("streamerror", "Bad magic number");
                                setRunning(false);
                                break;
                            }
                            int additional = recv_buf[2] * 128 + recv_buf[3];
                            recv_buf_msg_len += (additional - Constants.SE_MSG_HDR);
                        }

                        if (recv_buf_offset == recv_buf_msg_len) {
                            int this_msg_len = recv_buf_msg_len;
                            recv_buf_offset = 0;
                            recv_buf_msg_len = Constants.SE_MSG_HDR;
                            char type = recv_buf[1];
                            if (type == Constants.SEEV_FLUSH) {
                                mLastReceiveTime = System.currentTimeMillis();
                                //time = System.currentTimeMillis();
                                //Log.d("streamdebug", "time passed to receive refresh: " + (System.currentTimeMillis() - mLastReceiveTime) + " milliseconds");
                                if (isDiff) {
                                    Canvas canvas = mSurfaceHolder.lockCanvas();
                                    synchronized (mSurfaceHolder) {
                                        doDraw(canvas);
                                    }
                                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                                    isDiff = false;
                                    //Log.d("streamdebug", "render took: " + (System.currentTimeMillis() - time) + " milliseconds");
                                }
                            } else if (type == Constants.SEEV_TILE || type == Constants.SEEV_SOLID_TILE || type == Constants.SEEV_RLE24_TILE || type == Constants.SEEV_RLE16_TILE || type == Constants.SEEV_RLE8_TILE) {
                                int xx = recv_buf[4] * 128 + recv_buf[5];
                                int yy = recv_buf[6] * 128 + recv_buf[7];
                                int sz = Constants.TILE_SIZE;

                                if (0 <= xx && xx + sz <= Constants.CUR_IMG_WIDTH &&
                                        0 <= yy && yy + sz <= Constants.CUR_IMG_HEIGHT) {
                                    if (type == Constants.SEEV_TILE) {
                                        for (int ii = 0; ii < sz; ++ ii) {
                                            for (int jj = 0; jj < sz; ++ jj) {
                                                color_array[ii + jj * sz] = Color.rgb(recv_buf[8 + 3 * (sz - jj - 1 + ii * sz) + 0] * 2,
                                                        recv_buf[8 + 3 * (sz - jj - 1 + ii * sz) + 1] * 2,
                                                        recv_buf[8 + 3 * (sz - jj - 1 + ii * sz) + 2] * 2);
                                            }
                                        }
                                    } else if (type == Constants.SEEV_SOLID_TILE) {
                                        Arrays.fill(color_array, Color.rgb(recv_buf[8] * 2, recv_buf[9] * 2, recv_buf[10] * 2));
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
                                            int run = recv_buf[ii + 0];
                                            int rr;
                                            int gg;
                                            int bb;
                                            if (type == Constants.SEEV_RLE24_TILE) {
                                                rr  = recv_buf[ii + 1] * 2;
                                                gg  = recv_buf[ii + 2] * 2;
                                                bb  = recv_buf[ii + 3] * 2;
                                            } else if (type == Constants.SEEV_RLE16_TILE) {
                                                int clr0 = recv_buf[ii + 1];
                                                int clr1 = recv_buf[ii + 2];
                                                rr  = ((clr0 & 0x007C) >> 2) * 8;
                                                gg  = (((clr0 & 0x0003) << 3) | ((clr1 & 0x0070) >> 4)) * 8;
                                                bb  = ((clr1 & 0x000F) >> 0) * 16;
                                            } else {
                                                int clr = recv_buf[ii + 1];
                                                rr  = ((clr & 0x0060) >> 5) * 64;
                                                gg  = ((clr & 0x0018) >> 3) * 64;
                                                bb  = ((clr & 0x0007) >> 0) * 32;
                                            }

                                            for (int jj = 0; jj < run; ++ jj) {
                                                color_array[rxx + ryy * sz] = Color.rgb(rr, gg, bb);
                                                ryy -= 1;
                                                if (ryy < 0) {
                                                    ryy = Constants.TILE_SIZE - 1;
                                                    rxx += 1;
                                                }
                                            }
                                        }
                                    }
                                    int inv_yy = Constants.CUR_IMG_HEIGHT - yy - sz;
                                    image.setPixels(color_array, 0, sz, xx , inv_yy, sz, sz);
                                    isDiff = true; // bitmap has been changed
                                }
                            } else if(type == Constants.SEEV_TERMINATE) {
                                int terminateType = recv_buf[4];
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
                                Log.e("streamerror", "Bad server event: " + (int)type);
                            }
                        }
                    }
                } catch(Exception e) {
                    Log.e("streamerror", "Exception: " + e.getMessage() + e.toString());
                    lostConnection();
                }

                if (System.currentTimeMillis() - mLastReceiveTime > 10000) {
                    lostConnection("Connection timed out.");
                }
            }
            if (s != null) {
                send_buf[1] = Constants.CLEV_TERMINATE;
                sendProcess(send_buf, Constants.CL_MSG_SIZE);
                s.close();
            }
        }
        public void setRunning(boolean b) {
            mRun = b;
        }
        private void closeSocket() {
            if (s!= null) {
                s.close();
            }
        }
        private void doDraw(Canvas canvas) {
            canvas.drawBitmap(image, rs, rd, null);
        }

    }
    // stream view
    private Context mContext;
    private StreamThread thread;
    private long time;
    public Handler streamHandler;
    private boolean artificialCtrl;
    private boolean zoom;
    private float zoomX;
    private float zoomY;
    private final float ZOOM_SCALE_FACTOR = 1.5f;
    private final GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            //Log.d("streamdebug", "Longpress detected");
        }
    };
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         */
        private PointF viewportFocus = new PointF();
        private float lastSpanX;
        private float lastSpanY;
        private float lastFocusX;
        private float lastFocusY;
        private boolean translating;

        // Detects that new pointers are going down.
        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            lastSpanX = scaleGestureDetector.getCurrentSpanX();
            lastSpanY = scaleGestureDetector.getCurrentSpanY();
            lastFocusX = scaleGestureDetector.getFocusX();
            lastFocusY = scaleGestureDetector.getFocusY();
            translating = false;
            streamHandler.obtainMessage(Constants.CLEV_MOUSE_UP, 0, 0).sendToTarget();
            streamHandler.obtainMessage(Constants.CLEV_MODKEY_DOWN, 0, 0).sendToTarget();
            streamHandler.obtainMessage(Constants.CLEV_TRANSLATE, (int) scaleGestureDetector.getFocusX(), (int) scaleGestureDetector.getFocusY()).sendToTarget();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            float mScaleFactor = scaleGestureDetector.getScaleFactor();
            if (Math.abs(scaleGestureDetector.getFocusY() - lastFocusY) > 5 || Math.abs(scaleGestureDetector.getFocusX() - lastFocusX) > 5) {
                translating = true;
            }

            //Log.d("streamdebug", mScaleFactor + "");
            if (mScaleFactor < 1.0) {
                streamHandler.obtainMessage(Constants.CLEV_SCROLL_DOWN).sendToTarget();
            } else if (mScaleFactor > 1.0) {
                streamHandler.obtainMessage(Constants.CLEV_SCROLL_UP).sendToTarget();
            }
            int x = (int) scaleGestureDetector.getFocusX();
            int y = (int) scaleGestureDetector.getFocusY();
            //if (translating) {
                streamHandler.obtainMessage(Constants.CLEV_MOUSE_MOVE, x, y).sendToTarget();
            //}

            return true;

        }
        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            if (!GameActivity.ctrlDown) {
                streamHandler.obtainMessage(Constants.CLEV_MODKEY_UP, 0, 0).sendToTarget();
            }
            streamHandler.obtainMessage(Constants.CLEV_MOUSE_UP, (int) scaleGestureDetector.getFocusX(), (int) scaleGestureDetector.getFocusY()).sendToTarget();
        }
    };
    private Rect mContentRect;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    public StreamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);
        holder.addCallback(this);
        time = 0;
        thread = new StreamThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });
        streamHandler = thread.getHandler();
        setFocusable(true);
        artificialCtrl = false;
    }

    public StreamThread getThread() {
        return thread;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getUnicodeChar() != 0) {
            streamHandler.obtainMessage(Constants.CLEV_CHAR, event.getUnicodeChar(), 0).sendToTarget();
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            streamHandler.obtainMessage(Constants.CLEV_CHAR, Constants.KEYCODE_BACKSPACE, 0).sendToTarget();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //mScaleDetector.onTouchEvent(e);
        //touchTime = e.getEventTime();
        mGestureDetector.onTouchEvent(e);
        if (e.getPointerCount() > 1) {
            return mScaleGestureDetector.onTouchEvent(e);
        }
        int x = (int) e.getX();
        int y = (int) e.getY();
        int action = e.getAction();
        int cl_action = 0;
        if (action == MotionEvent.ACTION_MOVE) {
            if (zoom) {
                zoomX = e.getX();
                zoomY = e.getY();
                invalidate();
            }
            cl_action = Constants.CLEV_MOUSE_MOVE;
/* Uncomment to use 1-2 batched move events instead of just current */
//            int historySize = e.getHistorySize();
//            for (int h = 0; h < historySize; h++) {
//                int xx = (int) e.getHistoricalX(h);
//                int yy = (int) e.getHistoricalY(h);
//                streamHandler.obtainMessage(cl_action, xx, yy).sendToTarget();
//            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            cl_action = Constants.CLEV_MOUSE_DOWN;
        } else if (action == MotionEvent.ACTION_UP) {
            cl_action = Constants.CLEV_MOUSE_UP;
        } else if (action == MotionEvent.ACTION_SCROLL) {
                if (android.os.Build.VERSION.SDK_INT >= 12) {
                    if (e.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0) {
                        cl_action = Constants.CLEV_SCROLL_DOWN;
                    } else {
                        cl_action = Constants.CLEV_SCROLL_UP;
                    }
                }
        } else {
            return true;
        }
        streamHandler.obtainMessage(cl_action, x, y).sendToTarget();
        return true;
    }

    public void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException ee) {
            ee.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        //thread.setSurfaceSize(width, height);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

}
