package it.fold.remotecontrolandroid;

import android.os.Bundle;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Dhruv on 4/8/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class GameActivityTest {
    @Mock
    private GameView view;

    @Mock
    private View v;
    private GamePresenter presenter;

    @Before
    public void setUp() throws Exception {

        presenter = new GamePresenter(view);
    }


    @Test
    public void shouldCloseDrawerWhenLeftButtonClicked() throws Exception {
        presenter.onLeftClickButton();
        verify(view).closeDrawer();
    }

    @Test
    public void shouldCloseDrawerWhenRightButtonClicked() throws Exception {
        presenter.onRightClickButton();
        verify(view).closeDrawer();
    }

    @Test
    public void shouldCloseDrawerWhenMiddleButtonClicked() throws Exception {
        presenter.onMiddleClickButton();
        verify(view).closeDrawer();
    }

    @Test
    public void shouldShowToastMessageWhenMiddleButtonClicked() throws Exception {
        presenter.onMiddleClickButton();
        verify(view).showToastForMiddleClickButton();
    }

    @Test
    public void shouldShowToastMessageWhenRightButtonClicked() throws Exception {
        presenter.onRightClickButton();
        verify(view).showToastForRightClickButton();
    }

    @Test
    public void shouldShowToastMessageWhenLeftButtonClicked() throws Exception {
        presenter.onLeftClickButton();
        verify(view).showToastForLeftClickButton();
    }
}