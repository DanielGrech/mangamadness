package com.dgsd.android.mangamaster.modules;

import com.dgsd.android.mangamaster.util.DaoUtils;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Provides access to our model DAO objects
 */
@Module(
        staticInjections = {
                // DaoUtils.class
        }
)
public class DaoModule {

}
