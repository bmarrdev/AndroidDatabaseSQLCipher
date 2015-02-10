package com.hookedonplay.androidbycode.androiddatabaseencryption.database_sqlcipher;

import android.content.Context;

import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * @author BRM20150209
 *
 * @see net.sqlcipher.database.SQLiteOpenHelper
 */
public class DbCipherTableResultHelper extends SQLiteOpenHelper {
    public static final String TABLE_TARGET = "table_result";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "col_name";
    public static final String COLUMN_RANKING = "col_ranking";
    public static final String COLUMN_TIME = "col_time";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TARGET + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME + " text, "
            + COLUMN_RANKING + " integer, "
            + COLUMN_TIME + " real);";

    private static final String DATABASE_NAME = "dbcipher_results.db";

    private static final int DATABASE_VERSION = 1;

    public DbCipherTableResultHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(DbCipherTableResultHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARGET);
        onCreate(db);
    }
} 