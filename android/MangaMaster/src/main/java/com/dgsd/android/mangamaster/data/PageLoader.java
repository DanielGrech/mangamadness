package com.dgsd.android.mangamaster.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.dgsd.android.mangamaster.util.DaoUtils;
import com.dgsd.android.mangamaster.util.ProviderUtils;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class PageLoader extends AsyncLoader<List<MangaPage>> {

    private final String mChapterId;

    public PageLoader(final Context context, final String chapterId) {
        super(context);
        mChapterId = chapterId;
    }

    @Override
    protected Uri getContentUri() {
        return MMContentProvider.PAGES_URI;
    }

    @Override
    public List<MangaPage> loadInBackground() {
        List<MangaPage> pages = new LinkedList<>();

        Cursor cursor = null;
        try {
            cursor = ProviderUtils.query(MMContentProvider.PAGES_URI)
                    .where(Db.Field.CHAPTER_ID + " = ?", mChapterId)
                    .sort(Db.Field.NAME + " ASC")
                    .cursor(getContext());
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    pages.add(DaoUtils.build(MangaPage.class, cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return pages;
    }
}
