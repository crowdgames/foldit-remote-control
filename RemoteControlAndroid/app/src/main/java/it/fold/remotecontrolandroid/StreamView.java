package it.fold.remotecontrolandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.view.ScaleGestureDetector.*;
import android.view.GestureDetector.*;

/**
* Handles events and imaging
*/
public class StreamView extends SurfaceView implements SurfaceHolder.Callback
{
    private StreamThread mStreamThread; /** object to handle streaming */
    private Handler mStreamThreadHandler;

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    //
    private SparseArray<PointF> mActivePointers;
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW };


    private final GestureDetector.OnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
    };

    private final OnScaleGestureListener mScaleGestureListener
            = new SimpleOnScaleGestureListener() {
    };

    /**
    * constructor that inherits fields from surfaceview to give attributes
    * and context to view
    *
    * @param Context context global information about application environment
    * @param AttributeSet attrs collection of attributes
    */
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
    /**
    * When any changes are made to the surface this is called to update image
    *
    * @param SurfaceHolder holder handles surface changes to imaging
    */
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d("StreamView", "creating surface");

        Paint paint = new Paint();
        paint.setColor(0xFFFFFFFF);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0xFFFF0000);
        canvas.drawLine(0, 0, 100, 100, paint);
        holder.unlockCanvasAndPost(canvas);

        mActivePointers = new SparseArray<PointF>();

        mStreamThread.initialize(Constants.IP_ADDRESS_LOCAL, Constants.PORT, "");
        mStreamThread.setRunning(true);
        mStreamThread.start();
        Log.d("StreamView", "successfully created surface");
    }

    @Override
    /**
    * When surface is destroyed, logs to user
    *
    * @param SurfaceHolder holder unused but handles surface changing
    */
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d("StreamView", "surfaceDestroyed");
    }

    @Override
    /**
    * When surface is changed, logs to user
    *
    * @param SurfaceHolder holder unused but handles surface changing
    * @param int format new pixel format
    * @param int width width of surface
    * @param int height height of surface
    */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.d("StreamView", "surfaceChanged");
    }

    @Override
    /**
    * Handles touch events from the user
    *
    * @param MotionEvent e information about event
    * @return boolean true
    */
    public boolean onTouchEvent(MotionEvent e) {
    //mScaleDetector.onTouchEvent(e);
    //touchTime = e.getEventTime();
        mGestureDetector.onTouchEvent(e);
        int x = (int) e.getX();
        int y = (int) e.getY();
        int action = e.getAction();
        int cl_action = 0;

        int historySize = e.getHistorySize();
        int maskedAction = e.getActionMasked();
        int pointerIndex = e.getActionIndex();
        int pointerId = e.getPointerId(pointerIndex);


//        Log.d("debug", "historySize: " + historySize);
//        Log.d("debug", "maskedAction: " + maskedAction);
//        Log.d("debug", "pointerIndex: " + pointerIndex);
//        Log.d("debug", "pointerId: " + pointerId);




//        if (e.getPointerCount() > 1) {
//            Log.d("streamdebug", "Multiple pointers registered");
//            Log.d("streamdebug", String.format("Begin iterating through pointer history size of %S (indexed started at 0)", historySize));
//            for (int h = 0; h < historySize; h++) {
//                Log.d("streamdebug", String.format("Pointer: %S", h));
//                if (action == MotionEvent.ACTION_MOVE) {
//                    cl_action = Constants.CLEV_MOUSE_MOVE;
//                } else if (action == MotionEvent.ACTION_DOWN) {
//                    switch (h) {
//                        case 0: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_0;
//                        case 1: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_1;
//                        case 2: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_2;
//                    }
//                }
//                int xx = (int) e.getHistoricalX(h);
//                int yy = (int) e.getHistoricalY(h);
//                mStreamThreadHandler.obtainMessage(cl_action, xx, yy).sendToTarget();
//            }
//            return mScaleGestureDetector.onTouchEvent(e);
//        }

        switch(maskedAction) {
            case MotionEvent.ACTION_DOWN: {
                Log.d("debug", "ACTION_DOWN");

            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.d("debug", "ACTION_POINTER_ DOWN");
                // We have a new pointer. Lets add it to the list of pointers

                PointF f = new PointF();
                f.x = e.getX(pointerIndex);
                f.y = e.getY(pointerIndex);
                mActivePointers.put(pointerId, f);

                Log.d("debug", "f.x: " + f.x + " ::: f.y: " + f.y);
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                Log.d("debug", "ACTION_MOVE");
                for (int size = e.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(e.getPointerId(i));
                    if (point != null) {
                        int pointId = e.getPointerId(i);
                        point.x = e.getX(i);
                        point.y = e.getY(i);
                        Log.d("debug", "POINT ID:: " + pointId + " point.x: " + point.x + " ::: point.y: " + point.y);
                        switch (pointId) {
                            case 0: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_0;
                            case 1: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_1;
                            case 2: cl_action = Constants.CLEV_MOUSE_DOWN_AUX_2;
                        }
                      mStreamThreadHandler.obtainMessage(cl_action, ((int) point.x), ((int) point.y)).sendToTarget();
                    }
                }
//                break;
                return mScaleGestureDetector.onTouchEvent(e);
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                Log.d("debug", "pointer removed");
                mActivePointers.remove(pointerId);
                break;
            }
        }
        invalidate();
        Log.d("debug", "--");

        draw();


        return true;

//
//        if (action == MotionEvent.ACTION_MOVE) {
////          if (zoom) {zoomX = e.getX();zoomY = e.getY(); invalidate();}
//            cl_action = Constants.CLEV_MOUSE_MOVE;
//        } else if (action == MotionEvent.ACTION_DOWN) {
//            cl_action = Constants.CLEV_MOUSE_DOWN;
//        } else if (action == MotionEvent.ACTION_UP) {
//            cl_action = Constants.CLEV_MOUSE_UP;
//        } else if (action == MotionEvent.ACTION_SCROLL) {
//            if (Build.VERSION.SDK_INT >= 12) {
//                if (e.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0) {
//                    cl_action = Constants.CLEV_SCROLL_DOWN;
//                } else {
//                    cl_action = Constants.CLEV_SCROLL_UP;
//                }
//            }
//        } else {
//            return true;
//        }

//        mStreamThreadHandler.obtainMessage(cl_action, x, y).sendToTarget();
//        return true;
    }

    /**
    * draws pointers, currently unused
    */
    public void draw() {


        // draw all pointers
//        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
//            PointF point = mActivePointers.valueAt(i);
//            if (point != null)
//                mPaint.setColor(colors[i % 9]);
//            canvas.drawCircle(point.x, point.y, SIZE, mPaint);
//        }
//        canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40 , textPaint);

    }


}
