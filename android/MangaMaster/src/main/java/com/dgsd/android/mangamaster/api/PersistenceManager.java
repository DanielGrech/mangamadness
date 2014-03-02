package com.dgsd.android.mangamaster.api;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.data.Db;
import com.dgsd.android.mangamaster.data.MMContentProvider;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.util.ContentValuesBuilder;
import com.dgsd.android.mangamaster.util.DaoUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * Default implementation of
 * {@link com.dgsd.android.mangamaster.api.IPersistenceManager}
 */
public class PersistenceManager implements IPersistenceManager {

    final MMApp mApp;

    final ContentResolver mContentResolver;

    public PersistenceManager(Context context) {
        mApp = (MMApp) context.getApplicationContext();
        mContentResolver = mApp.getContentResolver();
    }

    @Override
    public void saveSeries(final List<MangaSeries> series) {
        if (series != null && !series.isEmpty()) {
            for (MangaSeries s : series) {
                mContentResolver.insert(MMContentProvider.SERIES_URI,
                        DaoUtils.convert(s));

                if (s.hasGenres()) {
                    for (String genre : s.getGenres()) {
                        final ContentValues values = new ContentValuesBuilder()
                                .put(Db.Field.SERIES_ID.getName(), s.getSeriesId())
                                .put(Db.Field.NAME.getName(), genre)
                                .build();
                        mContentResolver.insert(MMContentProvider.SERIES_GENRE_URI, values);
                    }
                }
            }
        }
    }

    @Override
    public void saveChapters(final List<MangaChapter> chapters) {
        if (chapters != null && !chapters.isEmpty()) {
            for (MangaChapter c : chapters) {
                mContentResolver.insert(MMContentProvider.CHAPTERS_URI,
                        DaoUtils.convert(c));
            }
        }
    }

    @Override
    public void savePages(final List<MangaPage> pages) {
        if (pages != null && !pages.isEmpty()) {
            for (MangaPage p : pages) {
                mContentResolver.insert(MMContentProvider.PAGES_URI,
                        DaoUtils.convert(p));
            }
        }
    }
}
