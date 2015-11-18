package org.coursera.android.dailyselfie;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.SimpleDateFormat;

/**
 * Created by spezzino on 11/17/15.
 */
public class Utils {
    public static String formatDate(long millis, String format) {
        SimpleDateFormat sout = new SimpleDateFormat(format);
        return sout.format(millis);
    }

    public static String formatDateTime(long millis) {
        return formatDate(millis, "dd/MM/yyyy HH:mm");
    }

    public static void postNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_menu_camera)
                        .setContentTitle("DailySelfie")
                        .setContentText("Time to take a selfie!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, TakeSelfieActivity2.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    public static ProgressDialog getProgressDialog(Context context,
                                                   String title, String text) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle(title);
        pd.setMessage(text);
        pd.setCancelable(false);

        return pd;
    }

    public static void scheduleAlarms(Context context, long triggerAt) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, 1000 * 60 * 60 * 24, pi);
    }
}
