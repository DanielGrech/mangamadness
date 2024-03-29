package com.dgsd.android.mangamaster.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.activity.BaseActivity;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all fragments in the app
 */
public abstract class BaseFragment extends Fragment {

    @Inject
    protected Bus mEventBus;

    @Inject
    JobManager mJobManager;

    private Set<String> mAcceptableJobTokens = new HashSet<>();

    @Override
    public void onAttach(final Activity activity) {
        if ( !(activity instanceof BaseActivity)) {
            throw new IllegalArgumentException("Must embed in a subclass of BaseActivity");
        }

        super.onAttach(activity);
    }

    protected void registerForJob(String token) {
        mAcceptableJobTokens.add(token);
        getBaseActivity().registerForJob(token);
    }

    public boolean onJobRequestStart(String token) {
        return isRegisteredForJob(token);
    }

    public boolean onJobRequestFinish(String token) {
        return isRegisteredForJob(token);
    }

    public boolean onJobRequestError(String token, String error) {
        return isRegisteredForJob(token);
    }

    protected boolean isRegisteredForJob(String token) {
        return mAcceptableJobTokens.contains(token);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MMApp app = (MMApp) getActivity().getApplication();
        app.inject(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }


    /**
     * Reload the data from a loader
     *
     * @param loaderId  The id of the loader to reload
     * @param callbacks The callback to be invoked by the underlying loader
     */
    protected void reload(int loaderId, LoaderManager.LoaderCallbacks callbacks) {
        final Loader loader = getLoaderManager().getLoader(loaderId);
        if (loader == null) {
            getLoaderManager().initLoader(loaderId, null, callbacks);
        } else {
            getLoaderManager().restartLoader(loaderId, null, callbacks);
        }
    }
}
