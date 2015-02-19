package com.example.cobi.ais;

import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cobi on 18.02.15.
 */
public class SpeedHandler {
    String countdown = "21";

    protected MainActivity a;
    protected SZPL currentSzpl;

    int i = 0;
    final Handler myHandler = new Handler();

    public SpeedHandler(MainActivity a, SZPL currentSzpl) {
        this.a = a;
        this.currentSzpl = currentSzpl;
    }

    protected void calculate(){
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {UpdateGUI();}
        }, 0, 1000);
    }

    private void UpdateGUI() {
        i++;
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            a.countdownTextView.setText("#" +countdown + i);
        }
    };
   /* @Override
    public void run() {
        c.setTime(today);
        int currentSecond = c.get(Calendar.SECOND);
        int greenFrom = currentSzpl.getGreenFrom();
        int greenTo = currentSzpl.getGreenTo();

        if(currentSecond>=currentSzpl.getGreenFrom()&&currentSecond<=currentSzpl.getGreenTo()){
            Log.d("###", "Ampel ist grün");
        } else {
            Log.d("###","Ampel ist rot");
        }
    }*/

}
