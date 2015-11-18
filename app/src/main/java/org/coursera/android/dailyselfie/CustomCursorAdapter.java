package org.coursera.android.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by spezzino on 11/17/15.
 */
public class CustomCursorAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView selfieDate = (TextView) view.findViewById(R.id.tvFechaSync);

        long selfieTime = cursor.getLong(cursor.getColumnIndex(Selfie.SELFIE_TIME));

        selfieDate.setText(Utils.formatDateTime(selfieTime));
    }
}
