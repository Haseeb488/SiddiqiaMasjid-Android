package com.kahr.siddiqiamosque;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.parseColor;


public class PrayerActivity extends AppCompatActivity {

    BottomNavigationView bottomBar;
    private static final String Json_Url = "https://securenet.justyes.co.uk/Prod/SiddiqiaMosque/praytime.php";

    TextView fajr, jfajr, zuhr, jzuhr, asr, jasr, maghrib, jmaghrib, esha, jesha, sunrise;
    private ViewGroup container;
    CircularDotsLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);

        getSupportActionBar().setTitle("Today Prayer Timings");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00101F")));

        bottomBar = findViewById(R.id.bottom_navigationBar);

        fajr = findViewById(R.id.fajr);
        jfajr = findViewById(R.id.jfajr);
        zuhr = findViewById(R.id.zuhr);
        jzuhr = findViewById(R.id.jzuhr);
        asr = findViewById(R.id.asr);
        jasr = findViewById(R.id.jasr);
        maghrib = findViewById(R.id.maghrib);
        jmaghrib = findViewById(R.id.jmaghrib);
        esha = findViewById(R.id.esha);
        jesha = findViewById(R.id.jesha);
        sunrise = findViewById(R.id.sunrise);

        loader = findViewById(R.id.loader);

        loader.setDefaultColor(ContextCompat.getColor(this,R.color.Blue));
        loader.setSelectedColor(ContextCompat.getColor(this,R.color.Blue));
        loader.setBigCircleRadius(50);
        loader.setRadius(15);
        loader.setAnimDur(100);
        loader.setShowRunningShadow(true);
        loader.setFirstShadowColor(ContextCompat.getColor(this, R.color.lightBrown));
        loader.setSecondShadowColor(ContextCompat.getColor(this, R.color.Blue));

        checkInternetStatus();

        bottomBar.setSelectedItemId(R.id.nav_prayerTiming);


        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nave_hadith:

                        Intent it = new Intent(PrayerActivity.this, HadithActivity.class);
                        startActivity(it);
                        overridePendingTransition(0, 0);
                        finish();
                        break;


                    case R.id.nav_LiveStreaming:
                        Intent ht = new Intent(PrayerActivity.this, WebActivity.class);
                        startActivity(ht);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.nav_events:
                        Intent mt = new Intent(PrayerActivity.this, EventActivity.class);
                        startActivity(mt);
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                }

                return true;
            }
        });


    }


    public void checkInternetStatus()
    {
        if (AppStatus.getInstance(this).isOnline()) {
            loadProducts();
        }

        else {
            InternetAlert();
            loader.setVisibility(View.GONE);
        }
    }


    public void InternetAlert() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PrayerActivity.this, R.style.AlertDialogStyle);

        builder.setTitle("No Internet Connection")
                .setMessage("Check your  Internet Connection or Try again"
                )

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(PrayerActivity.this,WebActivity.class);
                        startActivity(it);
                    }
                })
                .setNegativeButton("", null);
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#1174d1")));
    }







    private void loadProducts() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Json_Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loader.setVisibility(View.GONE);

                        try {

                            JSONArray products = new JSONArray(response);

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);

                                String fajrAzan = productObject.getString("fajr");
                                fajr.setText(fajrAzan);


                                String fajrJamah = productObject.getString("fajr_jamah");
                                jfajr.setText(fajrJamah);


                                String zuhrAzan = productObject.getString("zuhr");
                                zuhr.setText(zuhrAzan);

                                String zuhrJammah = productObject.getString("zuhr_jamah");
                                jzuhr.setText(zuhrJammah);

                                String asrAzan = productObject.getString("asr");
                                asr.setText(asrAzan);

                                String asrJammah = productObject.getString("asr_jamah");
                                jasr.setText(asrJammah);

                                String maghribAzan = productObject.getString("maghrib");
                                maghrib.setText(maghribAzan);

                                String maghribJammah = productObject.getString("maghrib_jamah");
                                jmaghrib.setText(maghribJammah);


                                String eshaAzan = productObject.getString("isha");
                                esha.setText(eshaAzan);

                                String eshaJammah = productObject.getString("isha_jamah");
                                jesha.setText(eshaJammah);


                                String sun = productObject.getString("sunrise");
                                sunrise.setText(sun);


                            }


                        } catch (JSONException e) {

                            // Toast.makeText(PrayerActivity.this, "catch executed", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(PrayerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PrayerActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);

    }

}

