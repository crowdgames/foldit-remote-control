package it.fold.remotecontrolandroid;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
/**
 * @author - Ruta
 */
@RunWith(AndroidJUnit4.class)
  public class LoginIPActivityActivityTest {
    public final String TAG = "LoginIPActivityActivityTest";
    @Rule
    public ActivityTestRule<LoginIPActivity> loginIPActivityActivityTestRule = new ActivityTestRule(LoginIPActivity.class);

    @Test
    public void ensureIPAddressAcceptsInput() {
        // Type text and then press the button.
        onView(withId(R.id.ip))
                .perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.IP_sign_in_button)).perform(click());
    }
    @Test
    public  void ensureIPAddressChanges(){
        // Check that the text was changed.
        onView(withId(R.id.ip))
                .perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.IP_sign_in_button)).perform(click());
        onView(withId(R.id.ip)).check(matches(withText("123")));
        Log.d(TAG, "test - LoginIPActivity accepts IP as numeric values");
    }

    @Test
    public  void ensureOptionalPasswordChanges(){
        // Check that the text was changed.
        onView(withId(R.id.password))
                .perform(typeText("qwerty"), closeSoftKeyboard());
        onView(withId(R.id.IP_sign_in_button)).perform(click());
        onView(withId(R.id.password)).check(matches(withText("qwerty")));
        Log.d(TAG, "test - LoginIPActivity accepts password");
    }
}