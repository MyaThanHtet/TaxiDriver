package com.lightidea.taxidriver.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.lightidea.taxidriver.MainActivity;
import com.lightidea.taxidriver.R;


public class SplashScreen extends Activity {

    public static final String filename = "TaxiDriverData";
    public static String Key = "flag";
    SharedPreferences sp;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.splash_screen_layout);
        sp = getApplicationContext().getSharedPreferences(filename, Context.MODE_PRIVATE);

        Thread td = new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Okay();

                    finish();


                }
            }
        });
        td.start();
    }

    public void Okay() {
        if (!sp.getString(Key, "").isEmpty()) {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashScreen.this, RegisterActivity.class));
        }
    }

}