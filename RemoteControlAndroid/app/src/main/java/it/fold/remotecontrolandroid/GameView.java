package it.fold.remotecontrolandroid;

import android.view.View;

/**
 * Created by Dhruv on 4/7/2016.
 */
public interface GameView {

    void onLeftClickButton(View v);

    void onRightClickButton(View v);

    void onMiddleClickButton(View v);

    void bringUpKeyboard(View v);

    void closeDrawer();

    void setNetworkValuesForLeftClick();

    void setNetworkValuesForRightClick();

    void setNetworkValuesForMiddleClick();

    void showToastForLeftClickButton();

    void showToastForRightClickButton();

    void showToastForMiddleClickButton();
}
