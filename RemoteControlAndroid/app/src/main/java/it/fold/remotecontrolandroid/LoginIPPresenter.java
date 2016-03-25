package it.fold.remotecontrolandroid;

import android.text.TextUtils;

/**
 * Created by Dhruv on 3/21/2016.
 */
public class LoginIPPresenter {
    private final LoginView view;

    public LoginIPPresenter(LoginView view) {
        this.view = view;
    }

    public void attemptLogin() {
        view.resetErrors();
        String ipAddress = view.getIPAddress();
        String password = view.getPassword();
        if(password!=null) {
            if (!password.isEmpty() && !view.isPasswordValid(password)) {
                view.showPasswordInvalidError(R.string.error_invalid_password);
            }
        }

        if(ipAddress==null || ipAddress.isEmpty()) {
            view.showIPEmptyError(R.string.error_field_required);
        } else if (!view.isIPValid(ipAddress)) {
            view.showIpInvalidError(R.string.error_invalid_IP);
        }
        view.attemptLoginTask(ipAddress,password);


    }
}
