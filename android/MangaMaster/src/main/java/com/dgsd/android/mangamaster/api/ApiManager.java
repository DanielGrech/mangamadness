package com.dgsd.android.mangamaster.api;


import android.content.Context;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.model.ChapterListRequest;
import com.dgsd.android.mangamaster.model.PageListRequest;
import com.dgsd.android.mangamaster.model.SeriesListRequest;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * Default implementation of {@link com.dgsd.android.mangamaster.api.IApiManager}
 */
public class ApiManager implements IApiManager {

    @Inject
    MangaMadnessApi mMMApi;

    @Inject
    IPersistenceManager mPersistenceManager;

    MMApp mApp;

    public ApiManager(Context context) {
        mApp = (MMApp) context.getApplicationContext();
        mApp.inject(this);
    }

    @Override
    public void getSeriesList(int limit, int offset, long updatedSince) {
        SeriesListRequest results = mMMApi.getSeriesList(limit, offset, updatedSince);
        if (results != null && results.hasSeries()) {
            mPersistenceManager.saveSeries(results.getSeries());
        }
    }

    @Override
    public void getChaptersInSeries(String series, int limit, int offset, long updatedSince) {
        ChapterListRequest results = mMMApi.getChapterList(series, limit, offset, updatedSince);
        if (results != null && results.hasChapters()) {
            mPersistenceManager.saveChapters(results.getChapters());
        }
    }

    @Override
    public void getPagesInChapter(String series, int chapter) {
        PageListRequest results = mMMApi.getPages(series, chapter);

        Timber.d("Got pages %s", results);

        if (results != null && results.hasPages()) {
            mPersistenceManager.savePages(results.getPages());
        }
    }

    @Override
    public void getPagesInChapter(final String chapterId) {
        PageListRequest results = mMMApi.getPages(chapterId);

        Timber.d("Got pages %s", results);

        if (results != null && results.hasPages()) {
            mPersistenceManager.savePages(results.getPages());
        }
    }
}