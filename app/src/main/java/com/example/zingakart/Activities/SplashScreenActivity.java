package com.example.zingakart.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.zingakart.Helper.SessionManager;
import com.example.zingakart.Helper.TrackerGPS_V3;
import com.example.zingakart.R;
import com.example.zingakart.Utils.ConnectivityReceiver;
import com.example.zingakart.app.AppController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreenActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "this";
    public static String add;
    String type;
    SharedPreferences permissionStatus;
    private SessionManager sessionManager;
    Handler handler;
    ConstraintLayout splash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splash = findViewById(R.id.splash);

        if (getIntent().getStringExtra("type") != null) {
            type = getIntent().getStringExtra("type");
        } else {
            type = "skip";
        }

        handler = new Handler();

        sessionManager = new SessionManager(getApplicationContext());
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                session_manage();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
            }
        }, 1000);

    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
    }


    private void session_manage() {
            if (!sessionManager.isLoggedIn() && !sessionManager.isFacebookLoggedIn() && !sessionManager.isGoogleLoggedIn() && type.equalsIgnoreCase("skip")) {
                sessionManager.skip();
                finish();
            } else if (sessionManager.isLoggedIn()) {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.START);
                TransitionManager.beginDelayedTransition(splash, slide);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.START);
                TransitionManager.beginDelayedTransition(splash, slide);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
    }


    @Override
    public void onNetworkConnectionChange(boolean isConnected) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        if (!isConnected) {
            Snackbar snackbar = Snackbar
                    .make(splash, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.RED)
                    .setDuration(1000)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

}