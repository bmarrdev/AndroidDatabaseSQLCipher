package com.hookedonplay.androidbycode.androiddatabaseencryption.app;

/**
 * @author BRM20150209
 *         SQLCipher Android database example
 */
public class Result {
    public long mID;
    public String mName;
    public int mRanking;
    public double mTime;

    public Result() {
    }

    public Result(long id, String name, int ranking, float time) {
        mID = id;
        mName = name;
        mRanking = ranking;
        mTime = time;
    }
}
