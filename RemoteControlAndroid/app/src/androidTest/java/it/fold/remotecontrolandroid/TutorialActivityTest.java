package it.fold.remotecontrolandroid;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by rutagadgil on 4/3/16.
 */
@RunWith(AndroidJUnit4.class)
public class TutorialActivityTest {
    final String TAG = TutorialActivityTest.class.getCanonicalName();

    final String DownLoadInstructions = "Visit https://fold.it\n" +
            "Click the link 'Are you new to Foldit? Click here.'\n" +
            "Follow the instructions on the next page.";
    final String step1 = "Select the menu option on the lower left corner and Select Puzzle Menu";
    final String step2 = "Select Science Puzzle";
    final String step3 = "Select Puzzle of your choice and click Play";
    final String step4 = "Click on the Social tab option";
    final String step5 = "Click on Remote Control";
    final String step6a = "Enter the Local IP on your Android device";
    final String step6b = "You can also generate an optional key by selecting the" +
            " require key option and enter the Local IP and the key on your android" +
            " device to connect to a server";

    @Rule
    public ActivityTestRule<TutorialActivity> tutorialActivityTestRule = new ActivityTestRule(TutorialActivity.class);

    @Test
    public void ensureDownloadDesktopButtonDisplaysCorrectText(){
        onView(withId(R.id.downloadDesktopVersionButton)).perform(click());

        onView(withId(R.id.download_desktop_version_textview)).check(matches(withText(DownLoadInstructions)));
        Log.d(TAG, "test - TutorialActivity : Download Desktop Version button displays correct information");
    }

    @Test
    public void ensureDesktopVersionSettingsButtonDisplaysStep1(){
        onView(withId(R.id.desktopVersionSettingsButton)).perform(click());
        onView(withId(R.id.setup_tutorial_step1)).check(matches(withText(step1)));
        Log.d(TAG, "test - TutorialActivity : Desktop Version Settings button displays step1");
    }

    @Test
    public void ensureDesktopVersionSettingsButtonDisplaysStep2(){
        onView(withId(R.id.desktopVersionSettingsButton)).perform(click());
        onView(withId(R.id.setup_tutorial_step2)).check(matches(withText(step2)));
        Log.d(TAG, "test - TutorialActivity : Desktop Version Settings button displays step2");
    }

    @Test
    public void ensureDesktopVersionSettingsButtonDisplaysStep3(){
        onView(withId(R.id.desktopVersionSettingsButton)).perform(click());
        onView(withId(R.id.setup_tutorial_step3)).check(matches(withText(step3)));
        Log.d(TAG, "test - TutorialActivity : Desktop Version Settings button displays step3");
    }

    @Test
    public void ensureDesktopVersionSettingsButtonDisplaysStep4(){
        onView(withId(R.id.desktopVersionSettingsButton)).perform(click());
        onView(withId(R.id.setup_tutorial_step4)).check(matches(withText(step4)));
        Log.d(TAG, "test - TutorialActivity : Desktop Version Settings button displays step4");
    }

    @Test
    public void ensureDesktopVersionSettingsButtonDisplaysStep5(){
        onView(withId(R.id.desktopVersionSettingsButton)).perform(click());
        onView(withId(R.id.setup_tutorial_step5)).check(matches(withText(step5)));
        Log.d(TAG, "test - TutorialActivity : Desktop Version Settings button displays step5");
    }

    @Test
    public void ensureDesktopVersionSettingsButtonDisplaysStep6a(){
        onView(withId(R.id.connectionSettingsButton)).perform(click());
        onView(withId(R.id.setup_tutorial_step6a)).check(matches(withText(step6a)));
        Log.d(TAG, "test - TutorialActivity : Desktop Version Settings button displays step6a");
    }

    @Test
    public void ensureDesktopVersionSettingsButtonDisplaysStep6b(){
        onView(withId(R.id.connectionSettingsButton)).perform(click());
        onView(withId(R.id.setup_tutorial_step6b)).check(matches(withText(step6b)));
        Log.d(TAG, "test - TutorialActivity : Desktop Version Settings button displays step6b");
    }
}
