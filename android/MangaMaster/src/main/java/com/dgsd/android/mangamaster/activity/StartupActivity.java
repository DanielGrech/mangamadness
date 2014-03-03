package com.dgsd.android.mangamaster.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.api.IAccountManager;

import javax.inject.Inject;

public class StartupActivity extends Activity {
    private static final String TAG = StartupActivity.class.getSimpleName();

    @Inject
    IAccountManager mAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MMApp app = (MMApp) getApplicationContext();
        app.inject(this);

        final Class mCls;

        if (mAccountManager.isLoggedIn()) {
            mCls = MainActivity.class;
        } else {
            mCls = LoginActivity.class;
        }

        //Pass on any extras we've already received
        final Intent intent = new Intent(this, mCls);
        intent.putExtras(getIntent());

        //Make our choice!
        startActivity(intent);
        finish();
    }
}