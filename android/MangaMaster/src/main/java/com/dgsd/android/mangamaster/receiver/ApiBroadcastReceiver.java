package com.dgsd.android.mangamaster.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.dgsd.android.mangamaster.R;

import java.util.HashSet;
import java.util.Set;

import static com.dgsd.android.mangamaster.jobs.Constants.*;

/**
 * Catches broadcasts sent at different lifecycle events
 * of Api requests as executed by {@link com.dgsd.android.mangamaster.service.ApiExecutorService}
 */
public abstract class ApiBroadcastReceiver extends BroadcastReceiver {

    private Set<String> mAcceptableTokens = new HashSet<>();

    /**
     * Called when a new API request has started
     *
     * @param token The kind of request being executed
     */
    protected abstract void onStart(String token);

    /**
     * Called when an API request has finished
     *
     * @param token The kind of request that finished
     */
    protected abstract void onFinish(String token);

    /**
     * Called when there is an error with an API request
     *
     * @param token    The kind of request which caused an error
     * @param errorMsg The human-readable error message representing the problem
     */
    protected abstract void onError(String token, String errorMsg);

    /**
     * Start listening for API events
     *
     * @param context
     */
    public void register(Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_API_START);
        filter.addAction(ACTION_API_FINISH);
        filter.addAction(ACTION_API_ERROR);
        LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
    }

    /**
     * Cease listening to API events
     *
     * @param context
     */
    public void unregister(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    /**
     * Accept broadcasts of type <code>token</code>
     *
     * @param token The kind of broadcast to accept
     */
    public void addAcceptableToken(String token) {
        mAcceptableTokens.add(token);
    }

    /**
     * Reference counter for the number of requests currently executing
     */
    private int mRunningCounter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final String token = intent.getStringExtra(EXTRA_TOKEN);
        if (action != null && token != null && mAcceptableTokens.contains(token)) {
            switch (action) {
                case ACTION_API_START:
                    mRunningCounter++;
                    onStart(token);
                    break;

                case ACTION_API_FINISH:
                    mRunningCounter--;
                    if (mRunningCounter < 0) {
                        mRunningCounter = 0;
                    }

                    onFinish(token);
                    break;

                case ACTION_API_ERROR:
                    String errorMsg = intent.getStringExtra(EXTRA_ERROR_MESSAGE);
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = context.getString(R.string.unknown_error);
                    }

                    onError(token, errorMsg);

                    break;
            }
        }
    }

    /**
     * @return The number of currently executing requests
     */
    public int getRunningCounter() {
        return mRunningCounter;
    }
}
