package com.napster.primitive.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.napster.primitive.pojo.Submission;
import com.napster.primitive.utils.Constants;

import java.util.ArrayList;

/**
 * Created by napster on 05/01/17.
 */

public class PrimitiveDao extends SQLiteOpenHelper {

    public PrimitiveDao(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constants.CREATE_TABLE_SUBMISSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(Constants.DROP_TABLE_SUBMISSIONS);
        onCreate(db);
    }

    public synchronized void insertSubmission(Submission submission) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.COL_FILE_KEY, submission.getFileKey());
        contentValues.put(Constants.COL_PROCESSED, submission.isProcessed() ? 1 : 0);
        contentValues.put(Constants.COL_ORG_LOC_URI, submission.getOriginalUri());
        contentValues.put(Constants.COL_PROC_LOC_URI, submission.getProcessedUri());
        db.insertWithOnConflict(Constants.TBL_SUBMISSIONS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public ArrayList<Submission> selectSubmissions() {
        ArrayList<Submission> submissions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = Constants.SELECT_SUBMISSIONS;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Submission submission = new Submission();
                    submission.setFileKey(cursor.getString(0));
                    submission.setProcessed(cursor.getInt(1) == 1 ? true : false);
                    submission.setOriginalUri(cursor.getString(2));
                    submission.setProcessedUri(cursor.getString(3));
                    submissions.add(submission);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return submissions;
    }
}
