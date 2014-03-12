package com.dgsd.android.mangamaster.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
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

    private String mSeriesId;

    private String mChapterId;

    public static ChapterLoader createWithSeriesId(Context context, String seriesId) {
        final ChapterLoader loader = new ChapterLoader(context);
        loader.mSeriesId = seriesId;
        return loader;
    }

    public static ChapterLoader createWithChapterId(Context context, String chapterId) {
        final ChapterLoader loader = new ChapterLoader(context);
        loader.mChapterId = chapterId;
        return loader;
    }

    private ChapterLoader(final Context context) {
        super(context);
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
            final String sel;
            final String[] selArgs;
            if (!TextUtils.isEmpty(mSeriesId)) {
                sel = Db.Field.SERIES_ID + " = ?";
                selArgs = new String[]{ mSeriesId };
            } else if (!TextUtils.isEmpty(mChapterId)) {
                sel = Db.Field.CHAPTER_ID + " = ?";
                selArgs = new String[]{ mChapterId };
            } else {
                sel = "1=0";
                selArgs = null;
            }

            cursor = ProviderUtils.query(MMContentProvider.CHAPTERS_URI)
                    .where(sel, selArgs)
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
