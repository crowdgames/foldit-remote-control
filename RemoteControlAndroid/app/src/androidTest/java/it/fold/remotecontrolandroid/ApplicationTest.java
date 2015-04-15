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

    public MotionEvent _motionEventDownOne = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100, 200, 0);
    public MotionEvent _motionEventDownTwo = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 500, 500, 0);
    public MotionEvent _motionEventDownThree = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 300, 100, 0);

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

    public void testStatesAfterInit() {
        _streamView.hashCode();

//        System.out.println("test");
    }

    public void testOnTouchEventSingle() {
        assertTrue(_streamView.onTouchEvent(_motionEventDownOne)); // Should not fail
        _streamView.onTouchEvent(_motionEventDownOne);
        //Assert there is one pointer
        _streamView.onTouchEvent(_motionEventDownTwo);
        //Assert there are two pointers
        //Move _motionEventDownOne
        //Assert _motionEventDownOne moved
        _streamView.onTouchEvent(_motionEventDownThree);
        //Assert there are three pointers
        //Remove _motionEventDownTwo
        //Assert there are two pointers
    }

}