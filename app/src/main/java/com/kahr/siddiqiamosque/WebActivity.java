
package com.kahr.siddiqiamosque;
import androidx.appcompat.app.AppCompatActivity;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.graphics.Color.parseColor;
import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WebActivity extends AppCompatActivity {

    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    WebView webView;
    Button mute, unMute, escape;
    ImageView muteIcon;
    BottomNavigationView bottomBar;
    TextView off, on, mode, volume, soundStatus;
    String streamingUrl;
    public int r = 0, currentVolume;
    View v;

    Animation blinking;

    private static final String insertUrl = "http://onpointglobal.co.uk/mkcjm/insert.php";
    private static final String jsonStatus = "http://onpointglobal.co.uk/mkcjm/jsonStatus.php";
    private static final String getEventsInfo_Url = "https://onpointglobal.co.uk/mkcjm/events.php";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String EM = "em";

    private Handler mHandler = new Handler();
    private Handler handler = new Handler();
    AudioManager manager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        FirebaseMessaging.getInstance().subscribeToTopic("mkcjmEvents");

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE,
                                WebActivity.this, RC_APP_UPDATE);

                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {

                    //        Toast.makeText(WebActivity.this, "No Update Found", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //   mAppUpdateManager.registerListener(installStateUpdatedListener);


        getWindow().addFlags(FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(FLAG_TURN_SCREEN_ON);


        manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        progressDialog = new ProgressDialog(WebActivity.this);
        progressDialog.setMessage("Checking for Streaming..");


        off = findViewById(R.id.offline);
        on = findViewById(R.id.online);
        mode = findViewById(R.id.mode);
        volume = findViewById(R.id.volume);
        soundStatus = findViewById(R.id.soundStatus);


        off.setVisibility(View.INVISIBLE);
        on.setVisibility(View.INVISIBLE);
        webView = findViewById(R.id.mWebview);

        blinking = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);


/*
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(WebActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WebActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }


 */
        checkInternetStatus();

        getAvailableEvents();

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String uN = settings.getString("Mode", "").toString();

        //    Toast.makeText(this, "selected mode "+uN, Toast.LENGTH_SHORT).show();

        if (uN.contentEquals("")) {
            FirebaseMessaging.getInstance().subscribeToTopic("ApplicationMode");
            SharedPreferences sf = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = sf.edit();
            editor.putString("Mode", "Application");
            editor.commit();
            mode.setText("Application");
        }
        if (uN.contentEquals("Radio")) {
            mode.setText(" Radio");
        }

        if (uN.contentEquals("Application")) {
            mode.setText("Application");
        }

        if (uN.contentEquals("No Alert Mode")) {
            mode.setText(" No Alerts");
        }


        bottomBar = findViewById(R.id.bottom_navigationBar);
        bottomBar.setSelectedItemId(R.id.nav_LiveStreaming);
        /* loadData();
        checkExpiry();
        loadProducts();*/


        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nave_hadith:

                        Intent nt = new Intent(WebActivity.this, HadithActivity.class);
                        startActivity(nt);
                        overridePendingTransition(0, 0);
                        mHandler.removeCallbacks(mToastRunnable);
                        finish();
                        break;

                    case R.id.nav_prayerTiming:

                        Intent it = new Intent(WebActivity.this, PrayerActivity.class);
                        startActivity(it);
                        overridePendingTransition(0, 0);
                        mHandler.removeCallbacks(mToastRunnable);
                        finish();
                        break;

                    case R.id.nav_events:

                        Intent mt = new Intent(WebActivity.this, EventActivity.class);
                        startActivity(mt);
                        overridePendingTransition(0, 0);
                        mHandler.removeCallbacks(mToastRunnable);
                        finish();
                        break;

                }

                return true;
            }
        });

  /*      new Handler(this.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
               BadgeDrawable badgeDrawable = bottomBar.getOrCreateBadge(R.id.nav_events);
                badgeDrawable.setVisible(true);
        //        badgeDrawable.setVerticalOffset(dpToPx(WebActivity.this,10));
                badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));


            }
        }, 1000);
*/

        mute = findViewById(R.id.muteButton);
        unMute = findViewById(R.id.unMuteButton);
        muteIcon = findViewById(R.id.iconImage);
        unMute.setVisibility(View.INVISIBLE);
        muteIcon.setVisibility(View.INVISIBLE);
        escape = findViewById(R.id.Exit);



        this.getSupportActionBar().setTitle("Live Streaming Player");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(parseColor("#00101F")));

        OnTheTopRequest();


        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

                volume.setText("Muted");
                volume.setTextColor(Color.parseColor("#ED0000"));

                mute.setVisibility(View.INVISIBLE);
                unMute.setVisibility(View.VISIBLE);
                muteIcon.setVisibility(View.VISIBLE);

            }
        });

        unMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (manager.isMusicActive()) {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7,7);
                    volume.setText("7");
                    volume.setTextColor(Color.parseColor("#9CC4E9"));
                    mute.setVisibility(View.VISIBLE);
                    unMute.setVisibility(View.INVISIBLE);
                    muteIcon.setVisibility(View.INVISIBLE);
                } else {
                    volume.setText("No Broadcast");
                    mute.setVisibility(View.VISIBLE);
                    unMute.setVisibility(View.INVISIBLE);
                    muteIcon.setVisibility(View.INVISIBLE);
                    volume.setTextColor(Color.parseColor("#9CC4E9"));
                }

            }
        });

        escape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure to exit the app?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>YES</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        finishAffinity();
                        System.exit(0);
                    }
                });

                builder.setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                Drawable drawable = getResources().getDrawable(R.drawable.dialog_background);
                alert.getWindow().setBackgroundDrawable(drawable);

                alert.show();


            }
        });


    }


    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }


    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {

            //   Toast.makeText(WebActivity.this, "This is delayed Toast", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(mToastRunnable, 5000);

            if (manager.isMusicActive()) {
                progressDialog.dismiss();

                // AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                // audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 10);
                Toast.makeText(WebActivity.this, "Connection Established Successfully", Toast.LENGTH_SHORT).show();
                volume.setText("10");
                mHandler.removeCallbacks(mToastRunnable);

            } else {
                r++;
                if (r == 2) {
                    //    Toast.makeText(WebActivity.this, "10 secs ", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    mHandler.removeCallbacks(mToastRunnable);
                }

            }
        }
    };


    public void checkInternetStatus() {
        if (AppStatus.getInstance(this).isOnline()) {
            loadData();
//            checkExpiry();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                getStreamingURL();
            }

        } else {
            InternetAlert();
        }
    }


    public void InternetAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this, R.style.AlertDialogStyle);

        builder.setTitle("No Internet Connection")
                .setMessage("Check your  Internet Connection or Try again"
                )

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("", null);
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#1174d1")));
    }


    private void getStreamingURL() {

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray products = new JSONArray(response);
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);

                                streamingUrl = productObject.getString("StreamingURL");
                                // current url "https://bbb.dynamicblend.co.uk:8001/stream"

                                Log.d("url", "found url is " + streamingUrl);

                                webView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        if (url.equals(streamingUrl)) {

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        checkBroadCastingStatus();
                                                    }
                                                }, 2000);
                                            }
                                        }
                                    }
                                });
                                webView.loadUrl(streamingUrl);
                            }

                        } catch (JSONException e) {

                            Toast.makeText(WebActivity.this, "error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(WebActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);

    }

    public void start_Repeating(View v) {
        handlerRunnable.run();
    }

    public void stop_Repeating(View v) {

        handler.removeCallbacks(handlerRunnable);
    }


    private Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            //          Toast.makeText(WebActivity.this, "This is delayed Toast", Toast.LENGTH_SHORT).show();

            //  checkBroadCastingStatus();

            if (manager.isMusicActive()) {
                manager.getMode();

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    checkBroadCastingStatus();
                }
            }

            handler.postDelayed(handlerRunnable, 5000);

        }
    };

    /*
        public void retrieveBroadcastingStatus() {

            getStreamingURL();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://mkcjm-org-uk.host3.dynamicblend.co.uk:65533/status-json.xsl",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                jsonObject1 = obj.getJSONObject("icestats");

                                int number = jsonObject1.toString().length();
                                items = String.valueOf(number);

                                webView.setWebViewClient(new WebViewClient());

                                if (items.contentEquals("228")) {
                                    off.setVisibility(View.VISIBLE);
                                    on.setVisibility(View.INVISIBLE);
                                    soundStatus.setText("Sound Status");
                                    //    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                     //   audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                    stop_Repeating(v);
                                } else {
                                    webView.loadUrl(url);
                                    off.setVisibility(View.INVISIBLE);
                                    on.setVisibility(View.VISIBLE);
                                    progressDialog.show();
                                    manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                    currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    String vol = String.valueOf(currentVolume);
                                    volume.setText(vol);
                                    mHandler.postDelayed(mToastRunnable, 5000);
                                    start_Repeating(v);
                                }

                                //228 means streaming is off


                                //  Toast.makeText(WebActivity.this, "" + jsonObject1.toString().length(), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {

                                Toast.makeText(WebActivity.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(WebActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
    */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkBroadCastingStatus() {

        AudioManager manager;

        manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        if (manager.isMusicActive()) {

            progressDialog.dismiss();
            off.setVisibility(View.INVISIBLE);
            on.setVisibility(View.VISIBLE);
            on.startAnimation(blinking);
            manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
            String vol = String.valueOf(currentVolume);
            volume.setText(vol);
            mHandler.postDelayed(mToastRunnable, 5000);

            progressDialog.dismiss();
            start_Repeating(v);

        } else {

            off.setVisibility(View.VISIBLE);
            on.setVisibility(View.INVISIBLE);
            soundStatus.setText("Sound Status");
            //    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //   audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

            progressDialog.dismiss();
            stop_Repeating(v);
        }

    }

    /*
        public void retrieveOnlyBroadcastingStatus() {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://mkcjm-org-uk.host3.dynamicblend.co.uk:65533/status-json.xsl",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject obj = new JSONObject(response);

                                jsonObject1 = obj.getJSONObject("icestats");

                                int number = jsonObject1.toString().length();
                                items = String.valueOf(number);

                                webView.setWebViewClient(new WebViewClient());

                                //228 means streaming is off

                                if (items.contentEquals("228")) {
                                    off.setVisibility(View.VISIBLE);
                                    on.setVisibility(View.INVISIBLE);
                                    soundStatus.setText("Sound Status");
                                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                    Toast.makeText(WebActivity.this, "Broadcasting Stopped", Toast.LENGTH_SHORT).show();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                    stop_Repeating(v);
                                }
                                //  Toast.makeText(WebActivity.this, "" + jsonObject1.toString().length(), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {

                                Toast.makeText(WebActivity.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(WebActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
    */
  /*
    public void SubscriptionAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this, R.style.AlertDialogStyle);


        builder.setTitle("Subscription Required")
                .setMessage("To listen live broadcasting from the MKCJM Mosque including five time prayer's Azan, Friday's Sermon and " +
                        "other events in the Mosque on your mobile device." + "\n\n" + "Please Contact MKCJM Mosque Administration Department to get subscription." +
                        "\n\n" + "Already Have Subscription? Press Continue"
                )

                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(WebActivity.this, SubscriptionActivity.class));

                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#00101F")));

    }
*/
/*
    public void ResubscriptionAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this, R.style.AlertDialogStyle);


        builder.setTitle("Subscription Expired")
                .setMessage("Your Subscription has been expired! Please Re-subscribe for MKCJM Live Streaming service" + "\n\n" + "Please Contact MKCJM Mosque Administration Department to get subscription." +
                        "\n\n" + "Already Have Subscription? Press Continue"
                )

                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(WebActivity.this, SubscriptionActivity.class));

                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#AA0D22")));

    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.web, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Preferences:

                startActivity(new Intent(WebActivity.this, PreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (webView.canGoBack()) {
            // webView.goBack();

        } else {
            //    super.onBackPressed();
        }
    }

    public void OnTheTopRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                permissionAlert();
            } else {

                if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 1);
                }
            }
        }
    }

    public void permissionAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this, R.style.AlertDialogStyle);

        builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Permission Required</font>"))
                .setMessage(Html.fromHtml("<font color='#FFFFFF'>MKCJM app required permission to draw on screen, on the next page" +
                        " scroll down to MKCJM app and allow permission</font>"
                ))

                .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>OK</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);
                    }
                })
                .setNegativeButton("", null);
        AlertDialog alert = builder.create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#1174d1")));
    }


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String status = sharedPreferences.getString(TEXT, "notSubscribed");
        String em = sharedPreferences.getString(EM, "email");


        //      Toast.makeText(this, "email is "+em, Toast.LENGTH_SHORT).show();

        if (status.contentEquals("notSubscribed")) {
            // SubscriptionAlert();
        }

        // Toast.makeText(this, "the value is..."+status, Toast.LENGTH_SHORT).show();

    }


    private void getAvailableEvents() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEventsInfo_Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray products = new JSONArray(response);

                            if (products.length() != 0) {
                                BadgeDrawable badgeExplorer = bottomBar.getOrCreateBadge(R.id.nav_events);
                                badgeExplorer.setVisible(true);
                                badgeExplorer.setNumber(products.length());
                                badgeExplorer.setBackgroundColor(getResources().getColor(R.color.red));
                                badgeExplorer.setBadgeTextColor(getResources().getColor(R.color.white));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(WebActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);

    }


    private void checkExpiry() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, insertUrl,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray products = new JSONArray(response);

                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            String email = sharedPreferences.getString(EM, "NULL");

                            //     Toast.makeText(WebActivity.this, "checking"+email, Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < products.length(); i++) {

                                JSONObject productObject = products.getJSONObject(i);

                                String name = productObject.getString("Email").toString();
                                String t = productObject.getString("ExpiryDate").toString();
                                //   Toast.makeText(MainActivity.this, ""+fajrAzan+ "\n"+""+t, Toast.LENGTH_SHORT).show();

                                if (name.contentEquals(email)) {
//                                    Toast.makeText(WebActivity.this, "name found", Toast.LENGTH_SHORT).show();

                                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    String exp = t;

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                    try {
                                        Date todayDate = sdf.parse(today);
                                        Date ExpireDate = sdf.parse(exp);

                                        if (todayDate.compareTo(ExpireDate) > 0) {
                                            //  Toast.makeText(WebActivity.this, "Subscription has been expired", Toast.LENGTH_SHORT).show();

                                            FirebaseMessaging.getInstance().unsubscribeFromTopic("Radio");
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic("Application");
//                                            ResubscriptionAlert();
                                        } else {
                                            //   Toast.makeText(WebActivity.this, "not expired", Toast.LENGTH_SHORT).show();
                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }

                            }


                        } catch (JSONException e) {

                            // Toast.makeText(PrayerActivity.this, "catch executed", Toast.LENGTH_SHORT).show();
                            Toast.makeText(WebActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(WebActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }


    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {

            if (state.installStatus() == InstallStatus.DOWNLOADED) {
               //showCompletedUpdate();
                Toast.makeText(WebActivity.this, "New App is ready to installed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onStop() {
       /* if (mAppUpdateManager != null)
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);*/
        super.onStop();
    }

