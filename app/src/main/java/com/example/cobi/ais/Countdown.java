/*
package com.example.cobi.ais;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimerTask;

*/
/**
 * Created by cobi on 19.02.15.
 *//*

public class Countdown extends TimerTask {
    private SpeedHandler speedHandler = new SpeedHandler();
    private SZPL currentSzpl = speedHandler.currentSzpl;

    Date today = new Date();
    Calendar c = Calendar.getInstance(Locale.GERMANY);


    @Override
    public void run() {
        c.setTime(today);
        Log.d("###", "Ampel ist grün von " + currentSzpl.getGreenFrom());
        int greenFrom = currentSzpl.getGreenFrom();
        int greenTo = currentSzpl.getGreenTo();


        int currentSecond = c.get(Calendar.SECOND);

        if(currentSecond>=currentSzpl.getGreenFrom()&&currentSecond<=currentSzpl.getGreenTo()){
            Log.d("###", "Ampel ist grün");
        } else {
            Log.d("###","Ampel ist rot");
        }
    }
}
*/
