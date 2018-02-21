package com.dewnaveen.olaplaystudios;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dewnaveen.olaplaystudios.Class.Utils;


public class SplashActivity extends AppCompatActivity {

    Activity mAct = this;
    Handler handler;
    private Intent intentMain  = null;
    Runnable runnable = new Runnable() {

        public void run() {
            intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overridePendingTransition(0, 0);
            startActivity(intentMain);
            finish();
        }
    };

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    private boolean isUserFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            View decorView = getWindow().getDecorView();
            // hide the navigation bar.
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        setContentView(R.layout.activity_splash);

        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(SplashActivity.this, PREF_USER_FIRST_TIME, "true"));

        if(isUserFirstTime){

            intentMain = new Intent(SplashActivity.this, IntroActivity.class);
        }else {
            intentMain = new Intent(SplashActivity.this, MainActivity.class);

        }

        handler = new Handler();
        handler.postDelayed(runnable, 1600);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    @Override
    protected void onResume() {
        handler = new Handler();
        handler.postDelayed(runnable, 1600);
        super.onResume();
    }
}
