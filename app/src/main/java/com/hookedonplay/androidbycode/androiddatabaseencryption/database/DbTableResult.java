package com.hookedonplay.androidbycode.androiddatabaseencryption.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hookedonplay.androidbycode.androiddatabaseencryption.app.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BRM20150209
 *         SQLite unencrpted database example
 * @see {@link android.database.sqlite.SQLiteDatabase}
 */
public class DbTableResult {
    @SuppressWarnings("unused")
    private static final String TAG = "DbTableResult";

    private SQLiteDatabase mDatabase;
    private DbTableResultHelper mDbHelper;

    /**
     * Array of all columns in the table
     */
    private String[] mAllColumns = {
            DbTableResultHelper.COLUMN_ID,
            DbTableResultHelper.COLUMN_NAME,
            DbTableResultHelper.COLUMN_RANKING,
            DbTableResultHelper.COLUMN_TIME
    };

    public DbTableResult(Context context) {
        mDbHelper = new DbTableResultHelper(context);
    }

    /**
     * Open the SQLite database
     */
    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    /**
     * Close the SQLite database
     */
    public void close() {
        mDbHelper.close();
    }


    public Result addResult(@NonNull final Result result) {
        ContentValues values = new ContentValues();
        values.put(DbTableResultHelper.COLUMN_NAME, result.mName);
        values.put(DbTableResultHelper.COLUMN_RANKING, result.mRanking);
        values.put(DbTableResultHelper.COLUMN_TIME, result.mTime);

        final long insertId = mDatabase.insert(DbTableResultHelper.TABLE_TARGET, null, values);

        Cursor cursor = mDatabase.query(DbTableResultHelper.TABLE_TARGET,
                mAllColumns, DbTableResultHelper.COLUMN_ID + " = "
                        + insertId, null, null, null, null);

        cursor.moveToFirst();

        Result resultInserted = cursorToResult(cursor);
        cursor.close();

        return resultInserted;
    }

    public void testSelect(@Nullable String where, @Nullable String[] args) {
        long recordCount = DatabaseUtils.queryNumEntries(mDatabase, DbTableResultHelper.TABLE_TARGET);
        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, null, null);
            cursor.close();
        }

        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, DbTableResultHelper.COLUMN_NAME, null);
            cursor.close();
        }

        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, DbTableResultHelper.COLUMN_RANKING, null);
            cursor.close();
        }

        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, DbTableResultHelper.COLUMN_TIME, null);
            cursor.close();
        }
    }

    public boolean deleteFirstRecord() {
        List<Result> results = getResults(null, null, "1");
        for (Result result : results) {
            mDatabase.delete(DbTableResultHelper.TABLE_TARGET, DbTableResultHelper.COLUMN_ID + "=?", new String[] {""+result.mID});
        }
        return results.size() > 0;
    }
    /**
     * Delete all records from the database table (but not the table itself)
     */
    public void deleteAll() {
        mDatabase.delete(DbTableResultHelper.TABLE_TARGET, null, null);
    }

    /**
     * Retrieve a list of Results that match the filters passed
     *
     * @param where Sqlite where filter string
     * @param args  Sqlite args array
     * @return List of dbQuake objects that meet filter
     */
    public List<Result> getResults(@Nullable String where, @Nullable String[] args, String limit) {
        List<Result> databaseSelectedResults = new ArrayList<>();
        Cursor cursor = mDatabase.query(DbTableResultHelper.TABLE_TARGET,
                mAllColumns, where, args, null, null, null, limit);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Result result = cursorToResult(cursor);
            databaseSelectedResults.add(result);
            cursor.moveToNext();
        }

        cursor.close();

        return databaseSelectedResults;
    }

    /**
     * Take the cursor from a database query and create fill the result structure
     *
     * @param cursor Database cursor from sqlite query
     * @return new result structure
     */
    private Result cursorToResult(@NonNull final Cursor cursor) {
        Result result = new Result();
        result.mID = cursor.getLong(0);
        result.mName = cursor.getString(1);
        result.mRanking = cursor.getInt(2);
        result.mTime = cursor.getFloat(3);
        return result;
    }
}