package com.dgsd.android.mangamaster.data;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.dgsd.android.mangamaster.util.ProviderUtils;
import timber.log.Timber;
import com.dgsd.android.mangamaster.BuildConfig;

import java.sql.SQLException;

/**
 * Provies access to the underlying datastore
 */
public class MMContentProvider extends ContentProvider {

    private static final String AUTHORITY = BuildConfig.CONTENT_AUTHORITY;

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final int TYPE_SERIES = 0x1;
    public static final int TYPE_SERIES_GENRES = 0x2;
    public static final int TYPE_CHAPTERS = 0x3;
    public static final int TYPE_PAGES = 0x4;

    public static Uri SERIES_URI;
    public static Uri SERIES_GENRE_URI;
    public static Uri CHAPTERS_URI;
    public static Uri PAGES_URI;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static Db mDb;

    private ContentResolver mContentResolver;

    static {
        SERIES_URI = Uri.withAppendedPath(BASE_URI, "series");
        SERIES_GENRE_URI = Uri.withAppendedPath(BASE_URI, "genres");
        CHAPTERS_URI = Uri.withAppendedPath(BASE_URI, "chapters");
        PAGES_URI = Uri.withAppendedPath(BASE_URI, "pages");

        sURIMatcher.addURI(AUTHORITY, "series", TYPE_SERIES);
        sURIMatcher.addURI(AUTHORITY, "genres", TYPE_SERIES_GENRES);
        sURIMatcher.addURI(AUTHORITY, "chapters", TYPE_CHAPTERS);
        sURIMatcher.addURI(AUTHORITY, "pages", TYPE_PAGES);
    }

    @Override
    public boolean onCreate() {
        mDb = Db.get(getContext());
        mContentResolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public String getType(final Uri uri) {
        return sURIMatcher.match(uri) == UriMatcher.NO_MATCH ?
                null : uri.toString();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String sel, String[] selArgs, String sort) {
        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(getTableFromType(sURIMatcher.match(uri)).getName());

            Cursor cursor = qb.query(mDb.getReadableDatabase(),
                    projection, sel, selArgs, null, null, sort, uri.getQueryParameter("limit"));

            if (cursor != null) {
                cursor.setNotificationUri(mContentResolver, uri);
            }
            return cursor;
        } catch (Exception e) {
            Timber.e(e, "Error quering database");
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDb.getWritableDatabase();

        try {
            final int type = sURIMatcher.match(uri);
            final DbTable table = getTableFromType(type);
            final DbField[] upsertFields = getUpsertFieldsFromType(type);

            final long id;
            if (upsertFields == null || upsertFields.length == 0) {
                id = db.replaceOrThrow(table.getName(), null, values);
            } else {
                id = ProviderUtils.upsert(db, table, values, upsertFields);
            }

            if (id >= 0) {
                Uri newUri = ContentUris.withAppendedId(uri, id);
                mContentResolver.notifyChange(uri, null);
                return newUri;
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }

        } catch (Exception e) {
            Timber.e(e, "Error inserting into database");
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String sel, String[] selArgs) {
        try {
            final int type = sURIMatcher.match(uri);
            final SQLiteDatabase db = mDb.getWritableDatabase();
            int rowsAffected = db.delete(getTableFromType(type).getName(), sel, selArgs);

            mContentResolver.notifyChange(uri, null);
            return rowsAffected;
        } catch (Exception e) {
            Timber.e(e, "Error deleting from database");
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String sel, String[] selArgs) {
        try {
            final int type = sURIMatcher.match(uri);
            final SQLiteDatabase db = mDb.getWritableDatabase();
            final int rowsAffected = db.update(getTableFromType(type).getName(), values, sel, selArgs);

            mContentResolver.notifyChange(uri, null);
            return rowsAffected;
        } catch (Exception e) {
            Timber.e(e, "Error updating database");
        }

        return 0;
    }

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        final SQLiteDatabase db = mDb.getWritableDatabase();

        final int type = sURIMatcher.match(uri);
        final DbTable table = getTableFromType(type);
        final DbField[] upsertFields = getUpsertFieldsFromType(type);

        int numInserted = 0;
        try {
            db.beginTransaction();

            for (ContentValues v : values) {
                final long id;
                if (upsertFields == null || upsertFields.length == 0) {
                    id = db.replaceOrThrow(table.getName(), null, v);
                } else {
                    id = ProviderUtils.upsert(db, table, v, upsertFields);
                }

                if (id > 0) {
                    numInserted++;
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Timber.e(e, "Error inserting into database");
        } finally {
            db.endTransaction();
        }

        mContentResolver.notifyChange(uri, null);

        return numInserted;
    }

    private DbField[] getUpsertFieldsFromType(int type) {
        switch (type) {
            case TYPE_SERIES:
                return new DbField[]{ Db.Field.SERIES_ID };
            case TYPE_SERIES_GENRES:
                return new DbField[]{ Db.Field.SERIES_ID, Db.Field.NAME };
            case TYPE_CHAPTERS:
                return new DbField[]{ Db.Field.CHAPTER_ID };
            case TYPE_PAGES:
                return new DbField[]{ Db.Field.PAGE_ID };
            default:
                return null;
        }
    }

    private DbTable getTableFromType(int type) {
        switch (type) {
            case TYPE_SERIES:
                return Db.Table.SERIES;
            case TYPE_SERIES_GENRES:
                return Db.Table.GENRES;
            case TYPE_CHAPTERS:
                return Db.Table.CHAPTERS;
            case TYPE_PAGES:
                return Db.Table.PAGES;
            default:
                throw new IllegalArgumentException("Unrecognised uri type: " + type);
        }
    }
}
