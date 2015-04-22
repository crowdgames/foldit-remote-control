package it.fold.remotecontrolandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles events and imaging
 */
public class StreamView extends SurfaceView implements SurfaceHolder.Callback {
    private StreamThread mStreamThread;
    /**
     * object to handle streaming
     */
    private Handler mStreamThreadHandler;

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    Map<Integer, PointF> _pointers;

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
     * @param context context global information about application environment
     * @param attrs   attrs collection of attributes
     */
    public StreamView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);

        mStreamThread = new StreamThread(surfaceHolder);
        mStreamThreadHandler = mStreamThread.getHandler();

        _pointers = new HashMap<Integer, PointF>();

        setFocusable(true);
    }

    @Override
    /**
     * When any changes are made to the surface this is called to update image
     *
     * @param SurfaceHolder holder handles surface changes to imaging
     */
    public void surfaceCreated(SurfaceHolder holder) {
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
    /**
     * When surface is destroyed, logs to user
     *
     * @param SurfaceHolder holder unused but handles surface changing
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("StreamView", "surfaceChanged");
    }

    @Override
    /**
     * Handles touch events from the user
     *
     * @param e MotionEvent from android
     * @return
     */
    public boolean onTouchEvent(MotionEvent e) {

        mGestureDetector.onTouchEvent(e);

        if (e.getPointerCount() > 1) {
            return handleMultiplePointers(e);
        } else {
            return handleSinglePointer(e);
        }
    }

    /**
     * Add a a pointer to our list of pointers
     * @param pointId_
     * @param x_
     * @param y_
     */
    public void addPointer(int pointId_, int x_, int y_) {
        if (_pointers != null) {
            _pointers.put(pointId_, new PointF(x_, y_));
        } else {
            Log.d("error", "List of pointers was never initialized. Cannot add pointer.");
        }
    }

    /**
     * Remove the pointer from our list of pointers
     * @param pointId_
     */
    public void removePointer(int pointId_) {
        if (_pointers != null) {
            if(_pointers.containsKey(pointId_)) {
                _pointers.remove(pointId_);
            } else {
                Log.d("error", "There has been an attempt to remove a pointer that didn't exist.");
            }
        } else {
            Log.d("error", "List of pointers was never initialized. Cannot remove pointer.");
        }
    }

    /**
     * Handles multiple pointers in the form of the passed in MotionEvent
     *
     * @param e MotionEvent registered from android
     * @return
     */
    public boolean handleMultiplePointers(MotionEvent e) {

        int x = 0; // will change for each pointer
        int y = 0; // will change for each pointer
        int maskedAction = e.getActionMasked();
        int pointerCount = e.getPointerCount();
        int cl_action = 0;

        if (maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
            Log.d("debug", "action pointer down");
            for (int i = 0; i < pointerCount; i++) {
                int pointId = e.getPointerId(i);
                x = (int) e.getX(i);
                y = (int) e.getY(i);
                addPointer(pointId, x, y); // FOR TESTING

                Log.d("DEBUG", "pointer count: " + pointerCount);
                Log.d("DEBUG", "i: " + i);

                if (pointId == 0) {
                    cl_action = Constants.CLEV_MOUSE_DOWN_AUX_0;
                } else if (pointId == 1) {
                    cl_action = Constants.CLEV_MOUSE_DOWN_AUX_1;
                } else if (pointId == 2) {
                    cl_action = Constants.CLEV_MOUSE_DOWN_AUX_2;
                }
                Log.d("debug", "Sending :: POINTER DOWN :: " + cl_action + " through the stream...");
                mStreamThreadHandler.obtainMessage(cl_action, x, y).sendToTarget();
            }
        } else if (maskedAction == MotionEvent.ACTION_POINTER_UP) {
            Log.d("debug", "action pointer up");
            for (int i = 0; i < pointerCount; i++) {
                int pointId = e.getPointerId(i);
                x = (int) e.getX(i);
                y = (int) e.getY(i);
                removePointer(pointId); // FOR TESTING

                if (pointId == 0) {
                    cl_action = Constants.CLEV_MOUSE_UP_AUX_0;
                } else if (pointId == 1) {
                    cl_action = Constants.CLEV_MOUSE_UP_AUX_1;
                } else if (pointId == 2) {
                    cl_action = Constants.CLEV_MOUSE_UP_AUX_2;
                }
                Log.d("debug", "Sending :: POINTER UP :: " + cl_action + " through the stream...");
                mStreamThreadHandler.obtainMessage(cl_action, x, y).sendToTarget();
            }
        } else if (maskedAction == MotionEvent.ACTION_MOVE) {
            Log.d("debug", "action move");
            for (int i = 0; i < pointerCount; i++) {
                int pointId = e.getPointerId(i);
                x = (int) e.getX(i);
                y = (int) e.getY(i);
                addPointer(pointId, x, y);

                if (pointId == 0) {
                    cl_action = Constants.CLEV_MOUSE_MOVE_AUX_0;
                } else if (pointId == 1) {
                    cl_action = Constants.CLEV_MOUSE_MOVE_AUX_1;
                } else if (pointId == 2) {
                    cl_action = Constants.CLEV_MOUSE_MOVE_AUX_2;
                }
                Log.d("debug", "Sending :: MOVE :: " + cl_action + " through the stream...");
                mStreamThreadHandler.obtainMessage(cl_action, x, y).sendToTarget();
            }
        } else {
            Log.d("debug", "no case triggered for multitouch");
            return true;
        }

        mStreamThreadHandler.obtainMessage(cl_action, x, y).sendToTarget();
        return true;
    }

    /**
     * Handles a single pointer in the form of a passed in MotionEvent
     *
     * @param e MotionEvent registered from android
     * @return
     */
    public boolean handleSinglePointer(MotionEvent e) {

        int x = (int) e.getX();
        int y = (int) e.getY();
        int action = e.getAction();
        int cl_action = 0;

        if (action == MotionEvent.ACTION_DOWN) {
            cl_action = Constants.CLEV_MOUSE_DOWN;
        } else if (action == MotionEvent.ACTION_UP) {
            cl_action = Constants.CLEV_MOUSE_UP;
        } else if (action == MotionEvent.ACTION_MOVE) {
            cl_action = Constants.CLEV_MOUSE_MOVE;
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
