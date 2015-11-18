package org.coursera.android.dailyselfie;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by spezzino on 11/16/15.
 */
public class ListSelfiesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView listView;
    private CustomCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_selfies);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.selfiesList);

//        getLoaderManager().initLoader(0, null, this);
        getSupportLoaderManager().initLoader(0, null, this);
        adapter = new CustomCursorAdapter(this, null, 0);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = ((CustomCursorAdapter) parent.getAdapter()).getCursor();
                c.moveToPosition(position);
                Intent i = new Intent(ListSelfiesActivity.this, ViewSelfieActivity.class);
                i.putExtra(ViewSelfieActivity.SELFIE_PATH, c.getString(c.getColumnIndex(Selfie.SELFIE_PATH)));
                startActivity(i);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Selfie._ID, Selfie.SELFIE_TIME, Selfie.SELFIE_PATH};
        String selection = Selfie.USER_ID + "=?";
        String[] selectionArgs = {getIntent().getStringExtra(MainActivity.EXTRA_UUID)};

        CursorLoader cursorLoader = new CursorLoader(this,
                SelfieContentProvider.CONTENT_URI, projection, selection, selectionArgs, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
