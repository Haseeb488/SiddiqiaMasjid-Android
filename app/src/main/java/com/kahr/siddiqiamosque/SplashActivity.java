package com.kahr.siddiqiamosque;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    TextView harpreet_text,studio_text;
    Animation fade_out_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        harpreet_text = findViewById(R.id.harpreet_text);
        studio_text = findViewById(R.id.studio_text);
        fade_out_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        harpreet_text.startAnimation(fade_out_anim);
        studio_text.startAnimation(fade_out_anim);


/*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("djisjis","printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("djisjis", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("djisjis", "printHashKey()", e);
        }

*/

        Thread td = new Thread() {

            public void run() {
                try {
                    sleep(5000);

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {
                    Intent intent = new Intent(SplashActivity.this, DecisionActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            }

        };
        td.start();
    }
}