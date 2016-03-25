package it.fold.remotecontrolandroid;

/**
 * Created by Dhruv on 3/21/2016.
 */
public interface LoginView {

    String getIPAddress();

    void showIPEmptyError(int resId);

    void showIpInvalidError(int resId);

    void showPasswordInvalidError(int resId);

    void resetErrors();

    boolean isIPValid(String Ip);

    String getPassword();

    boolean isPasswordValid(String password);

    void attemptLoginTask(String ip, String password);
}
