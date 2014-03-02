package com.dgsd.android.mangamaster.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.dgsd.android.mangamaster.data.CursorColumnMap;
import com.dgsd.android.mangamaster.data.Db;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.util.ContentValuesBuilder;

public class SeriesDao implements IDao<MangaSeries> {

    @Override
    public MangaSeries build(final Cursor cursor) {
        CursorColumnMap cc = new CursorColumnMap(cursor);

        final MangaSeries s = new MangaSeries();
        s.setId(cc.getLong(Db.Field.ID));
        s.setSeriesId(cc.getString(Db.Field.SERIES_ID));
        s.setName(cc.getString(Db.Field.NAME));
        s.setAuthor(cc.getString(Db.Field.AUTHOR));
        s.setArtist(cc.getString(Db.Field.ARTIST));
        s.setSummary(cc.getString(Db.Field.SUMMARY));
        s.setUrlSegment(cc.getString(Db.Field.URL_SEGMENT));
        s.setCoverImageUrl(cc.getString(Db.Field.COVER_IMAGE_URL));
        s.setYearOfRelease(cc.getInt(Db.Field.YEAR_OF_RELEASE));
        s.setTimeCreated(cc.getLong(Db.Field.TIME_CREATED));
        return s;
    }

    @Override
    public ContentValues convert(final MangaSeries series) {
        final ContentValuesBuilder b = new ContentValuesBuilder();

        if (series.getId() > 0) {
            b.put(Db.Field.ID.getName(), series.getId());
        }

        b.put(Db.Field.SERIES_ID.getName(), series.getSeriesId())
                .put(Db.Field.NAME.getName(), series.getName())
                .put(Db.Field.AUTHOR.getName(), series.getAuthor())
                .put(Db.Field.ARTIST.getName(), series.getArtist())
                .put(Db.Field.SUMMARY.getName(), series.getSummary())
                .put(Db.Field.URL_SEGMENT.getName(), series.getUrlSegment())
                .put(Db.Field.COVER_IMAGE_URL.getName(), series.getCoverImageUrl())
                .put(Db.Field.YEAR_OF_RELEASE.getName(), series.getYearOfRelease())
                .put(Db.Field.TIME_CREATED.getName(), series.getTimeCreated());

        return b.build();
    }
}
