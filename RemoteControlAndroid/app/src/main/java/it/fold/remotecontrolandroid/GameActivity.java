package it.fold.remotecontrolandroid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
* Class for activities using the action bar library
*/
public class GameActivity extends ActionBarActivity {

    @Override
    /**
    * initializes based off of Bundle
    *
    * @param Bundle savedInstanceState parseable strings used for init
    */
    protected void onCreate(Bundle savedInstanceState)
    {
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
}
