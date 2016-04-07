package it.fold.remotecontrolandroid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
* Class for activities using the action bar library
*/
public class GameActivity extends Activity implements KeyEvent.Callback, GameView{
    //gets the invisible textbox we send keyboard activity to
    //
    EditText textInput;
    StreamView sview;
    final String[] data ={"one"};
    DrawerLayout drawer;
    @Override
    /**
    * initializes based off of Bundle
    *B
    * @param Bundle savedInstanceState parseable strings used for init
    */
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_game);
        sview = (StreamView) findViewById(R.id.tempView);
        textInput = (EditText) findViewById(R.id.editText);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) findViewById(R.id.drawer);
        navList.setAdapter(new DrawerListAdapter(this,data,this));



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
        drawer.closeDrawers();
    }

    //sends CLEV_MODKEY_DOWN info 0, CLEV_MODKEY_UP info 2, this sets control down, shift up
    public void onRightClickButton(View v)
    {
        //StreamView view = (StreamView) v;
        sview.OnViewEvent(Constants.CLEV_MODKEY_DOWN, '0');
        sview.OnViewEvent(Constants.CLEV_MODKEY_UP, '2');
        drawer.closeDrawers();
    }

    //sends CLEV_MODKEY_UP info 0, CLEV_MODKEY_DOWN info 2, this sets control up, shift down
    public void onMiddleClickButton(View v)
    {
        //StreamView view = (StreamView) v;
        sview.OnViewEvent(Constants.CLEV_MODKEY_UP, '0');
        sview.OnViewEvent(Constants.CLEV_MODKEY_DOWN, '2');
        drawer.closeDrawers();
    }

    //brings up the keyboard when pressed, records chars to et
    public void bringUpKeyboard(View v)
    {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0,0);

    }


}
