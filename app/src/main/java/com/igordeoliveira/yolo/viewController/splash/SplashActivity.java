package com.igordeoliveira.yolo.viewController.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.igordeoliveira.yolo.Constants;
import com.igordeoliveira.yolo.R;
import com.igordeoliveira.yolo.viewController.home.HomeActivity;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends Activity {
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        timer.schedule(new TimerTask() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1 * 1000);
    }
}