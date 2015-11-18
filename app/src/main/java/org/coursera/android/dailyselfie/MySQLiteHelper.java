package org.coursera.android.dailyselfie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by spezzino on 11/15/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "selfie.local.db";
    private static final int DATABASE_VERSION = 3;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(Selfie.createTable());
    }

    protected void onDestroy(SQLiteDatabase database) {
        database.execSQL(Selfie.dropTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(Selfie.upgradeTable());
        onCreate(database);
    }
}