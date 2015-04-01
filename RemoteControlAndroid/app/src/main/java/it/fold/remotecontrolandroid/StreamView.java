package it.fold.remotecontrolandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.ScaleGestureDetector.*;
import android.view.GestureDetector.*;


public class StreamView extends SurfaceView implements SurfaceHolder.Callback
{
    private StreamThread mStreamThread;
    private Handler mStreamThreadHandler;

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;


    private final GestureDetector.OnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
    };

    private final OnScaleGestureListener mScaleGestureListener
            = new SimpleOnScaleGestureListener() {
    };

        public StreamView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);

        mStreamThread = new StreamThread(surfaceHolder);
        mStreamThreadHandler = mStreamThread.getHandler();

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d("StreamView", "creating surface");

        Paint paint = new Paint();
        paint.setColor(0xFFFFFFFF);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0xFFFF0000);
        canvas.drawLine(0, 0, 100, 100, paint);
        holder.unlockCanvasAndPost(canvas);

        mStreamThread.initialize(Constants.IP_ADDRESS_LOCAL, Constants.PORT, "");
        mStreamThread.setRunning(true);
        mStreamThread.start();
        Log.d("StreamView", "successfully created surface");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d("StreamView", "surfaceDestroyed");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.d("StreamView", "surfaceChanged");
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
    //mScaleDetector.onTouchEvent(e);
    //touchTime = e.getEventTime();
        mGestureDetector.onTouchEvent(e);
        int x = (int) e.getX();
        int y = (int) e.getY();
        int action = e.getAction();
        int cl_action = 0;
        int historySize = e.getHistorySize();



        if (e.getPointerCount() > 1) {
            Log.d("streamdebug", "Multiple pointers registered");


            Log.d("streamdebug", String.format("Begin iterating through pointer history size of %S (indexed started at 0)", historySize));
            for (int h = 0; h < historySize; h++) {
                Log.d("streamdebug", String.format("Pointer: %S", h));

                if (action == MotionEvent.ACTION_MOVE) {
                    cl_action = Constants.CLEV_MOUSE_MOVE;
                } else if (action == MotionEvent.ACTION_DOWN) {
                    switch (h) {
                        case 0: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_0;
                        case 1: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_1;
                        case 2: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_2;
                    }
                }

                int xx = (int) e.getHistoricalX(h);
                int yy = (int) e.getHistoricalY(h);

                mStreamThreadHandler.obtainMessage(cl_action, xx, yy).sendToTarget();
            }
            return mScaleGestureDetector.onTouchEvent(e);
        }
        if (action == MotionEvent.ACTION_MOVE) {
//          if (zoom) {zoomX = e.getX();zoomY = e.getY(); invalidate();}
            cl_action = Constants.CLEV_MOUSE_MOVE;
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
        mStreamThreadHandler.obtainMessage(cl_action, x, y).sendToTarget();
        return true;
    }

}
