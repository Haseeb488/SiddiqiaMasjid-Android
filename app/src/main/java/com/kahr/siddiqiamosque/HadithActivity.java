package com.kahr.siddiqiamosque;

import static android.graphics.Color.parseColor;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

public class HadithActivity extends AppCompatActivity {
    BottomNavigationView bottomBar;
    TextView quote,ref;
    CircularDotsLoader loader;
    private static final String Hadith_Url = "http://onpointglobal.co.uk/mkcjm/hadith.php";
    Animation fade_in_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadith);


        getSupportActionBar().setTitle("Daily Hadith");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(parseColor("#00101F")));

        fade_in_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        bottomBar = findViewById(R.id.bottom_navigationBar);
        bottomBar.setSelectedItemId(R.id.nave_hadith);
        quote = findViewById(R.id.quote);
        ref = findViewById(R.id.ref);
        loader = findViewById(R.id.loader);
        loader.setDefaultColor(ContextCompat.getColor(this,R.color.Blue));
        loader.setSelectedColor(ContextCompat.getColor(this,R.color.Blue));
        loader.setBigCircleRadius(80);
        loader.setRadius(20);
        loader.setAnimDur(100);
        loader.setShowRunningShadow(true);
        loader.setFirstShadowColor(ContextCompat.getColor(this, R.color.lightBrown));
        loader.setSecondShadowColor(ContextCompat.getColor(this, R.color.Blue));

        checkInternetStatus();


        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_LiveStreaming:

                        Intent nt = new Intent(HadithActivity.this, WebActivity.class);
                        startActivity(nt);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.nav_prayerTiming:

                        Intent it = new Intent(HadithActivity.this, PrayerActivity.class);
                        startActivity(it);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.nav_events:

                        Intent mt = new Intent(HadithActivity.this, EventActivity.class);
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

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HadithActivity.this, R.style.AlertDialogStyle);

        builder.setTitle("No Internet Connection")
                .setMessage("Check your  Internet Connection or Try again"
                )

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(HadithActivity.this,WebActivity.class);
                        startActivity(it);
                    }
                })
                .setNegativeButton("", null);
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#1174d1")));
    }

    private void loadProducts() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Hadith_Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loader.setVisibility(View.GONE);

                        try {

                            JSONArray products = new JSONArray(response);

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);

                                String hadith = productObject.getString("Hadith");
                                quote.setText(hadith);
                                quote.startAnimation(fade_in_anim);

                                String id = productObject.getString("ID");

                                String Ref = productObject.getString("Ref");
                                ref.setText(Ref);
                                ref.startAnimation(fade_in_anim);




                                Log.i("id","id is "+id);


                            }


                        } catch (JSONException e) {

                            loadProducts();
                            //  Toast.makeText(HadithActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(HadithActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);

    }

}