/*
    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.content), "New APP is Ready",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppUpdateManager.completeUpdate();
            }
        });

        snackbar.show();
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_APP_UPDATE && requestCode != RESULT_OK) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE,
                                WebActivity.this, RC_APP_UPDATE);

                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {

                    //        Toast.makeText(WebActivity.this, "No Update Found", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:


                if (manager.isMusicActive()) {
                    manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    unMute.setVisibility(View.INVISIBLE);
                    mute.setVisibility(View.VISIBLE);
                    manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    volume.setTextColor(Color.parseColor("#9CC4E9"));
                    String vol = String.valueOf(currentVolume);
                    volume.setText(vol);
                    muteIcon.setVisibility(View.INVISIBLE);
                    return true;
                } else {

                    manager.adjustStreamVolume(AudioManager.STREAM_RING,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    volume.setTextColor(Color.parseColor("#9CC4E9"));
                    muteIcon.setVisibility(View.INVISIBLE);

                    Toast.makeText(this, "Mosque is Not Broadcasting", Toast.LENGTH_SHORT).show();

                }

                return true;


            case KeyEvent.KEYCODE_VOLUME_DOWN:


                if (manager.isMusicActive()) {
                    manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

                    manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);

                    volume.setTextColor(Color.parseColor("#9CC4E9"));

                    String dvol = String.valueOf(currentVolume);
                    volume.setText(dvol);
                    muteIcon.setVisibility(View.INVISIBLE);
                } else {
                    manager.adjustStreamVolume(AudioManager.STREAM_RING,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
  /*  manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                 currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
                 String vol = String.valueOf(currentVolume);
                 volume.setText(vol);*/
                    Toast.makeText(this, "Mosque is Not Broadcasting", Toast.LENGTH_SHORT).show();
                }

                return true;

            default:
                return false;
        }
    }
}
