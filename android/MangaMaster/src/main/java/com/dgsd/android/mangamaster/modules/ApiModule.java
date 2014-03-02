package com.dgsd.android.mangamaster.modules;

import android.content.Context;
import com.dgsd.android.mangamaster.BuildConfig;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.activity.MainActivity;
import com.dgsd.android.mangamaster.api.*;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import timber.log.Timber;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides access to the underlying API
 */
@Module(
        complete = false,
        library = true,

        injects = {
                ApiManager.class,
        }
)
public class ApiModule {

    private RestAdapter mRestAdapter;

    public ApiModule() {
        mRestAdapter = getRestAdapter();
    }

    @Provides
    @Singleton
    public IApiManager providesApiManager(@ForApplication Context context) {
        return new ApiManager(context);
    }

    @Provides
    @Singleton
    public IPersistenceManager providesPersistenceManager(@ForApplication Context context) {
        return new PersistenceManager(context);
    }

    @Provides
    @Singleton
    public MangaMadnessApi providesMangaMadnessApi() {
        return mRestAdapter.create(MangaMadnessApi.class);
    }

    private RestAdapter getRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_API_URL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(final String s) {
                        Timber.d(s);
                    }
                })
                .build();
    }

}
