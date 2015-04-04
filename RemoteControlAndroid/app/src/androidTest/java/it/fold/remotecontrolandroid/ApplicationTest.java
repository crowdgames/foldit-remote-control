package it.fold.remotecontrolandroid;

import android.app.Application;
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

    @Mock public GameActivity _gameActivity;

    @Mock public SocketBuffer _socketBuffer;

    @Mock public StreamThread _streamThread;

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
        _streamThread.hashCode();
        _gameActivity.hashCode();
        _socketBuffer.hashCode();

//        System.out.println("test");
    }

}