package it.fold.foldit;

//import android.app.ActionBar;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.lang.Override;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.view.LayoutInflater;
import android.view.View;
import android.content.*;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends SherlockFragmentActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    //private MenuListAdapter mMenuAdapter;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPageTitles;
    private int currentItem;
    //AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    Fragment fragment1 = new MainSectionFragment();
    Fragment fragment2 = new HelpSectionFragment();
    Fragment fragment3 = new AboutSectionFragment();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setContentView(R.layout.activity_main_drawers);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Constants.CUR_IMG_HEIGHT = metrics.heightPixels - metrics.heightPixels % Constants.TILE_SIZE;
            Constants.CUR_IMG_WIDTH = metrics.widthPixels - metrics.widthPixels % Constants.TILE_SIZE;
        } else {
            Constants.CUR_IMG_WIDTH = metrics.heightPixels - metrics.heightPixels % Constants.TILE_SIZE;
            Constants.CUR_IMG_HEIGHT = metrics.widthPixels - metrics.widthPixels % Constants.TILE_SIZE;
        }
        Constants.REAL_IMG_HEIGHT = Constants.CUR_IMG_HEIGHT;
        Constants.REAL_IMG_WIDTH = Constants.CUR_IMG_WIDTH;
        Log.d("jeff", "Height: "+ Constants.CUR_IMG_HEIGHT + " Width: " + Constants.CUR_IMG_WIDTH);

        mTitle = mDrawerTitle = getTitle();
        mPageTitles = getResources().getStringArray(R.array.pages_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPageTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // enable ActionBar app icon to behave as action to toggle nav drawer

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {

                getSupportActionBar().setTitle(mTitle);

                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {

                getSupportActionBar().setTitle(mDrawerTitle);

                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        /* Show the drawer upon startup if it has never been used before to teach the user how to use the app
        * Once the Drawer has been opened once manually "drawerHasBeenUsed" will be added to the preferences */
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (!myPrefs.contains("drawerHasBeenUsed")) {
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        switch(item.getItemId()){
            case android.R.id.home :
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                    SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    if (!myPrefs.contains("drawerHasBeenUsed")) {
                        myPrefs.edit().putString("drawerHasBeenUsed", "true").commit(); // Open the drawer on first install of app only
                    }
                }
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, settingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        if (position == 3) { // Wiki
            mDrawerList.setItemChecked(currentItem, true);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://foldit.wikia.com/wiki/Foldit_Wiki"));
            startActivity(browserIntent);
            return;
        }
        currentItem = position;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (position == 0) { // Play Foldit
            ft.replace(R.id.content_frame, fragment1);
        } else if (position == 1) { // Help
            ft.replace(R.id.content_frame, fragment2);
        } else if (position == 2) { // About
            ft.replace(R.id.content_frame, fragment3);
        }
        ft.commit();
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPageTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    // Link to the foldit mainpage to download foldit
    public void linkClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://fold.it"));
        startActivity(intent);
    }

    public static class MainSectionFragment extends SherlockFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_section_main, container, false);
            EditText add = (EditText) rootView.findViewById(R.id.editAddress);
            //EditText port = (EditText) rootView.findViewById(R.id.editPort);
            EditText key = (EditText) rootView.findViewById(R.id.editKey);
            add.setImeOptions(EditorInfo.IME_ACTION_DONE);
            key.setImeOptions(EditorInfo.IME_ACTION_DONE);
            SharedPreferences myPrefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
            add.setText(myPrefs.getString("address", ""));
            key.setText(myPrefs.getString("key", ""));

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            rootView.findViewById(R.id.button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startFoldit(rootView);
                        }
                    });

            return rootView;
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }

        public void startFoldit(View view) {
            if (isOnline()) {
                EditText add = (EditText) view.findViewById(R.id.editAddress);
                EditText key = (EditText) view.findViewById(R.id.editKey);
                String myPort = Constants.PORT + "";
                String myKey = key.getText().toString();

                String myAddress = add.getText().toString();
                if (myAddress.equals("") || !(Pattern.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$", myAddress) || Pattern.matches("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$", myAddress))) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Enter a valid IP address.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (!myKey.equals("") && myKey.length() != Constants.KEY_LENGTH) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Key should be " + Constants.KEY_LENGTH + " characters.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                SharedPreferences myPrefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
                // "128.95.2.85";
                SharedPreferences.Editor e = myPrefs.edit();

                e.putString("address", myAddress);
                e.putString("key", myKey);

                if (!myPort.equals("1230")) {
                    e.putString("port", myPort);
                } else {
                    e.putString("port", "");
                }
                e.commit();

                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("address", myAddress);
                intent.putExtra("port", myPort);
                intent.putExtra("key", myKey);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You are not connected to the internet.", Toast.LENGTH_SHORT);
                toast.show();
            }


        }

    }

    public static class HelpSectionFragment extends SherlockFragment {
        public HelpSectionFragment() {
            // empty
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_help, container, false);
            Bundle args = getArguments();
            return rootView;
        }
    }
    public static class AboutSectionFragment extends Fragment {
        public AboutSectionFragment() {
            // empty
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_about, container, false);
            Bundle args = getArguments();
            TextView vers = (TextView) rootView.findViewById(R.id.version);
            vers.setText(vers.getText() + getResources().getString(R.string.version));
            ImageView img = (ImageView)rootView.findViewById(R.id.logo);
            img.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) { // Go to the website when clicked
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://fold.it"));
                    startActivity(intent);
                }
            });
            return rootView;
        }
    }

}
