package it.fold.remotecontrolandroid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
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

    @Before
    public void setUp() throws Exception {
        presenter = new LoginIPPresenter(view);

    }

    @Test
    public void shouldShowErrorMessageWhenIPFieldIsEmpty() throws Exception {
        when(view.getIPAddress()).thenReturn("");
        presenter.attemptLogin();

        verify(view).showIPError(R.string.error_field_required);
    }

    @Test
    public void shouldShowErrorMessageWhenIPFieldIsInvalid() throws Exception {
        when(view.getIPAddress()).thenReturn("");
        presenter.attemptLogin();

        verify(view).showIPError(R.string.error_field_required);
    }

    @Test
    public void shouldShowErrorMessageWhenOptionalKeyFieldIsInvalid() throws Exception {

    }
}