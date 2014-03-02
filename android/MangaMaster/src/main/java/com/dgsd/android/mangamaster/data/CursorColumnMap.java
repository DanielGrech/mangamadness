package com.dgsd.android.mangamaster.data;

import android.database.Cursor;
import android.support.v4.util.ArrayMap;

/**
 *
 */
public class CursorColumnMap {
    private ArrayMap<String, Integer> mColumns;

    private Cursor mCursor;

    public CursorColumnMap(Cursor cursor) {
        mCursor = cursor;
        mColumns = new ArrayMap<>();

        for (String col : cursor.getColumnNames()) {
            mColumns.put(col, cursor.getColumnIndex(col));
        }
    }

    public int getInt(DbField col) {
        return getInt(col.getName());
    }

    public int getInt(String col) {
        return mCursor.getInt(get(col));
    }

    public long getLong(DbField col) {
        return getLong(col.getName());
    }

    public long getLong(String col) {
        return mCursor.getLong(get(col));
    }

    public String getString(DbField col) {
        return getString(col.getName());
    }

    public String getString(String col) {
        return mCursor.getString(get(col));
    }

    private int get(String columnName) {
        if (mColumns == null || mColumns.isEmpty() || !mColumns.containsKey(columnName)) {
            throw new CursorColumnMapException("Could not find " + columnName + " in " + mColumns);
        }

        return mColumns.get(columnName);
    }

    public class CursorColumnMapException extends RuntimeException {
        public CursorColumnMapException(final String detailMessage) {
            super(detailMessage);
        }
    }
}
