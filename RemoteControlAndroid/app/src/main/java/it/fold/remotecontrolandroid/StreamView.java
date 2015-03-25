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
        Log.d("StreamView", "surfaceCreated");

        Paint paint = new Paint();
        paint.setColor(0xFFFFFFFF);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0xFFFF0000);
        canvas.drawLine(0, 0, 100, 100, paint);
        holder.unlockCanvasAndPost(canvas);

        mStreamThread.initialize(Constants.IP_ADDRESS_LOCAL, Constants.PORT, "");
        mStreamThread.setRunning(true);
        mStreamThread.start();
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
        if (e.getPointerCount() > 1) {
            return mScaleGestureDetector.onTouchEvent(e);
        }
        int x = (int) e.getX();
        int y = (int) e.getY();
        int action = e.getAction();
        int cl_action = 0;
        if (action == MotionEvent.ACTION_MOVE) {
//            if (zoom) {
//                zoomX = e.getX();
//                zoomY = e.getY();
//                invalidate();
//            }
            cl_action = Constants.CLEV_MOUSE_MOVE;
        /* Uncomment to use 1-2 batched move events instead of just current */
        // int historySize = e.getHistorySize();
        // for (int h = 0; h < historySize; h++) {
        // int xx = (int) e.getHistoricalX(h);
        // int yy = (int) e.getHistoricalY(h);
        // streamHandler.obtainMessage(cl_action, xx, yy).sendToTarget();
        // }
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
