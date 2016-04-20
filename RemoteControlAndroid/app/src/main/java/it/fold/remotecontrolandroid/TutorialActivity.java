package it.fold.remotecontrolandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
* Class for displaying an interactive tutorial on instructions for game setup
*/
public class TutorialActivity extends Activity {

    final String TAG = TutorialActivity.class.getCanonicalName();
    final String DownLoadInstructions = "Visit https://fold.it\n" +
            "Click the link 'Are you new to Foldit? Click here.'\n" +
            "Follow the instructions on the next page.";

    private boolean mdesktopVersionDownloadButton = false;
    private boolean mdesktopVersionSetupButton = false;
    private boolean mdesktopVersionConnectButton = false;

    @Override
    /**
     * initializes based off of Bundle
     *B
     * @param Bundle savedInstanceState parseable strings used for init
     */
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Tutorial Activity created");
        setContentView(R.layout.activity_tutorial);
        super.onCreate(savedInstanceState);

        final View desktopDownload = findViewById(R.id.download_desktop_version_textview);
        final View desktopSetup = findViewById(R.id.desktop_setup_layout);
        final View connectFRC = findViewById(R.id.connect_FRC_layout);




        Button mDownloadDesktopVersion = (Button)findViewById(R.id.downloadDesktopVersionButton);
        mDownloadDesktopVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdesktopVersionDownloadButton = !mdesktopVersionDownloadButton;
                TextView downloadDesktopVersionTutorial = (TextView) findViewById(R.id.download_desktop_version_textview);
                downloadDesktopVersionTutorial.setText(DownLoadInstructions);

                if(mdesktopVersionDownloadButton){
                    desktopDownload.setVisibility(View.VISIBLE);
                    desktopSetup.setVisibility(View.GONE);
                    connectFRC.setVisibility(View.GONE);

                }
                else {
                    desktopDownload.setVisibility(View.GONE);
                    desktopSetup.setVisibility(View.GONE);
                    connectFRC.setVisibility(View.GONE);
                }
            }
        });

        Button mDesktopSettings = (Button)findViewById(R.id.desktopVersionSettingsButton);
        mDesktopSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdesktopVersionSetupButton = !mdesktopVersionSetupButton;

                if(mdesktopVersionSetupButton){
                    desktopDownload.setVisibility(View.GONE);
                    desktopSetup.setVisibility(View.VISIBLE);
                    connectFRC.setVisibility(View.GONE);
                }
                else {
                    desktopDownload.setVisibility(View.GONE);
                    desktopSetup.setVisibility(View.GONE);
                    connectFRC.setVisibility(View.GONE);
                }


            }
        });

        Button mConnectFRC = (Button)findViewById(R.id.connectionSettingsButton);
        mConnectFRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdesktopVersionConnectButton = !mdesktopVersionConnectButton;

                if(mdesktopVersionConnectButton){
                    desktopDownload.setVisibility(View.GONE);
                    desktopSetup.setVisibility(View.GONE);
                    connectFRC.setVisibility(View.VISIBLE);

                }
                else {
                    desktopDownload.setVisibility(View.GONE);
                    desktopSetup.setVisibility(View.GONE);
                    connectFRC.setVisibility(View.GONE);
                }
            }
        });
    }

}
