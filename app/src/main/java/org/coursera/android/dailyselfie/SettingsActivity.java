package org.coursera.android.dailyselfie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by spezzino on 11/17/15.
 */
public class SettingsActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private SharedPreferences sharedPreferences;
    private String uuid;
    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        uuid = getIntent().getStringExtra(MainActivity.EXTRA_UUID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rootView = (LinearLayout) findViewById(R.id.rootView);

        timePicker = (TimePicker) findViewById(R.id.timer);
        timePicker.setIs24HourView(true);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        String preferences = sharedPreferences.getString(uuid,"{}");
        try {
            JSONObject json = new JSONObject(preferences);
            if(json.has("hour")){
                timePicker.setCurrentHour(json.getInt("hour"));
            }else {
                timePicker.setCurrentHour(12);
            }
            if(json.has("minute")){
                timePicker.setCurrentMinute(json.getInt("minute"));
            }else {
                timePicker.setCurrentMinute(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            timePicker.setCurrentHour(12);
            timePicker.setCurrentMinute(0);
        }


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void saveSettings(){
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        JSONObject json = new JSONObject();
        try {
            json.put("hour", hour);
            json.put("minute", minute);
            sharedPreferences.edit().putString(uuid, json.toString()).apply();

            Date date = new Date(System.currentTimeMillis());
            date.setHours(hour);
            date.setMinutes(minute);
            Utils.scheduleAlarms(this, date.getTime());

            Snackbar.make(rootView, "Settings saved!", Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    SettingsActivity.this.finish();
                }
            }).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
