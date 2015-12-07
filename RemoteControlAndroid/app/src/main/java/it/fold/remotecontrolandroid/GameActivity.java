package it.fold.remotecontrolandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
* Class for activities using the action bar library
*/
public class GameActivity extends Activity implements KeyEvent.Callback{
    //gets the invisible textbox we send keyboard activity to
    //
    EditText textInput;
    StreamView sview;
    @Override
    /**
    * initializes based off of Bundle
    *B
    * @param Bundle savedInstanceState parseable strings used for init
    */
    protected void onCreate(Bundle savedInstanceState)
    {
        sview = (StreamView) findViewById(R.id.streamView);
        textInput = (EditText) findViewById(R.id.editText);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }


    @Override
    /**
    * initializes the options menu
    *
    * @param Menu menu information for the options menu
    * @return boolean true
    */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    /**
    * whenever an option is selected, handles it
    *
    * @param MenuItem item that has been selected
    * @return boolean true
    */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //sends CLEV_MODKEY_UP info 0,CLEV_MODKEY_UP info 2, this sets both control and shift up
    public void onLeftClickButton(View v)
    {
        //StreamView view = (StreamView) v;
        sview.OnViewEvent(Constants.CLEV_MODKEY_UP, '0');
        sview.OnViewEvent(Constants.CLEV_MODKEY_UP, '2');
    }

    //sends CLEV_MODKEY_DOWN info 0, CLEV_MODKEY_UP info 2, this sets control down, shift up
    public void onRightClickButton(View v)
    {
        //StreamView view = (StreamView) v;
        sview.OnViewEvent(Constants.CLEV_MODKEY_DOWN, '0');
        sview.OnViewEvent(Constants.CLEV_MODKEY_UP, '2');
    }

    //sends CLEV_MODKEY_UP info 0, CLEV_MODKEY_DOWN info 2, this sets control up, shift down
    public void onMiddleClickButton(View v)
    {
        //StreamView view = (StreamView) v;
        sview.OnViewEvent(Constants.CLEV_MODKEY_UP, '0');
        sview.OnViewEvent(Constants.CLEV_MODKEY_DOWN, '2');
    }

    //brings up the keyboard when pressed, records chars to et
    public void bringUpKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textInput, InputMethodManager.SHOW_IMPLICIT);
    }

    //override the key listener to
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        //view.OnViewEvent(Constants.CLEV_CHAR, (char) keyCode);
        return true;
    }




}
