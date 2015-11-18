package org.coursera.android.dailyselfie;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

public class SelfieContentProvider extends ContentProvider {
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/forms";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/form";
    // used for the UriMacher
    private static final int SELFIES = 10;
    private static final int SELFIE_ID = 20;
    private static final String AUTHORITY = "org.coursera.android.dailyselfie.authority";
    private static final String BASE_PATH = "selfies";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SELFIES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SELFIE_ID);
    }

    // database
    private MySQLiteHelper database;

    public SelfieContentProvider() {
    }

    @Override
    public boolean onCreate() {
        database = new MySQLiteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d("DailySelfie.TAG", "querying ContentProvider\n" +
                "Uri: " + uri.toString() + "\n" +
                "Projection: " + Arrays.toString(projection) + "\n" +
                "Selection: " + selection + "\n" +
                "SelectionArgs: " + Arrays.toString(selectionArgs) + "\n" +
                "SortOrder: " + sortOrder);

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(Selfie.getTableName());

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SELFIES:
                break;
            case SELFIE_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(Selfie._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("DailySelfie.TAG", "inserting ContentProvider\n" +
                "Uri: " + uri.toString() +
                "ContentValues: " + values.toString());

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case SELFIES:
                id = sqlDB.insert(Selfie.getTableName(), null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Uri result = Uri.parse(CONTENT_URI + "/" + id);
        Log.d("DailySelfie.TAG", "Inserted URI: " + result.toString());
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SELFIES:
                rowsDeleted = sqlDB.delete(Selfie.getTableName(), selection,
                        selectionArgs);
                break;
            case SELFIE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(Selfie.getTableName(),
                            Selfie._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(Selfie.getTableName(),
                            Selfie._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case SELFIES:
                rowsUpdated = sqlDB.update(Selfie.getTableName(),
                        values,
                        selection,
                        selectionArgs);
                break;
            case SELFIE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(Selfie.getTableName(),
                            values,
                            Selfie._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(Selfie.getTableName(),
                            values,
                            Selfie._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {Selfie._ID, Selfie.SELFIE_PATH, Selfie.SELFIE_TIME, Selfie.USER_ID};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}