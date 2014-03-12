package com.dgsd.android.mangamaster.api;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.dgsd.android.mangamaster.MMApp;
import com.dgsd.android.mangamaster.data.*;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.util.ContentValuesBuilder;
import com.dgsd.android.mangamaster.util.DaoUtils;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
            ContentValues[] seriesValues = new ContentValues[series.size()];
            List<ContentValues> genreValues = new LinkedList<>();

            for (int i = 0, size = series.size(); i < size; i++) {
                final MangaSeries s = series.get(i);
                seriesValues[i] = DaoUtils.convert(s);

                if (s.hasGenres()) {
                    for (String genre : s.getGenres()) {
                        genreValues.add(new ContentValuesBuilder()
                                .put(Db.Field.SERIES_ID.getName(), s.getSeriesId())
                                .put(Db.Field.NAME.getName(), genre)
                                .build());
                    }
                }
            }

            mContentResolver.bulkInsert(MMContentProvider.SERIES_URI, seriesValues);

            if (!genreValues.isEmpty()) {
                ContentValues[] values = new ContentValues[genreValues.size()];
                genreValues.toArray(values);

                mContentResolver.bulkInsert(MMContentProvider.SERIES_GENRE_URI, values);
            }

            notify(SeriesLoader.CONTENT_URI);
        }
    }

    @Override
    public void saveChapters(final List<MangaChapter> chapters) {
        if (chapters != null && !chapters.isEmpty()) {
            ContentValues[] values = new ContentValues[chapters.size()];
            for (int i = 0, size = chapters.size(); i < size; i++) {
                values[i] = DaoUtils.convert(chapters.get(i));
            }

            mContentResolver.bulkInsert(MMContentProvider.CHAPTERS_URI, values);
            notify(ChapterLoader.CONTENT_URI);
        }
    }

    @Override
    public void savePages(final List<MangaPage> pages) {
        if (pages != null && !pages.isEmpty()) {
            ContentValues[] values = new ContentValues[pages.size()];
            for (int i = 0, size = pages.size(); i < size; i++) {
                MangaPage page = pages.get(i);
                values[i] = DaoUtils.convert(page);
            }

            mContentResolver.bulkInsert(MMContentProvider.PAGES_URI, values);
            notify(PageLoader.CONTENT_URI);
        }
    }

    private void notify(Uri uri) {
        mContentResolver.notifyChange(uri, null, true);
    }
}
