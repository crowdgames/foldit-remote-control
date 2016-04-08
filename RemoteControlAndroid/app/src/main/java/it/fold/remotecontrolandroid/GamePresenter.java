package it.fold.remotecontrolandroid;

/**
 * Created by Dhruv on 4/8/2016.
 */
public class GamePresenter {
    private final GameView view;

    public GamePresenter(GameView gameView) {
        this.view = gameView;
    }

    public void onLeftClickButton() {
        view.setNetworkValuesForLeftClick();
        view.closeDrawer();
        view.showToastForLeftClickButton();
    }

    public void onRightClickButton() {
        view.setNetworkValuesForRightClick();
        view.closeDrawer();
        view.showToastForRightClickButton();
    }

    public void onMiddleClickButton() {
        view.setNetworkValuesForMiddleClick();
        view.closeDrawer();
        view.showToastForMiddleClickButton();
    }
}
