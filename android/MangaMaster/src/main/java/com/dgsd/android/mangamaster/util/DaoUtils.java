package com.dgsd.android.mangamaster.util;

import android.content.ContentValues;
import android.database.Cursor;
import com.dgsd.android.mangamaster.dao.ChapterDao;
import com.dgsd.android.mangamaster.dao.IDao;
import com.dgsd.android.mangamaster.dao.PageDao;
import com.dgsd.android.mangamaster.dao.SeriesDao;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.dgsd.android.mangamaster.model.MangaSeries;

import javax.inject.Inject;

/**
 * Provides a single point of conversion for all dao-related objects
 */
public class DaoUtils {

    @Inject
    static SeriesDao sSeriesDao;

    @Inject
    static ChapterDao sChapterDao;

    @Inject
    static PageDao sPageDao;

    public static ContentValues convert(Object obj) {
        IDao dao = getDao(obj.getClass());
        return dao.convert(obj);
    }

    public static <T> T build(Class<T> cls, Cursor cursor) {
        IDao<T> dao = getDao(cls);
        return dao.build(cursor);
    }

    private static <T> IDao<T> getDao(Class<T> cls) {
        if (cls.equals(MangaSeries.class)) {
            return (IDao<T>) sSeriesDao;
        } else if (cls.equals(MangaChapter.class)) {
            return (IDao<T>)  sChapterDao;
        } else if (cls.equals(MangaPage.class)) {
            return (IDao<T>)  sPageDao;
        }

        throw new IllegalArgumentException("No Dao for class: " + cls.getSimpleName());
    }

}
