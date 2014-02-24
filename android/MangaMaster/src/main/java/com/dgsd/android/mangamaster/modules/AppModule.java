package com.dgsd.android.mangamaster.modules;

import android.content.Context;
import com.dgsd.android.mangamaster.MMApp;
import dagger.Module;
import dagger.Provides;

/**
 * Provides injection of our global {@link android.app.Application} object
 */
@Module(
        library = true
)
public class AppModule {

    private MMApp mApp;

    public AppModule(MMApp app) {
        mApp = app;
    }

    @Provides
    @ForApplication
    public Context providesApplicationContext() {
        return mApp;
    }
}
