package com.dgsd.android.mangamaster.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.dgsd.android.mangamaster.data.CursorColumnMap;
import com.dgsd.android.mangamaster.data.Db;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.dgsd.android.mangamaster.util.ContentValuesBuilder;

public class PageDao implements IDao<MangaPage> {

    @Override
    public MangaPage build(final Cursor cursor) {
        final CursorColumnMap cc = new CursorColumnMap(cursor);

        final MangaPage p = new MangaPage();

        p.setId(cc.getLong(Db.Field.ID));
        p.setPageId(cc.getString(Db.Field.PAGE_ID));
        p.setChapterId(cc.getString(Db.Field.CHAPTER_ID));
        p.setName(cc.getInt(Db.Field.NAME));
        p.setImageUrl(cc.getString(Db.Field.IMAGE_URL));
        p.setTimeCreated(cc.getLong(Db.Field.TIME_CREATED));

        return p;
    }

    @Override
    public ContentValues convert(final MangaPage page) {
        final ContentValuesBuilder b = new ContentValuesBuilder();

        if (page.getId() > 0) {
            b.put(Db.Field.ID, page.getId());
        }

        b.put(Db.Field.PAGE_ID, page.getPageId())
                .put(Db.Field.CHAPTER_ID, page.getChapterId())
                .put(Db.Field.NAME, page.getName())
                .put(Db.Field.IMAGE_URL, page.getImageUrl())
                .put(Db.Field.TIME_CREATED, page.getTimeCreated());

        return b.build();
    }
}
