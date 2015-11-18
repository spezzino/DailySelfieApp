package org.coursera.android.dailyselfie;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by spezzino on 11/15/15.
 */
public class Selfie implements Serializable {
    public static final String _ID = "_id";
    public static final String SELFIE_PATH = "selfiePath";
    public static final String SELFIE_TIME = "selfieTime";
    public static final String USER_ID = "userId";
    private static final String TABLE_NAME = "selfies";
    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME + "("
            + _ID + " integer primary key autoincrement, "
            + SELFIE_PATH + " text not null, "
            + SELFIE_TIME + " datetime default null, "
            + USER_ID + " text not null "
            + ");";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private long id;
    private String selfiePath;
    private long selfieTime;
    private String userId;

    public Selfie() {
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String createTable() {
        return CREATE_TABLE;
    }

    public static String upgradeTable() {
        return dropTable();
    }

    public static String dropTable() {
        return DROP_TABLE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSelfiePath() {
        return selfiePath;
    }

    public void setSelfiePath(String selfiePath) {
        this.selfiePath = selfiePath;
    }

    public long getSelfieTime() {
        return selfieTime;
    }

    public void setSelfieTime(long selfieTime) {
        this.selfieTime = selfieTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Selfie{" +
                "id=" + id +
                ", selfiePath=" + selfiePath +
                ", selfieTime='" + selfieTime + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("selfiePath", selfiePath);
        contentValues.put("selfieTime", selfieTime);
        contentValues.put("userId", userId);
        return contentValues;
    }
}
