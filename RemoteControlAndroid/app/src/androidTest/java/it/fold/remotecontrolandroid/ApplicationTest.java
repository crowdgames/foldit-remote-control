package it.fold.remotecontrolandroid;

import android.app.Application;
import android.os.SystemClock;
import android.test.ApplicationTestCase;
import android.view.MotionEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.Socket;

import static org.mockito.Mockito.*;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    @Mock public StreamView _streamView;

    public MotionEvent _motionEventDownOne;
    public MotionEvent _motionEventUpOne;
    public MotionEvent _motionEventMoveOne;

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
    }

    public void setUpMotionEventDown() {
        _motionEventDownOne.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100, 100, _motionEventDownOne.getMetaState());
        _motionEventUpOne.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 100, 100, _motionEventUpOne.getMetaState());
        _motionEventMoveOne.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 100, 100, _motionEventMoveOne.getMetaState());
    }

    public void testStatesAfterInit() {
        _streamView.hashCode();

//        System.out.println("test");
    }

    public void testOnTouchEventSingle() {
        assertTrue(_streamView.onTouchEvent(_motionEventDownOne));
        assertTrue(_streamView.onTouchEvent(_motionEventUpOne));

    }

}