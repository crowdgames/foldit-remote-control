package it.fold.remotecontrolandroid;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Handles events and imaging
 */
public class  StreamView extends SurfaceView implements SurfaceHolder.Callback {
    private StreamThread mStreamThread;
    /**
     * object to handle streaming
     */
    private Handler mStreamThreadHandler;

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

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

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d("surface width", Integer.toString(width));
        Log.d("surface height", Integer.toString(height));
        //Constants.CUR_IMG_HEIGHT = height - height % Constants.TILE_SIZE;
        //Constants.CUR_IMG_WIDTH = width - width % Constants.TILE_SIZE;
        Constants.CUR_IMG_HEIGHT = height / Constants.SCALE;
        Constants.CUR_IMG_WIDTH = width / Constants.SCALE;
        holder.setFixedSize(Constants.CUR_IMG_WIDTH, Constants.CUR_IMG_HEIGHT);
        Constants.REAL_IMG_HEIGHT = Constants.CUR_IMG_HEIGHT;
        Constants.REAL_IMG_WIDTH = Constants.CUR_IMG_WIDTH;

        Paint paint = new Paint();
        paint.setColor(0xFFFFFFFF);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0xFFFF0000);
        canvas.drawLine(0, 0, 100, 100, paint);
        holder.unlockCanvasAndPost(canvas);

        mStreamThread.initialize(Constants.IP_ADDRESS, Constants.PORT, "");
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

        int x = (int) e.getX() / 2;
        int y = (int) e.getY() / 2;
        int action = e.getAction();

        int cl_action = 0;

        int pointerIndex = e.getActionIndex();
        int pointerId = e.getPointerId(pointerIndex);

        if (pointerId == 0) {
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
        } else if (pointerId <= 255) {
            if (action == MotionEvent.ACTION_DOWN) {
                cl_action = Constants.CLEV_AUX_PTR_DOWN;
            } else if (action == MotionEvent.ACTION_UP) {
                cl_action = Constants.CLEV_AUX_PTR_UP;
            } else if (action == MotionEvent.ACTION_MOVE) {
                cl_action = Constants.CLEV_AUX_PTR_MOVE;
            } else {
                return true;
            }
        } else {
            return true;
        }

        mStreamThreadHandler.obtainMessage(cl_action, x, y, new Character((char)pointerId)).sendToTarget();
        return true;
    }

    public boolean OnViewEvent(int cl_action, char character){
        mStreamThreadHandler.obtainMessage(cl_action, 0, 0, character).sendToTarget();
        return true;
    }

    //listen for key events, send them to the handler
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mStreamThreadHandler.obtainMessage(Constants.CLEV_CHAR, event.getUnicodeChar()).sendToTarget();
        return super.onKeyDown(keyCode, event);
    }
}
