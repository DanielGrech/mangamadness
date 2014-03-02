package com.dgsd.android.mangamaster.api;

public interface IAccountManager {

    public static enum LoginType {
        USERNAME_PASSWORD,
        GOOGLE_PLUS,
        FACEBOOK,
        TWITTER
    }

    public boolean isLoggedIn();

    public String getUsername();

    public LoginType getLoginType();
}
