package com.kahr.siddiqiamosque;


import static android.graphics.Color.parseColor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {


    ListView listView;
    MyAdapter adapter;
    public static ArrayList<Events> EventArrayList = new ArrayList<>();
    Events events;
    private ProgressDialog progressDialog;
    BottomNavigationView bottomBar;
    TextView noEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setTitle("Siddiqia Masjid Events");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(parseColor("#00101F")));


        noEvents = findViewById(R.id.noEventFound);

        bottomBar = findViewById(R.id.bottom_navigationBar);
        bottomBar.setSelectedItemId(R.id.nav_events);
        listView = findViewById(R.id.myListView);
        adapter = new MyAdapter(this, EventArrayList);
        listView.setAdapter(adapter);


        EventArrayList.clear();
        retrieveData();

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_LiveStreaming:

                        Intent nt = new Intent(EventActivity.this, WebActivity.class);
                        startActivity(nt);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.nav_prayerTiming:

                        Intent it = new Intent(EventActivity.this, PrayerActivity.class);
                        startActivity(it);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.nave_hadith:

                        Intent mt = new Intent(EventActivity.this, HadithActivity.class);
                        startActivity(mt);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                }

                return true;
            }
        });


    }


    public void retrieveData() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://onpointglobal.co.uk/mkcjm/events.php",
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        EventArrayList.clear();
                        try {

                            JSONArray products = new JSONArray(response);


                            if (products.length() != 0) {

                                noEvents.setVisibility(View.INVISIBLE);
                                for (int i = 0; i < products.length(); i++) {
                                    JSONObject productObject = products.getJSONObject(i);
                                    String eventName = productObject.getString("eventName");
                                    String description = productObject.getString("description");
                                    String eventTime = productObject.getString("eventTime");
                                    String eventDate = productObject.getString("eventDate");

                                    if(eventTime.contentEquals(""))
                                    {
                                        eventTime = "not required";
                                    }

                                    events = new Events(eventName, description, "Date: " + eventDate + "       Time: " + eventTime);
                                    EventArrayList.add(events);
                                    adapter.notifyDataSetChanged();

                                }
                            }

                        } catch (JSONException e) {
                            listView.setVisibility(View.INVISIBLE);
                            //   Toast.makeText(UserRecordListActivity.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(EventActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
        Intent it = new Intent(EventActivity.this, WebActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.slide_from_eft, R.anim.slide_to_right);

    }
}