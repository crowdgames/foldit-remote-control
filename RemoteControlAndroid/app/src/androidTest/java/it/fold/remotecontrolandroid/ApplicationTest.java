package it.fold.remotecontrolandroid;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
  public class ApplicationTest {
    public final String TAG = "ApplicationTest";
    @Rule
    public ActivityTestRule<LoginIPActivity> loginIPActivityActivityTestRule = new ActivityTestRule(LoginIPActivity.class);

    @Test
    public void ensureTextChangesWork() {
        //Log.d(TAG, "test run!!");
        // Type text and then press the button.
        onView(withId(R.id.ip))
                .perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.IP_sign_in_button)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.ip)).check(matches(withText("123")));
    }
}