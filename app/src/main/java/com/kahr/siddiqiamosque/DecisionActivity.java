package com.kahr.siddiqiamosque;


import static android.graphics.Color.parseColor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DecisionActivity extends AppCompatActivity {

    private static final String URL = "https://onpointglobal.co.uk/mkcjmStatus.php";
    private static final String deviceID_Url = "http://onpointglobal.co.uk/mkcjm/deviceid.php";

    private final int MY_PERMISSIONS_RECORD_AUDIO = 3;
    private static final int REQUEST_PHONE_STATE = 2;
    private static final int READ_EXTERNAL_STORAGE = 1;
    String id;
    private static String status ="No";
    CircularDotsLoader loader;
    AudioManager manager;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_decision);
        getSupportActionBar().hide();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);


        loader = findViewById(R.id.loader);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();

        loader.setDefaultColor(ContextCompat.getColor(this, R.color.grey)); //yellow

        loader.setSelectedColor(ContextCompat.getColor(this, R.color.grey)); //yellow
        loader.setBigCircleRadius(68);
        loader.setRadius(20);
        loader.setAnimDur(100);
        loader.setShowRunningShadow(true);
        loader.setFirstShadowColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); //lightBrown
        loader.setSecondShadowColor(ContextCompat.getColor(this, R.color.grey)); //yellow

        //  loadProducts();

        checkInternetStatus();

    }

    public void checkInternetStatus() {
        if (AppStatus.getInstance(this).isOnline()) {

            loadProducts();
        } else {
            InternetAlert();
        }
    }


    public void InternetAlert() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DecisionActivity.this, R.style.AlertDialogStyle);

        builder.setTitle("No Internet Connection")
                .setMessage("Check your  Internet Connection or Try again"
                )

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finishAffinity();
                        System.exit(0);
                    }
                })
                .setNegativeButton("", null);
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#1174d1")));
    }
    public void showMusicAlert() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DecisionActivity.this, R.style.AlertDialogStyle);

        builder.setTitle("Background Music Found")
                .setMessage("Please Stop background music and try again"
                )

                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(manager.isMusicActive())
                        {
                            Toast.makeText(DecisionActivity.this, "Please Stop background music and try again", Toast.LENGTH_SHORT).show();
                            showMusicAlert();
                        }
                        else
                        {
                            Intent it = new Intent(DecisionActivity.this, WebActivity.class);
                            startActivity(it);
                            loader.setVisibility(View.GONE);
                        }
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finishAffinity();
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#1174d1")));
    }


    private void requestAudioPermissions() {

        loader.setVisibility(View.GONE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                //   Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            Intent it = new Intent(DecisionActivity.this, MainActivity.class);
            startActivity(it);

            requestTelephonePermission();
        }
    }

    private void requestTelephonePermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                //         Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions

                loader.setVisibility(View.GONE);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PHONE_STATE);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PHONE_STATE);

                Toast.makeText(this, "Please Grant Permission to Continue", Toast.LENGTH_LONG).show();

            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            Intent it = new Intent(DecisionActivity.this, MainActivity.class);
            startActivity(it);

//            Toast.makeText(this, "Denied ", Toast.LENGTH_SHORT).show();
        }

    }


    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    requestTelephonePermission();

                }
                return;
            }

            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!


                    requestAudioPermissions();

                }
                return;

            }

            case REQUEST_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                    Intent it = new Intent(DecisionActivity.this, MainActivity.class);
                    startActivity(it);
                }

                return;
            }

        }


    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                //Give user option to still opt-in the permissions

                loader.setVisibility(View.GONE);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE);
            }
        }
        //If permission is granted, then go ahead recording audio

        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            requestAudioPermissions();

        }
    }

    private void loadProducts() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, deviceID_Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray products = new JSONArray(response);
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);
                                String androidId = Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID);

                                id = productObject.getString("DeviceID");

                                if (androidId.contentEquals(id)) {

                                    FirebaseMessaging.getInstance().unsubscribeFromTopic("RadioTest");
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic("ApplicationTest");
                                    requestStoragePermission();

                                    status = "Yes";
                                }
                            }

                            if (status == "Yes") {

                                loader.setVisibility(View.GONE);
                            }
                            else {


                                if(manager.isMusicActive())
                                {

                                    loader.setVisibility(View.GONE);
                                    Toast.makeText(DecisionActivity.this, "Please stop app background music", Toast.LENGTH_SHORT).show();
                                    showMusicAlert();
                                    return;


                                }
                                else
                                {
                                    Intent it = new Intent(DecisionActivity.this, WebActivity.class);
                                    startActivity(it);
                                    loader.setVisibility(View.GONE);
                                }

                            }

                        } catch (JSONException e) {

                            if (id == null) {

                                Intent it = new Intent(DecisionActivity.this, WebActivity.class);
                                startActivity(it);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DecisionActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        Volley.newRequestQueue(this).add(stringRequest);

    }

}

