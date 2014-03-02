package com.dgsd.android.mangamaster.api;

import android.content.Context;

public class UserAccountManager implements IAccountManager {

    public UserAccountManager(Context context) {

    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.USERNAME_PASSWORD;
    }
}
