package com.dgsd.android.mangamaster.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.dgsd.android.mangamaster.data.CursorColumnMap;
import com.dgsd.android.mangamaster.data.Db;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.util.ContentValuesBuilder;

public class ChapterDao implements IDao<MangaChapter> {

    @Override
    public MangaChapter build(final Cursor cursor) {
        final CursorColumnMap cc = new CursorColumnMap(cursor);

        final MangaChapter c = new MangaChapter();

        c.setId(cc.getLong(Db.Field.ID));
        c.setSeriesId(cc.getString(Db.Field.SERIES_ID));
        c.setChapterId(cc.getString(Db.Field.CHAPTER_ID));
        c.setName(cc.getString(Db.Field.NAME));
        c.setTitle(cc.getString(Db.Field.TITLE));
        c.setSequenceNumber(cc.getInt(Db.Field.SEQUENCE_NUMBER));
        c.setReleaseDate(cc.getLong(Db.Field.RELEASE_DATE));
        c.setTimeCreated(cc.getLong(Db.Field.TIME_CREATED));

        return c;
    }

    @Override
    public ContentValues convert(final MangaChapter chapter) {
        final ContentValuesBuilder b = new ContentValuesBuilder();

        if (chapter.getId() > 0) {
            b.put(Db.Field.ID.getName(), chapter.getId());
        }

        b.put(Db.Field.SERIES_ID.getName(), chapter.getSeriesId())
                .put(Db.Field.CHAPTER_ID.getName(), chapter.getChapterId())
                .put(Db.Field.NAME.getName(), chapter.getName())
                .put(Db.Field.TITLE.getName(), chapter.getTitle())
                .put(Db.Field.SEQUENCE_NUMBER.getName(), chapter.getSequenceNumber())
                .put(Db.Field.RELEASE_DATE.getName(), chapter.getReleaseDate())
                .put(Db.Field.TIME_CREATED.getName(), chapter.getTimeCreated());

        return b.build();
    }
}
