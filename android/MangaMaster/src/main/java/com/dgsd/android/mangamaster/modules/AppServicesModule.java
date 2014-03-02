package com.dgsd.android.mangamaster.modules;

import android.content.ContentResolver;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import com.dgsd.android.mangamaster.BuildConfig;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.activity.MainActivity;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.log.CustomLogger;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import timber.log.Timber;

import javax.inject.Singleton;

/**
 * Provides access to the different services required in the app
 */
@Module(
        complete = false,
        library = true,
        injects = {
                MainActivity.class
        }
)
public class AppServicesModule {

    @Provides
    @Singleton
    public LocalBroadcastManager providesLocalBroadcastManager(@ForApplication Context context) {
        return LocalBroadcastManager.getInstance(context.getApplicationContext());
    }

    @Provides
    @Singleton
    public ContentResolver providesContentResolver(@ForApplication Context context){
        return context.getApplicationContext().getContentResolver();
    }

    @Provides
    @Singleton
    public Bus providesBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Provides
    @Singleton
    public JobManager providesJobManager(@ForApplication final Context context) {
        final MMApp app = (MMApp) context.getApplicationContext();
        final DependencyInjector injector = new DependencyInjector() {
            @Override
            public void inject(final BaseJob job) {
                app.inject(job);
            }
        };

        final CustomLogger logger = new CustomLogger() {
            @Override
            public boolean isDebugEnabled() {
                return BuildConfig.DEBUG;
            }

            @Override
            public void d(final String message, final Object... args) {
                Timber.d(message, args);
            }

            @Override
            public void e(final Throwable throwable, final String message, final Object... args) {
                Timber.e(throwable, message, args);
            }

            @Override
            public void e(final String message, final Object... args) {
                Timber.e(message, args);
            }
        };

        final Configuration config = new Configuration.Builder(app)
                .customLogger(logger)
                .injector(injector)
                .build();

        return new JobManager(app, config);
    }
}
