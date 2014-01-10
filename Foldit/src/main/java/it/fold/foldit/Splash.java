package it.fold.foldit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by jeffpyke on 7/19/13.
 *  Displays a foldit splash screen.
 * CURRENTLY UNUSED
 */
public class Splash extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 650;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}