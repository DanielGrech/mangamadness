package com.dgsd.android.mangamaster.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import timber.log.Timber;

import java.lang.reflect.Modifier;

public class Db extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String DB_NAME = "MangaMaster.db";

    private static Db sInstance;

    public static final Object[] LOCK = new Object[0];

    public static Db get(Context c) {
        if (sInstance == null)
            sInstance = new Db(c);

        return sInstance;
    }

    protected Db(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        runForEachTable(new TableRunnable() {
            @Override
            public void run(final DbTable table) {
                db.execSQL(table.getCreateSql());
            }
        });
    }

    @Override
    public void onConfigure(final SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA synchronous=OFF");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: Support db upgrade...
        throw new IllegalStateException("We havent implemented db upgrades yet!");
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        synchronized (LOCK) {
            return super.getReadableDatabase();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        synchronized (LOCK) {
            return super.getWritableDatabase();
        }
    }

    /**
     * Execute a given against each table in the database
     *
     * @param runnable The task to perform
     */
    private void runForEachTable(TableRunnable runnable) {
        java.lang.reflect.Field[] declaredFields = Db.Table.class.getDeclaredFields();
        for (java.lang.reflect.Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(DbTable.class)) {
                try {
                    runnable.run((DbTable) field.get(null));
                } catch (IllegalAccessException e) {
                    Timber.e(e, "Error executing table runnable: " + field.getName());
                }
            }
        }
    }

    /**
     * Encapsulates a task to be run against a table
     */
    private interface TableRunnable {
        /**
         * Execute the request task
         *
         * @param table The table to execute the task on
         */
        public void run(DbTable table);
    }

    /**
     * Database fields used in the app
     */
    public static class Field {
        private Field() {
        }

        public static final DbField ID = new DbField("_id", "integer", "primary key");
        public static final DbField SERIES_ID = new DbField("series_id", "text");
        public static final DbField NAME = new DbField("name", "text");
        public static final DbField AUTHOR = new DbField("author", "text");
        public static final DbField ARTIST = new DbField("artist", "text");
        public static final DbField SUMMARY = new DbField("summary", "text");
        public static final DbField URL_SEGMENT = new DbField("url_segment", "text", "collate nocase");
        public static final DbField COVER_IMAGE_URL = new DbField("cover_image_url", "text");
        public static final DbField YEAR_OF_RELEASE = new DbField("year_of_release", "integer");
        public static final DbField TIME_CREATED = new DbField("time_created", "integer");
        public static final DbField CHAPTER_ID = new DbField("chapter_id", "text");
        public static final DbField TITLE = new DbField("title", "text");
        public static final DbField SEQUENCE_NUMBER = new DbField("sequence_number", "integer");
        public static final DbField RELEASE_DATE = new DbField("release_date", "integer");
        public static final DbField PAGE_ID = new DbField("page_id", "text");
        public static final DbField IMAGE_URL = new DbField("image_url", "text");

    }

    /**
     * Application database tables
     */
    public static class Table {
        private Table() {
        }

        public static DbTable SERIES = DbTable.with("series")
                .columns(
                        Field.ID,
                        Field.SERIES_ID,
                        Field.NAME,
                        Field.AUTHOR,
                        Field.ARTIST,
                        Field.SUMMARY,
                        Field.URL_SEGMENT,
                        Field.COVER_IMAGE_URL,
                        Field.YEAR_OF_RELEASE,
                        Field.TIME_CREATED
                )
                .scripts(
                        "CREATE UNIQUE INDEX unique_server_id ON series (" + Field.SERIES_ID + ")",
                        "CREATE INDEX idx_series_server_id ON series (" + Field.SERIES_ID + ")"
                )
                .create();

        public static DbTable GENRES = DbTable.with("genres")
                .columns(
                        Field.SERIES_ID,
                        Field.NAME
                )
                .scripts(
                        "CREATE UNIQUE INDEX unique_genre_server_id ON genres (" + Field.SERIES_ID + ", " +
                                "" + Field.NAME + ")",
                        "CREATE INDEX idx_genre_series_id ON genres (" + Field.SERIES_ID + ")"
                )
                .create();

        public static DbTable CHAPTERS = DbTable.with("chapters")
                .columns(
                        Field.ID,
                        Field.SERIES_ID,
                        Field.CHAPTER_ID,
                        Field.NAME,
                        Field.TITLE,
                        Field.SEQUENCE_NUMBER,
                        Field.RELEASE_DATE,
                        Field.TIME_CREATED
                )
                .scripts(
                        "CREATE UNIQUE INDEX unique_chapter_id ON chapters (" + Field.CHAPTER_ID + ")",
                        "CREATE INDEX idx_chapter_server_id ON series (" + Field.CHAPTER_ID + ")",
                        "CREATE INDEX idx_chapter_series_id ON series (" + Field.SERIES_ID + ")"
                )
                .create();

        public static DbTable PAGES = DbTable.with("pages")
                .columns(
                        Field.ID,
                        Field.PAGE_ID,
                        Field.CHAPTER_ID,
                        Field.NAME,
                        Field.IMAGE_URL,
                        Field.TIME_CREATED
                )
                .scripts(
                        "CREATE UNIQUE INDEX unique_page_id ON pages (" + Field.PAGE_ID + ")",
                        "CREATE INDEX idx_pages_chapter_id ON pages (" + Field.CHAPTER_ID + ")"
                )
                .create();
    }
}