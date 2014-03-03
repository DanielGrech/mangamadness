package com.dgsd.android.mangamaster.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.receiver.ApiBroadcastReceiver;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Base class for all activities in the app
 */
public abstract class BaseActivity extends Activity {
    
    @Inject
    protected Bus mEventBus;

    protected MMApp mApp;

    /**
     * Catches API-related broadcasts
     */
    private ApiBroadcastReceiver mApiReceiver = new ApiBroadcastReceiver() {
        @Override
        protected void onStart(final String token) {
            setProgressBarIndeterminateVisibility(getRunningCounter() > 0);
            onJobRequestStart(token);
        }

        @Override
        protected void onFinish(final String token) {
            setProgressBarIndeterminateVisibility(getRunningCounter() > 0);
            onJobRequestFinish(token);
        }

        @Override
        protected void onError(final String token, final String errorMsg) {
            onJobRequestError(token, errorMsg);
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        mApp = (MMApp) getApplication();
        mApp.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApiReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApiReceiver.unregister(this);
    }

    protected void onJobRequestStart(String action) {
        //No-op
    }

    protected void onJobRequestFinish(String action) {
        //No-op
    }

    protected void onJobRequestError(String action, String errorMsg) {
        //No-op
    }

    /**
     * Listen out for API broadcasts of type <code>token</code>
     *
     * @param token The token returned from {@link com.dgsd.android.mangamaster.jobs.BaseJob#getToken()}
     */
    public void registerForJob(String token) {
        mApiReceiver.addAcceptableToken(token);
    }

    /**
     * @param id  The id of the fragment to retrieve
     * @param <T> A {@link Fragment} subclass
     * @return The fragment with id <code>id</code>, or null if it doesn't exist
     */
    protected <T extends Fragment> T findFragment(int id) {
        return (T) getFragmentManager().findFragmentById(id);
    }
}
