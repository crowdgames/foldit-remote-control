package it.fold.remotecontrolandroid;

import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Dhruv on 3/21/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginIPActivityTest {

    private LoginIPPresenter presenter;
    @Mock
    private LoginView view;
    @Mock
    private Button mIPSignInButton;

    @Before
    public void setUp() throws Exception {
        presenter = new LoginIPPresenter(view);

    }

    @Test
    public void shouldShowErrorMessageWhenIPFieldIsEmpty() throws Exception {
        when(view.getIPAddress()).thenReturn("");
        presenter.attemptLogin();

        verify(view).showIPEmptyError(R.string.error_field_required);
    }

    @Test
    public void shouldShowErrorMessageWhenIPFieldIsDoesNotHaveAllNumbers() throws Exception {
        when(view.getIPAddress()).thenReturn("1233aa");
        presenter.attemptLogin();

        verify(view).showIpInvalidError(R.string.error_invalid_IP);
    }
    @Test
    public void shouldShowErrorMessageWhenIPFieldIsDoesNotHaveFourParts() throws Exception {
        when(view.getIPAddress()).thenReturn("123.123.213.123.123");
        presenter.attemptLogin();

        verify(view).showIpInvalidError(R.string.error_invalid_IP);
    }

    @Test
    public void shouldShowErrorMessageWhenIPFieldIsDoesNotHaveNumbersInCorrectRange() throws Exception {
        when(view.getIPAddress()).thenReturn("123.267.213.123");
        presenter.attemptLogin();

        verify(view).showIpInvalidError(R.string.error_invalid_IP);
    }

    @Test
    public void shouldShowErrorMessageWhenIPFieldIsEndsWithADot() throws Exception {
        when(view.getIPAddress()).thenReturn("123.267.213.123.");
        presenter.attemptLogin();

        verify(view).showIpInvalidError(R.string.error_invalid_IP);
    }



    @Test
    public void shouldShowErrorMessageWhenOptionalKeyFieldIsInvalid() throws Exception {
        when(view.getPassword()).thenReturn("123");
        presenter.attemptLogin();

        verify(view).showPasswordInvalidError(R.string.error_invalid_password);
    }
    @Test
    public void shouldStartGameActivityOnSuccessfulLogin() throws Exception {
        when(view.getIPAddress()).thenReturn("192.168.0.18");
        when(view.getPassword()).thenReturn("");
        presenter.attemptLogin();

        verify(view).attemptLoginTask("192.168.0.18", "");
    }

    @Test
    public void testPassword_rainyDay(){
        assertEquals("Password length should be greater than 4",false,view.isPasswordValid("fol"));
    }


}