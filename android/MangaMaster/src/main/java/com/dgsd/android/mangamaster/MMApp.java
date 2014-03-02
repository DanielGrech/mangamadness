package com.dgsd.android.mangamaster;

import android.app.Application;
import com.dgsd.android.mangamaster.modules.ApiModule;
import com.dgsd.android.mangamaster.modules.AppModule;
import com.dgsd.android.mangamaster.modules.AppServicesModule;
import com.dgsd.android.mangamaster.modules.DaoModule;
import com.dgsd.android.mangamaster.util.ReleaseLogger;
import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * 
 */
public class MMApp extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            //Default logger
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseLogger(getClass().getSimpleName()));
        }

        mObjectGraph = ObjectGraph.create(getDiModules());
        mObjectGraph.injectStatics();
    }

    /**
     * The base set of DI modules to inject app components with
     */
    private Object[] getDiModules() {
        return new Object[]{
                new AppModule(this),
                new ApiModule(),
                new AppServicesModule(),
                new DaoModule()
        };
    }

    /**
     * Inject the given object
     *
     * @param obj          The obejct to inject
     * @param extraModules Any additional modules to include in the injection
     */
    public void inject(Object obj, Object... extraModules) {
        ObjectGraph og = mObjectGraph;
        if (extraModules != null && extraModules.length > 0) {
            og = mObjectGraph.plus(extraModules);
        }
        og.inject(obj);
    }
}
