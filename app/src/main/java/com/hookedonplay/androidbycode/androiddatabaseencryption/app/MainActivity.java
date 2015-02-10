package com.hookedonplay.androidbycode.androiddatabaseencryption.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.hookedonplay.androidbycode.androiddatabaseencryption.R;
import com.hookedonplay.androidbycode.androiddatabaseencryption.database.DbTableResult;
import com.hookedonplay.androidbycode.androiddatabaseencryption.database_sqlcipher.DbCipherTableResult;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * @author BRM20150209
 *         SQLCipher Android database example
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    static private final int DATABASE_INSERT = 0;
    static private final int DATABASE_SELECT = 1;
    static private final int DATABASE_DELETE = 2;

    private static final int RECORD_COUNT = 500;

    private ListView mOutputListView;
    private ArrayAdapter<String> mOutputAdapter;
    private Button mButtonGenerate;
    private ProgressBar mProgress;

    /**
     * Database the uses your regular Sqlite android database
     */
    private DbTableResult mDatabaseTable;

    /**
     * Database encrypted with SQLCipher
     */
    private DbCipherTableResult mDatabaseCipherTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Important: Must call this function before calling any SQLCipher functions
         */
        SQLiteDatabase.loadLibs(getApplicationContext());

        mButtonGenerate = (Button) findViewById(R.id.buttonGenerate);
        mProgress = (ProgressBar) findViewById(R.id.progressGenerate);
        mOutputListView = (ListView) findViewById(R.id.outputListView);
        mOutputAdapter = new ArrayAdapter<>(this, R.layout.small_list_item);
        mOutputAdapter.add("Press Generate to build database");
        mOutputListView.setAdapter(mOutputAdapter);
    }

    public void onClickGenerate(View view) {
        Switch switchEncrypt = (Switch) findViewById(R.id.switchEncrypt);
        mOutputAdapter.clear();

        if (false == switchEncrypt.isChecked()) {
            mDatabaseTable = new DbTableResult(this);
            mDatabaseTable.open();
            mDatabaseTable.deleteAll();
            mDatabaseTable.close();
            mOutputAdapter.add("Generating database (Encryption: off)");
            new AsyncGenerateDatabase().execute(DATABASE_INSERT);
        } else {
            mDatabaseCipherTable = new DbCipherTableResult(this);
            mDatabaseCipherTable.open();
            mDatabaseCipherTable.deleteAll();
            mDatabaseCipherTable.close();
            mOutputAdapter.add("Generating database (Encryption: on)");
            new AsyncGenerateCipherDatabase().execute(DATABASE_INSERT);
        }
        mButtonGenerate.setEnabled(false);
        mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * AsyncTask to generate database
     */
    private class AsyncGenerateDatabase extends AsyncTask<Integer, Void, Long> {

        private int mTask;

        @Override
        protected Long doInBackground(Integer... params) {
            if (params.length != 1) {
                throw new IllegalArgumentException("Must pass operation as argument");
            }

            mTask = params[0];
            Long operationStartTime = System.currentTimeMillis();

            switch (mTask) {
                case DATABASE_INSERT:
                    databaseInsert(RECORD_COUNT);
                    break;
                case DATABASE_SELECT:
                    databaseSelect();
                    break;
                case DATABASE_DELETE:
                    databaseDelete();
                    break;
                default:
                    Log.e(TAG, "Unknown database request");
            }
            return System.currentTimeMillis() - operationStartTime;
        }

        @Override
        protected void onPostExecute(Long result) {
            mOutputAdapter.add("Complete in " + result + " ms");
            if (mTask == DATABASE_INSERT) {
                mOutputAdapter.add("Starting select tests");
                new AsyncGenerateDatabase().execute(DATABASE_SELECT);
            } else if (mTask == DATABASE_SELECT) {
                mOutputAdapter.add("Starting Delete tests");
                new AsyncGenerateDatabase().execute(DATABASE_DELETE);
            } else {
                mProgress.setVisibility(View.GONE);
                mButtonGenerate.setEnabled(true);
                mOutputAdapter.add("Finished tests");
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        private void databaseInsert(final int recordCount) {
            mDatabaseTable.open();
            Result result = new Result(0, "competitor name", 1, 100.0f);
            for (int i = 0; i < recordCount; i++) {
                mDatabaseTable.addResult(result);
                result.mRanking = i + 1;
                result.mTime += 0.1;
            }
            mDatabaseTable.close();

        }

        private void databaseSelect() {
            mDatabaseTable.open();
            mDatabaseTable.testSelect(null, null);
            mDatabaseTable.close();
        }

        private void databaseDelete() {

            mDatabaseTable.open();
            while (mDatabaseTable.deleteFirstRecord()) {
            }
            mDatabaseTable.close();
        }
    }

    /**
     * AsyncTask to generate and test encrypted database
     */
    private class AsyncGenerateCipherDatabase extends AsyncTask<Integer, Void, Long> {

        private int mTask;

        @Override
        protected Long doInBackground(Integer... params) {
            if (params.length != 1) {
                throw new IllegalArgumentException("Must pass operation as argument (encrypted)");
            }

            mTask = params[0];
            Long operationStartTime = System.currentTimeMillis();

            switch (mTask) {
                case DATABASE_INSERT:
                    databaseInsert(RECORD_COUNT);
                    break;
                case DATABASE_SELECT:
                    databaseSelect();
                    break;
                case DATABASE_DELETE:
                    databaseDelete();
                    break;
                default:
                    Log.e(TAG, "Unknown database request (encrypted)");
            }
            return System.currentTimeMillis() - operationStartTime;
        }

        @Override
        protected void onPostExecute(Long result) {
            mOutputAdapter.add("Complete in " + result + " ms (encrypted)");
            if (mTask == DATABASE_INSERT) {
                mOutputAdapter.add("Starting select tests (encrypted)");
                new AsyncGenerateCipherDatabase().execute(DATABASE_SELECT);
            } else if (mTask == DATABASE_SELECT) {
                mOutputAdapter.add("Starting Delete tests (encrypted)");
                new AsyncGenerateCipherDatabase().execute(DATABASE_DELETE);
            } else {
                mProgress.setVisibility(View.GONE);
                mButtonGenerate.setEnabled(true);
                mOutputAdapter.add("Finished tests (encrypted)");
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        private void databaseInsert(final int recordCount) {
            mDatabaseCipherTable.open();
            Result result = new Result(0, "competitor name", 1, 100.0f);
            for (int i = 0; i < recordCount; i++) {
                mDatabaseCipherTable.addResult(result);
                result.mRanking = i + 1;
                result.mTime += 0.1;
            }
            mDatabaseCipherTable.close();
        }

        private void databaseSelect() {
            mDatabaseCipherTable.open();
            mDatabaseCipherTable.testSelect(null, null);
            mDatabaseCipherTable.close();
        }

        private void databaseDelete() {
            mDatabaseCipherTable.open();
            while (mDatabaseCipherTable.deleteFirstRecord()) {
            }
            mDatabaseCipherTable.close();
        }
    }
}
