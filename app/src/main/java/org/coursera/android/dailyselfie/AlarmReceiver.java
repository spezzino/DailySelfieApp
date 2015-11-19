package org.coursera.android.dailyselfie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by spezzino on 11/16/15.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub

        Log.i("DailySelfie.TAG", "tick");

        Utils.postNotification(context);
    }

}
