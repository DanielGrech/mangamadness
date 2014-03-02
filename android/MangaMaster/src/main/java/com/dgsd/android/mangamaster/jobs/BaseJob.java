package com.dgsd.android.mangamaster.jobs;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.dgsd.android.mangamaster.api.IApiManager;
import com.dgsd.android.mangamaster.api.IPersistenceManager;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.UUID;

import static com.dgsd.android.mangamaster.jobs.Constants.*;

/**
 * Base class for long running jobs in the app
 */
public abstract class BaseJob extends Job {

    protected static final int PRIORITY_LOW = 1;
    protected static final int PRIORITY_DEFAULT = 10;
    protected static final int PRIORITY_HIGH = 9000;

    private transient final String mToken;

    @Inject
    transient LocalBroadcastManager mLocalBroadcastManager;

    @Inject
    transient IApiManager mApiManager;

    protected abstract void runJob();

    public BaseJob(Params params) {
        super(params);
        mToken = UUID.randomUUID().toString();
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        sendStartBroadcast(mToken);

//        final long startTime = System.nanoTime();

        runJob();

//        Timber.d("Time: %d", ( (System.nanoTime() - startTime) / 1000000));

        sendFinishBroadcast(mToken);
    }

    @Override
    protected void onCancel() {
        sendErrorBroadcast(mToken, null);
        sendFinishBroadcast(mToken);
    }

    @Override
    protected boolean shouldReRunOnThrowable(final Throwable throwable) {
        // Override in subclasses to differentiate between errors.
        // For example, if this was an Authentication exception,
        // there would be much point in continuing unless we re-authenticate
        return true;
    }

    private void sendStartBroadcast(String token) {
        Intent intent = new Intent(ACTION_API_START);
        intent.putExtra(EXTRA_TOKEN, token);
        broadcast(intent);
    }

    private void sendFinishBroadcast(String token) {
        Intent intent = new Intent(ACTION_API_FINISH);
        intent.putExtra(EXTRA_TOKEN, token);
        broadcast(intent);
    }

    private void sendErrorBroadcast(String token, String errorMessage) {
        final Intent intent = new Intent(ACTION_API_ERROR);
        intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
        intent.putExtra(EXTRA_TOKEN, token);
        broadcast(intent);
    }

    private void broadcast(Intent intent) {
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    public String getToken() {
        return mToken;
    }
}
