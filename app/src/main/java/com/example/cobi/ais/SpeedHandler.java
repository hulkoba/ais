package com.example.cobi.ais;

import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cobi on 18.02.15.
 */
public class SpeedHandler{
    Date today = new Date();
    Calendar c = Calendar.getInstance(Locale.GERMANY);
    final Handler myHandler = new Handler();
    protected MainActivity a;

   // private SZPL currentSzpl;

    int countdown = 33;

    public SpeedHandler(MainActivity a) {
        this.a = a;
    }


    protected void calculate(final SZPL currentSzpl){
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {UpdateGUI(currentSzpl);}
        }, 0, 1000);
    }

    private void UpdateGUI(SZPL currentSzpl) {
        Log.d("###", "Update GUI");
        c.setTime(today);
        int currentSecond = c.get(Calendar.SECOND);
        int greenFrom = currentSzpl.getGreenFrom();
        int greenTo = currentSzpl.getGreenTo();
        Log.d("###", "GREEN " +currentSecond);

        if(currentSecond>=greenFrom&&currentSecond<=greenTo){
           // a.okView.setVisibility(View.VISIBLE);
            Log.d("###", "Ampel ist grün");
        } else {
           // a.okView.setVisibility(View.INVISIBLE);
            Log.d("###","Ampel ist rot");
            countdown--;
        }

        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            if (countdown >= 0) {
                a.countdownTextView.setText(String.valueOf(countdown));
            }
        }
    };
}
