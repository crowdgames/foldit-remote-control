package it.fold.remotecontrolandroid;

/**
 * Created by Dhruv on 3/21/2016.
 */
public class LoginIPPresenter {
    private final LoginView view;

    public LoginIPPresenter(LoginView view) {
        this.view = view;
    }

    public void attemptLogin() {
        String ipAddress = view.getIPAddress();
        if(ipAddress.isEmpty()) {
            view.showIPError(R.string.error_field_required);
        }
    }
}
