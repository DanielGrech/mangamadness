package com.dgsd.android.mangamaster.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.util.DaoUtils;
import com.dgsd.android.mangamaster.util.ProviderUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ChapterLoader extends AsyncLoader<List<MangaChapter>> {

    public static Uri CONTENT_URI = Uri.parse("loader://chapter_loader");

    private final String mSeriesId;

    public ChapterLoader(final Context context, final String seriesId) {
        super(context);
        mSeriesId = seriesId;
    }

    @Override
    protected Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public List<MangaChapter> loadInBackground() {
        List<MangaChapter> chapters = new LinkedList<>();

        Cursor cursor = null;
        try {
            cursor = ProviderUtils.query(MMContentProvider.CHAPTERS_URI)
                    .where(Db.Field.SERIES_ID + " = ?", mSeriesId)
                    .sort(Db.Field.SEQUENCE_NUMBER + " DESC")
                    .cursor(getContext());

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    chapters.add(DaoUtils.build(MangaChapter.class, cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return chapters;
    }
}
