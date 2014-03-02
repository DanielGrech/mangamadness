package com.dgsd.android.mangamaster.modules;

import com.dgsd.android.mangamaster.dao.ChapterDao;
import com.dgsd.android.mangamaster.dao.PageDao;
import com.dgsd.android.mangamaster.dao.SeriesDao;
import com.dgsd.android.mangamaster.util.DaoUtils;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Provides access to our model DAO objects
 */
@Module(
        staticInjections = {
                DaoUtils.class
        }
)
public class DaoModule {

    @Singleton
    @Provides
    public SeriesDao providesSeriesDao() {
        return new SeriesDao();
    }

    @Singleton
    @Provides
    public ChapterDao providesChapterDao() {
        return new ChapterDao();
    }

    @Singleton
    @Provides
    public PageDao providesPageDao() {
        return new PageDao();
    }

}
