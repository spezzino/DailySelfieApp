package org.coursera.android.dailyselfie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by spezzino on 11/16/15.
 */
public class ViewSelfieActivity extends AppCompatActivity implements RestApi {

    private Button effect1;
    private Button effect2;
    private Button effect3;
    private Button done;
    private ImageView selfie;
    private RelativeLayout rootView;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private RestApi restApi;
    private RestAdapter restAdapter;

    private ProgressDialog pd;

    public static final String BASE_URL = "http://192.168.0.5:8080";

    public static final String SELFIE_PATH = "selfiePath";

    private enum Effects {
        GRAYSCALE,
        INVERT,
        KALEIDOSCOPE
    }

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selfie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        effect1 = (Button) findViewById(R.id.effect1);
        effect2 = (Button) findViewById(R.id.effect2);
        effect3 = (Button) findViewById(R.id.effect3);
        done = (Button) findViewById(R.id.done);
        selfie = (ImageView) findViewById(R.id.preview);

        restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
        restAdapter.setLogLevel(RestAdapter.LogLevel.BASIC);
        restApi = restAdapter.create(RestApi.class);

        rootView = (RelativeLayout) findViewById(R.id.rootView);

        if (getIntent().getStringExtra(SELFIE_PATH) == null) {
            Uri contentUri = getIntent().getParcelableExtra(TakeSelfieActivity2.CONTENT_URI);

            String[] projection = {Selfie.SELFIE_PATH, Selfie._ID};
            Cursor cursor = getContentResolver().query(contentUri, projection, null, null,
                    null);

            if (cursor != null) {
                cursor.moveToFirst();
                filePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(Selfie.SELFIE_PATH));
            }
        } else {
            filePath = getIntent().getStringExtra(SELFIE_PATH);
        }
        loadPreview(new File(filePath));

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake() {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent();
            }
        });

        effect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyEffect(Effects.GRAYSCALE);
            }
        });
        effect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyEffect(Effects.INVERT);
            }
        });
        effect3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyEffect(Effects.KALEIDOSCOPE);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewSelfieActivity.this, ListSelfiesActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        pd = Utils.getProgressDialog(this, "Processing image", "Please wait...");

    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private void loadPreview(File file) {

        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        selfie.setImageBitmap(myBitmap);

    }

    private void handleShakeEvent() {
        Log.d("DailySelfie.TAG", "shake!!");
        Snackbar.make(rootView, "Random effect!", Snackbar.LENGTH_LONG).show();

        switch ((int) (Math.random() * 100 % 3)) {
            case 0:
                applyEffect(Effects.GRAYSCALE);
                break;
            case 1:
                applyEffect(Effects.INVERT);
                break;
            case 2:
                applyEffect(Effects.KALEIDOSCOPE);
                break;
            default:
                applyEffect(Effects.GRAYSCALE);

        }
    }

    private void applyEffect(Effects effect) {
        pd.show();
        applyFilter(new TypedFile("image/jpeg", new File(filePath)), effect.toString(), new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                pd.dismiss();
                android.os.Debug.waitForDebugger();
                try {
                    InputStream is = response.getBody().in();

                    File file = new File(filePath);
                    FileOutputStream f = new FileOutputStream(file);

                    byte[] buffer = new byte[10240];
                    int len1 = 0;
                    while ((len1 = is.read(buffer)) > 0) {
                        f.write(buffer, 0, len1);
                    }
                    f.close();
                    loadPreview(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                android.os.Debug.waitForDebugger();
                pd.dismiss();
                Toast.makeText(ViewSelfieActivity.this, "Error while processing, try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void applyFilter(TypedFile image, String effect, Callback<Response> callback) {
        restApi.applyFilter(image, effect, callback);
    }
}
