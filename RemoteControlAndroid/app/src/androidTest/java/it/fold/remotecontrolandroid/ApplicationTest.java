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

    // to be used for testing our application
    @Mock
    public StreamView _streamView;

    // to be used for testing our application
    @Mock
    public GameActivity _gameActivity;

    // to be used for testing our application
    @Mock
    public SocketBuffer _socketBuffer;

    // to be used for testing our application
    @Mock
    public StreamThread _streamThread;

    /**
     * @Author Conor Ebbs
     */
    public ApplicationTest() {
        super(Application.class);

    }


    /**
     * Init the mocks that we will use to test our application
     *
     * @Author Conor Ebbs
     */
    @Before
    public void initMocks() {

        MockitoAnnotations.initMocks(this);
//          Not necessary, all being done by the annotations
//        _streamView = mock(StreamView.class);
//        _gameActivity = mock(GameActivity.class);
//        _socketBuffer = mock(SocketBuffer.class);
//        _streamThread = mock(StreamThread.class);
    }

    @Before
    public void applicationSetUp() {
        System.setProperty("dexmaker.dexcache", "/sdcard");
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
    }

    @Test
    public void testStatesAfterInit() {
        _streamView.hashCode();
        _streamThread.hashCode();
        _gameActivity.hashCode();
        _socketBuffer.hashCode();

//        System.out.println("test");
    }

}