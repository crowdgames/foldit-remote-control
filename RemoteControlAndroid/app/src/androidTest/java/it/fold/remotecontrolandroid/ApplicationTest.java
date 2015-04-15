package it.fold.remotecontrolandroid;

import android.app.Application;
import android.graphics.PointF;
import android.os.SystemClock;
import android.test.ApplicationTestCase;
import android.view.MotionEvent;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    @Mock public Context _context;

    @Mock public AttributeSet _attributeSet;

    public StreamView _streamView;

    public long downTime1;
    public long downTime2;
    public long downTime3;
    public long eventTime1;
    public long eventTime2;
    public long eventTime3;

    public MotionEvent _motionEventDownOne;
    public MotionEvent _motionEventDownTwo;
    public MotionEvent _motionEventDownThree;

    /**
     * @Author Conor Ebbs
     */
    public ApplicationTest() {
        super(Application.class);

    }

    @Override
    public void setUp() {
        System.setProperty("dexmaker.dexcache", "/sdcard");
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());

        MockitoAnnotations.initMocks(this);
        _streamView = new StreamView(_context, _attributeSet);
    }

    public void testStatesAfterInit() {
        _streamView.hashCode();

//        System.out.println("test");
    }

    public void testOnTouchEvent() {
        //Create first motionEvent
        downTime1 = SystemClock.uptimeMillis();
        eventTime1 = SystemClock.uptimeMillis();
        _motionEventDownOne = MotionEvent.obtain(downTime1, eventTime1, MotionEvent.ACTION_DOWN, 100, 200, 0);
        _streamView.onTouchEvent(_motionEventDownOne);
        //Check that it exists
        assertEquals(1, _streamView._pointers.size());
        //Create second motionEvent
        downTime2 = SystemClock.uptimeMillis();
        eventTime2 = SystemClock.uptimeMillis();
        _motionEventDownTwo = MotionEvent.obtain(downTime2, eventTime2, MotionEvent.ACTION_DOWN, 500, 500, 0);
        _streamView.onTouchEvent(_motionEventDownTwo);
        //Check that it exists
        assertEquals(2, _streamView._pointers.size());
        //Move first motionEvent
        eventTime1 = SystemClock.uptimeMillis();
        _motionEventDownOne = MotionEvent.obtain(downTime1, eventTime1, MotionEvent.ACTION_MOVE, 1200, 300, 0);
        _streamView.onTouchEvent(_motionEventDownOne);
        //Check that it moved
        assertEquals(1200, _streamView._pointers.get(1).x);
        assertEquals(300, _streamView._pointers.get(1).y);
        //Create third motionEvent
        downTime3 = SystemClock.uptimeMillis();
        eventTime3 = SystemClock.uptimeMillis();
        _motionEventDownTwo = MotionEvent.obtain(downTime3, eventTime3, MotionEvent.ACTION_DOWN, 300, 100, 0);
        _streamView.onTouchEvent(_motionEventDownThree);
        //Check that it was added
        assertEquals(3, _streamView._pointers.size());
        //Remove second motionEvent
        eventTime2 = SystemClock.uptimeMillis();
        _motionEventDownTwo = MotionEvent.obtain(downTime2, eventTime2, MotionEvent.ACTION_UP, 500, 500, 0);
        _streamView.onTouchEvent(_motionEventDownTwo);
        //Assert there are two pointers
        assertEquals(2, _streamView._pointers.size());
    }

}