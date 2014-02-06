package it.fold.foldit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ViewTarget;

import it.fold.foldit.StreamView.StreamThread;
//import java.awt.*;
//import java.awt.image.*;


public class GameActivity extends Activity {
    private ProgressBar progressBar;
    private ResponseReceiver receiver;
    private StreamThread mStreamThread;
    private StreamView mStreamView;
    private static GameActivity display;
    boolean loaded = false;
    public static boolean ctrlDown = false;
    private boolean shiftDown = false;
    private boolean keyboardShown = false;
    public static Toast toast;
    public static Toast outToast;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private ShowcaseView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = this;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Constants.CUR_IMG_HEIGHT = metrics.heightPixels - metrics.heightPixels % Constants.TILE_SIZE;
            Constants.CUR_IMG_WIDTH = metrics.widthPixels - metrics.widthPixels % Constants.TILE_SIZE;
        } else {
            finish();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Constants.REAL_IMG_HEIGHT = Constants.CUR_IMG_HEIGHT;
        Constants.REAL_IMG_WIDTH = Constants.CUR_IMG_WIDTH;
        // if low res enabled streamview will halve cur width and height
        setContentView(R.layout.activity_game);
        mStreamView = (StreamView) findViewById(R.id.stream);


        mStreamThread = mStreamView.getThread();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Intent intent = this.getIntent();
        String address = intent.getStringExtra("address");
        int port = intent.getIntExtra("port", Constants.PORT);
        String key = intent.getStringExtra("key");
        mStreamThread.doStart(address, port, key);
        int tid = android.os.Process.myTid();
        //Log.d("streamdebug", "oncreate thread id: " + tid);
    }
    public void shiftClick(View view) {
        if (!shiftDown) {
            mStreamView.streamHandler.obtainMessage(Constants.CLEV_MODKEY_DOWN, 2 * 128, 0).sendToTarget(); // 128 to compensate for (see streamview handlemessage)
            ((ImageView) view).setImageResource(R.drawable.shiftlit);
        } else {
            mStreamView.streamHandler.obtainMessage(Constants.CLEV_MODKEY_UP, 2 * 128, 0).sendToTarget();
            ((ImageView) view).setImageResource(R.drawable.shift);
        }
        shiftDown = !shiftDown;

    }
    public void ctrlClick(View view) {
        if (!ctrlDown) {
            mStreamView.streamHandler.obtainMessage(Constants.CLEV_MODKEY_DOWN, 0, 0).sendToTarget();
            ((ImageView) view).setImageResource(R.drawable.ctrllit);
        } else {
            mStreamView.streamHandler.obtainMessage(Constants.CLEV_MODKEY_UP, 0, 0).sendToTarget();
            ((ImageView) view).setImageResource(R.drawable.ctrl);
        }
        ctrlDown = !ctrlDown;
    }
    public void keyboardClick(View view) {
        if (keyboardShown) {
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        keyboardShown = !keyboardShown;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (!loaded) {
//            toast = Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT);
//            toast.show();
//        }

        // Messages to show if a connection is not made by SocketBuffer
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 5s = 5000ms
//                if ((!loaded && mStreamThread != null ) || !mStreamThread.connected()) {
//                    toast = Toast.makeText(getApplicationContext(), "Waiting for connection...", Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//        }, 3000);
    }
    @Override
    public void onRestart() {
        super.onRestart();
        finish(); // quit to main menu on restart
    }
    public void cancelThread() {
        if (mStreamThread != null) {
            mStreamThread.setRunning(false);
            try {
                mStreamThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static GameActivity instance() {
        return display;
    }
    public void setLoadingDone() {
        ProgressBar p = (ProgressBar) findViewById(R.id.progressBar);
        p.setVisibility(View.GONE);
        //toast.cancel();
        loaded = true;
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (!myPrefs.contains("gameTutorialFinished")) {
            ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
            co.hideOnClickOutside = true;
            co.shotType = ShowcaseView.TYPE_ONE_SHOT;
            ViewTarget target = new ViewTarget(findViewById(R.id.shiftImage));
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTutorial();
                }
            };
            sv = ShowcaseView.insertShowcaseView(target, this, "Modifier Keys", "These buttons toggle the Ctrl and Shift modifier keys.", co);
            sv.overrideButtonClick(clickListener);
        }
    }
    public void showTutorial() {
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        ViewTarget target = new ViewTarget(findViewById(R.id.keyboardImage));
        sv.setText("Keyboard Input", "Tap here to open the keyboard.");
        sv.setShowcase(target, true);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShowcaseView) view.getParent()).hide();
            }
        };
        sv.overrideButtonClick(clickListener);
        myPrefs.edit().putString("gameTutorialFinished", "true").commit();
    }
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.mamlambo.intent.action.MESSAGE_PROCESSED";
        @Override
        public void onReceive(Context context, Intent intent) {
            outToast = Toast.makeText(getApplicationContext(), intent.getStringExtra("msg"), Toast.LENGTH_LONG);
            outToast.show();
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor = detector.getScaleFactor();
//            //synchronized (events) {
//            if (mScaleFactor < 0.99) {
//                //events.add(new TouchEvent(0, 0, Constants.CLEV_SCROLL_DOWN));
//                return true;
//            } else if (mScaleFactor > 1.01) {
//                //events.add(new TouchEvent(0, 0, Constants.CLEV_SCROLL_UP));
//                return true;
//            }
//            // }
//            return false;
//        }
//    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        //toast.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
    }
    @Override
    public void onStop() {
        super.onStop();
        cancelThread();
    }

    
}
