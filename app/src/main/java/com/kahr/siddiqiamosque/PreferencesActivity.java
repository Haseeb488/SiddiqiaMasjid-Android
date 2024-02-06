package com.kahr.siddiqiamosque;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class PreferencesActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        radioGroup = findViewById(R.id.radioGroup);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Preferences</font>"));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.3));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Please set preference to listen livestream", Toast.LENGTH_SHORT).show();
    }

    public void checkButton(View view) {
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);

        String selected = radioButton.getText().toString();

        if (selected.equals("Radio Mode")) {

            RadioMessage();
        /*    FirebaseMessaging.getInstance().subscribeToTopic("Radio");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Application");
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Mode", "Radio");
            editor.commit();
            Toast.makeText(this, "Preference set to Radio Mode", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
*/

        }

        if (selected.equals("Application Mode")) {

            ApplicationMessage();

          /*  FirebaseMessaging.getInstance().subscribeToTopic("Application");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Radio");
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Mode", "Application");
            editor.commit();
            Toast.makeText(this, "Preference set to Application Mode", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        */
        }


        if (selected.equals("No Alert Mode")) {

            noAlertMessage();
          /*  FirebaseMessaging.getInstance().subscribeToTopic("Application");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Radio");
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Mode", "Application");
            editor.commit();
            Toast.makeText(this, "Preference set to Application Mode", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        */
        }

    }

    public void RadioMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);

        // Set the custom style for the message text
        builder.setMessage(Html.fromHtml("<font color='#FFFFFF'>Radio Mode automatically launches MKCJM app and starts live streaming when broadcasting starts from the mosque</font>" +
                "<br><br>Are you sure you want to select Radio Mode?"));

        builder.setTitle("Radio Mode")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseMessaging.getInstance().subscribeToTopic("RadioTest");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("ApplicationTest");

                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("Mode", "Radio");
                        editor.commit();
                        Toast.makeText(PreferencesActivity.this, "Preference set to Radio Mode", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(PreferencesActivity.this, WebActivity.class);
                        startActivity(it);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();

        // Set the text color of the message
        TextView messageView = alert.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

  /*  public void ApplicationMessage()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);

        builder.setTitle("Application Mode")
                .setMessage("Application Mode will not automatically launch MKCJM app however a notification will be received when broadcasting starts from the Mosque. By tapping on the notification you can listen live streaming from Mosque through you mobile device " +"\n"+"\n"+
                        "Are you sure to select Application Mode?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseMessaging.getInstance().subscribeToTopic("ApplicationTest");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("RadioTest");
                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("Mode", "Application");
                        editor.commit();
                        Toast.makeText(PreferencesActivity.this, "Preference set to Application Mode", Toast.LENGTH_SHORT).show();

                        Intent it = new Intent(PreferencesActivity.this,WebActivity.class);
                        startActivity(it);
                        overridePendingTransition(0,0);
                        finish();
                    }
                })
                .setNegativeButton("Cancel",null);

        AlertDialog alert = builder.create();
        alert.show();

    }
*/
  public void ApplicationMessage() {
      AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);

      // Set the custom style for the message text
      builder.setMessage(Html.fromHtml("<font color='#FFFFFF'>Application Mode will not automatically launch MKCJM app; however, you will receive a notification when broadcasting starts from the Mosque. By tapping on the notification, you can listen to live streaming from the Mosque through your mobile device.</font>" +
              "<br><br>Are you sure you want to select Application Mode?"));

      builder.setTitle("Application Mode")
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      FirebaseMessaging.getInstance().subscribeToTopic("ApplicationTest");
                      FirebaseMessaging.getInstance().unsubscribeFromTopic("RadioTest");
                      SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                      SharedPreferences.Editor editor = settings.edit();
                      editor.putString("Mode", "Application");
                      editor.commit();
                      Toast.makeText(PreferencesActivity.this, "Preference set to Application Mode", Toast.LENGTH_SHORT).show();

                      Intent it = new Intent(PreferencesActivity.this, WebActivity.class);
                      startActivity(it);
                      overridePendingTransition(0, 0);
                      finish();
                  }
              })
              .setNegativeButton("Cancel", null);

      AlertDialog alert = builder.create();
      alert.show();

      // Set the text color of the message
      TextView messageView = alert.findViewById(android.R.id.message);
      if (messageView != null) {
          messageView.setTextColor(getResources().getColor(android.R.color.white));
      }
  }
    public void noAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);

        // Set the custom style for the message text
        builder.setMessage(Html.fromHtml("<font color='#FFFFFF'>You will no longer receive any notification when the broadcast starts from the Mosque.</font>" +
                "<br><br>Are you sure you want to select No Alert Mode?"));

        builder.setTitle("No Alert Mode")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("ApplicationTest");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("RadioTest");

                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("Mode", "No Alert Mode");
                        editor.commit();

                        Toast.makeText(PreferencesActivity.this, "Preference set to No Alert Mode", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(PreferencesActivity.this, WebActivity.class);
                        startActivity(it);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();

        // Set the text color of the message
        TextView messageView = alert.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

}