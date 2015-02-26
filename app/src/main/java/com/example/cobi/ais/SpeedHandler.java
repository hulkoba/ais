package com.example.cobi.ais;


import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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

    private Location lsaLocation;
    private Location myLocation;
    private boolean run = true;
    int countdown;

    private boolean x, ok, up, upper, down, downer = false;

    public SpeedHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // Schaltplan der nächsten LSA holen
    protected void getCurrentSzpl(LSA nearestLSA, Location loc){
        Log.d("getCurrentSzpl", "getCurrentSzpl");

        lsaLocation = nearestLSA.getLsaLocation();
        myLocation = loc;
        SZPL currentSzpl = null;

        c.setTime(new Date());
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        // verkehrsabhängige LSA hat keinen Schaltplan
        if(nearestLSA.isDependsOnTraffic()){
            if(mainActivity.mepView.getVisibility() == View.INVISIBLE) {
                mainActivity.mepView.setVisibility(View.VISIBLE);
            }

            if(mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.okView.getVisibility() == View.VISIBLE) {
                mainActivity.okView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.mepView.getVisibility() == View.VISIBLE) {
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.downView.getVisibility() == View.VISIBLE) {
                mainActivity.downView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.upView.getVisibility() == View.VISIBLE) {
                mainActivity.upView.setVisibility(View.INVISIBLE);
            }          
        } else {
            if(mainActivity.mepView.getVisibility() == View.VISIBLE) {
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            }

            SZPL[] szpls = nearestLSA.getSzpls();

            for (SZPL szpl : szpls) {
                for (int i : szpl.getDays()) {
                    if (i == c.get(Calendar.DAY_OF_WEEK) && szpl.getTimeFrom() <= hourOfDay && szpl.getTimeTo() >= hourOfDay) {
                        currentSzpl = szpl;
                    }
                }
            }
        }

        if(currentSzpl != null) {
            getOptSpeed(currentSzpl);
        }
    }

    /*
     * Geschwindigkeit berechnen
     * v = s / (t2-t1)
     * t1 = aktuelle Sekunde
     * t2 = Ampel schaltet auf rot
     * s = Abstand zwischen Fahrrad und Ampel
     * speed = eigene Geschwindigkeit
     *
     * Timer updatet jede Sekunde die GUI
     */
    protected void getOptSpeed(SZPL szpl){
        final int greenFrom = szpl.getGreenFrom();
        final int greenTo = szpl.getGreenTo();
        final float speed = myLocation.getSpeed();

        c.setTime(new Date());
        int t1 = c.get(Calendar.SECOND);
        int t2 = greenTo + 1;
        if(t2<t1 || myLocation == null || lsaLocation == null) {
            return;
        }
        float deltaT = t2 - t1;
        float s = myLocation.distanceTo(lsaLocation);

        final double v = s / deltaT;
        Log.d("v: " , v +"\n in km/h: " + (v*3.6));

        final Timer myTimer = new Timer();
        if(run) {
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    UpdateGUI(greenFrom, greenTo, speed, v);
                }
            }, 0, 1000);
        }
        run = false;
    }

    /*
     * countdown berechnen
     */
    private void setCountdown(int greenFrom, int greenTo){
        c.setTime(new Date());
        int currentSecond = c.get(Calendar.SECOND);

        if(currentSecond>=greenFrom && currentSecond<=greenTo || countdown < 0){
            //Ampel ist grün
            countdown = 0;
        }else {
            // Ampel ist rot
            if(currentSecond < greenFrom) {
                countdown = greenFrom - currentSecond;
            } else {
                countdown = (60-currentSecond) + greenFrom;
            }
            Log.d("#","\nAmpel ist rot "+"\ncurr: "+ currentSecond + " \ngreenFrom:" + greenFrom+" \ngreento " +greenTo +"\n countdown"+countdown );
        }
    }

    private void UpdateGUI(int greenFrom, int greenTo, double speed, double v) {

        setCountdown(greenFrom, greenTo);

        if ((v*3.6) >= Constants.MAX_SPEED || (v*3.6) <= Constants.MIN_SPEED){
            x=true;
            ok = false; up = false; upper=false; down=false; downer = false;

        } else if (Math.round(speed) == Math.round(v)) {
            ok=true;
            x = false; up = false; upper=false; down=false; downer = false;

            //langsamer als empfohlen --> schneller fahren
        } else if (speed < v) {
            up=true;
            ok = false; x = false; upper=false; down=false; downer = false;

            //schneller als empfohlen --> langsamer fahren
        } else if (speed > v) {
            down=true;
            ok = false; x = false; upper=false; up=false; downer = false;
        }

        if(mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
            myHandler.post(myRunnable);
        }
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            if (mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
                mainActivity.countdownTextView.setText(String.valueOf(countdown));
            }
            if (x){
                mainActivity.xView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (ok){
                mainActivity.okView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (up) {
                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.upView.setVisibility(View.VISIBLE);

                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (down) {
                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.downView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            }
        }
    };
}
