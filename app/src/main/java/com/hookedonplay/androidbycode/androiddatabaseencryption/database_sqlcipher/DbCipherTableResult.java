package com.hookedonplay.androidbycode.androiddatabaseencryption.database_sqlcipher;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hookedonplay.androidbycode.androiddatabaseencryption.app.Result;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BRM20150209
 *         Encrypted SQLCipher database example
 * @see {@link net.sqlcipher.database.SQLiteDatabase}
 */
public class DbCipherTableResult {
    @SuppressWarnings("unused")
    private static final String TAG = "DbCipherTableResult";

    private SQLiteDatabase mDatabase;
    private DbCipherTableResultHelper mDbHelper;

    /**
     * Array of all columns in the table
     */
    private String[] mAllColumns = {
            DbCipherTableResultHelper.COLUMN_ID,
            DbCipherTableResultHelper.COLUMN_NAME,
            DbCipherTableResultHelper.COLUMN_RANKING,
            DbCipherTableResultHelper.COLUMN_TIME
    };

    public DbCipherTableResult(Context context) {
        mDbHelper = new DbCipherTableResultHelper(context);
    }

    /**
     * Open the SQLite database
     */
    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase("my_secure_key");
    }

    /**
     * Close the SQLite database
     */
    public void close() {
        mDbHelper.close();
    }


    public Result addResult(@NonNull final Result result) {
        ContentValues values = new ContentValues();
        values.put(DbCipherTableResultHelper.COLUMN_NAME, result.mName);
        values.put(DbCipherTableResultHelper.COLUMN_RANKING, result.mRanking);
        values.put(DbCipherTableResultHelper.COLUMN_TIME, result.mTime);

        final long insertId = mDatabase.insert(DbCipherTableResultHelper.TABLE_TARGET, null, values);

        Cursor cursor = mDatabase.query(DbCipherTableResultHelper.TABLE_TARGET,
                mAllColumns, DbCipherTableResultHelper.COLUMN_ID + " = "
                        + insertId, null, null, null, null);

        cursor.moveToFirst();

        Result resultInserted = cursorToResult(cursor);
        cursor.close();

        return resultInserted;
    }

    public void testSelect(@Nullable String where, @Nullable String[] args) {
        long recordCount = DatabaseUtils.queryNumEntries(mDatabase, DbCipherTableResultHelper.TABLE_TARGET);
        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbCipherTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, null, null);
            cursor.close();
        }

        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbCipherTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, DbCipherTableResultHelper.COLUMN_NAME, null);
            cursor.close();
        }

        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbCipherTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, DbCipherTableResultHelper.COLUMN_RANKING, null);
            cursor.close();
        }

        for (long i = recordCount; i > 0; i--) {
            Cursor cursor = mDatabase.query(DbCipherTableResultHelper.TABLE_TARGET,
                    mAllColumns, where, args, null, null, DbCipherTableResultHelper.COLUMN_TIME, null);
            cursor.close();
        }
    }

    public boolean deleteFirstRecord() {
        List<Result> results = getResults(null, null, "1");
        for (Result result : results) {
            mDatabase.delete(DbCipherTableResultHelper.TABLE_TARGET, DbCipherTableResultHelper.COLUMN_ID + "=?", new String[] {""+result.mID});
        }
        return results.size() > 0;
    }
    /**
     * Delete all records from the database table (but not the table itself)
     */
    public void deleteAll() {
        mDatabase.delete(DbCipherTableResultHelper.TABLE_TARGET, null, null);
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
        Cursor cursor = mDatabase.query(DbCipherTableResultHelper.TABLE_TARGET,
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