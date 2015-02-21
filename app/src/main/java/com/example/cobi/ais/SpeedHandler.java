package com.example.cobi.ais;

import android.os.Handler;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cobi on 18.02.15.
 */
public class SpeedHandler{

    Calendar c = Calendar.getInstance(Locale.GERMANY);
    final Handler myHandler = new Handler();
    protected MainActivity mainActivity;

    int countdown = 33;

    public SpeedHandler(MainActivity a) {
        this.mainActivity = a;
    }


    protected void calculate(final SZPL szpl){
        final int greenFrom = szpl.getGreenFrom();
        final int greenTo = szpl.getGreenTo();
        final int redFrom = greenTo + 1;
        countdown = greenTo +1 +greenFrom-1;
        Date today = new Date();
        today.setTime(greenFrom*1000);
        Log.d("today","set Time" + today);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateGUI(greenFrom, greenTo);
            }
        }, 0, 1000);
    }

    private void UpdateGUI(int greenFrom, int greenTo) {
        //TODO beim 2. Aufruf wird doppelt ausgeführt. tmp variable?
        c.setTime(new Date());
        int currentSecond = c.get(Calendar.SECOND);
        //Log.d("###", "currentSecond " +currentSecond);

        if(currentSecond>=greenFrom&&currentSecond<=greenTo){
           // Log.d("###", "Ampel ist grün");
        } else {
           // Log.d("###","Ampel ist rot");
           // getRedCountdown();
            countdown--;
        }

        myHandler.post(myRunnable);
    }

    private void getRedCountdown(){

    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            if (countdown >= 0 || countdown <=60) {
                mainActivity.countdownTextView.setText(String.valueOf(countdown));
            }
            // mainActivity.okView.setVisibility(View.INVISIBLE);
            // mainActivity.okView.setVisibility(View.VISIBLE);
        }
    };
}
