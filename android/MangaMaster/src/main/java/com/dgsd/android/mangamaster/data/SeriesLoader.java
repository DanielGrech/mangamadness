package com.dgsd.android.mangamaster.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.SparseArray;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.util.DaoUtils;
import com.dgsd.android.mangamaster.util.ProviderUtils;

import java.util.*;

/**
 *
 */
public class SeriesLoader extends AsyncLoader<List<MangaSeries>> {

    public static enum Sort {
        ALPHA, LATEST
    }

    private final Sort mSortOrder;

    private final String mSeriesId;

    private boolean mIncludeGenres;

    public static Uri CONTENT_URI = Uri.parse("loader://series_loader");

    public SeriesLoader(final Context context, boolean includeGenres, final Sort sort) {
        super(context);
        mSortOrder = sort;
        mSeriesId = null;
        mIncludeGenres = includeGenres;
    }

    public SeriesLoader(Context context, boolean includeGenres, String seriesId) {
        super(context);
        mSeriesId = seriesId;
        mSortOrder = null;
        mIncludeGenres = includeGenres;
    }

    @Override
    protected Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public List<MangaSeries> loadInBackground() {
        List<MangaSeries> series = getSeriesWithoutGenre();
        if (series != null) {
            Map<String, Set<String>> seriesIdToGenre = null;
            if (mIncludeGenres) {
                seriesIdToGenre = getGenres();
            }

            if (seriesIdToGenre != null) {
                for (MangaSeries mangaSeries : series) {
                    Set<String> genres
                            = seriesIdToGenre.get(mangaSeries.getSeriesId());

                    if (genres != null) {
                        mangaSeries.setGenres(new LinkedList<>(genres));
                    }
                }
            }
        }

        return series;
    }

    private Map<String, Set<String>> getGenres() {
        Cursor cursor = null;

        Map<String, Set<String>> retval = null;
        try {
            String sel = null;
            String[] selArgs = null;
            if (!TextUtils.isEmpty(mSeriesId)) {
                sel = Db.Field.SERIES_ID + " = ?";
                selArgs = new String[] { mSeriesId };
            }

            cursor = ProviderUtils.query(MMContentProvider.SERIES_GENRE_URI)
                    .where(sel, selArgs)
                    .sort(Db.Field.SERIES_ID + " ASC")
                    .cursor(getContext());
            if (cursor != null && cursor.moveToFirst()) {
                retval = new ArrayMap<>(cursor.getCount());
                CursorColumnMap cc = new CursorColumnMap(cursor);
                do {
                    final String seriesId = cc.getString(Db.Field.SERIES_ID);
                    final String name = cc.getString(Db.Field.NAME);

                    Set<String> set = retval.get(seriesId);
                    if (set == null) {
                        set = new HashSet<>();
                    }

                    set.add(name);
                    retval.put(seriesId, set);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return retval == null ?
                Collections.<String, Set<String>>emptyMap() : retval;
    }

    private List<MangaSeries> getSeriesWithoutGenre() {
        List<MangaSeries> series = new LinkedList<>();
        Cursor cursor = null;
        try {
            String sortStr = null;
            if (mSortOrder == Sort.LATEST) {
                sortStr = Db.Field.TIME_CREATED + " DESC";
            } else if (mSortOrder == Sort.ALPHA) {
                sortStr = Db.Field.URL_SEGMENT + " ASC";
            }

            String selStr = null;
            String[] selArgs = null;
            if (!TextUtils.isEmpty(mSeriesId)) {
                selStr = Db.Field.SERIES_ID + " = ?";
                selArgs = new String[]{ mSeriesId };
            }

            cursor = ProviderUtils.query(MMContentProvider.SERIES_URI)
                    .where(selStr, selArgs)
                    .sort(sortStr)
                    .cursor(getContext());

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    series.add(DaoUtils.build(MangaSeries.class, cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return series;
    }

}
