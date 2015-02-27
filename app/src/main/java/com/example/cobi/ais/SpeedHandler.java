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

    // Schaltplan der nächsten LSA holen, wenn keiner Vorhanden --> verkehrsabhängig --> keine Vorausage möglich
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
            if(mainActivity.downerView.getVisibility() == View.VISIBLE) {
                mainActivity.downerView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.upView.getVisibility() == View.VISIBLE) {
                mainActivity.upView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.upperView.getVisibility() == View.VISIBLE) {
                mainActivity.upperView.setVisibility(View.INVISIBLE);
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
     * v = s / (t2 - t1)
     * a = v / (t2 - t1)²
     * t1 = aktuelle Sekunde
     * t2 = Ampel schaltet auf rot
     * s = Abstand zwischen Fahrrad und Ampel
     * a = notwendige Beschleunigung
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

        final float v = s / deltaT;
        final double a = v / Math.pow(deltaT, 2.0);
        Log.d("v: " ,"\n"+ v +"\n in km/h: " + (v*3.6)+"\na= " +a +"\nspeed "+speed);


        final Timer myTimer = new Timer();
        if(run) {
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    UpdateGUI(greenFrom, greenTo, speed, v, a);
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
        }
    }

    /*
     * GUI sekündlich updaten
     */
    private void UpdateGUI(int greenFrom, int greenTo, float speed, float v, double a) {
        Log.d("\nv: " , v +"\n in km/h: " + (v*3.6)+"\na= " +a +"\nspeed "+speed);

        // empfohlene Geschwindigkeit = aktuelles Tempo
        if (Math.round(speed) == Math.round(v)) {
            Log.d("\nok", "ok");
            ok=true;
            x = false; up = false; upper=false; down=false; downer = false;
        } else
        //langsamer als empfohlen --> schneller fahren
        if (speed < v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {
            Log.d("\nx", "schnell");
            setCountdown(greenFrom, greenTo);
            up=true;
            ok = false; x = false; upper=false; down=false; downer = false;

        } else if (speed < v && (speed + Constants.DIFF_SPEED) < v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {
            Log.d("\nx", "schneller²");
            setCountdown(greenFrom, greenTo);
            up=false; upper=true;
            ok = false; x = false; down=false; downer = false;
            //schneller als empfohlen --> langsamer fahren
        } else if (speed > v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {
            Log.d("\nx", "langsamer");
            setCountdown(greenFrom, greenTo);
            down=true;
            ok = false; x = false; upper=false; up=false; downer = false;

            // viel schneller als empfohlen >> viel langsamer fahren
        } else if ( speed > v && (speed - Constants.DIFF_SPEED) > v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {
            Log.d("\nx", "langsamer²");
            setCountdown(greenFrom, greenTo);
            down = false; downer = true;
            ok = false; x = false; upper = false; up = false;
        } else {
            // Geschwindigkeit zu hoch  || Geschwindigkeit zu niedrig || Beschleunigung zu hoch >> anhalten
            Log.d("\nx", "x");
            x=true;
            ok = false; up = false; upper=false; down=false; downer = false;
        }

        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            if (mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
                mainActivity.countdownTextView.setText(String.valueOf(countdown));
            }
            if (x){
                mainActivity.xView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (ok){
                mainActivity.okView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (up) {
                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.upView.setVisibility(View.VISIBLE);

                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (upper) {
                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.upView.setVisibility(View.VISIBLE);
                mainActivity.upperView.setVisibility(View.VISIBLE);

                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (down) {
                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.downView.setVisibility(View.VISIBLE);

                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            } else if (downer) {
                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.downView.setVisibility(View.VISIBLE);
                mainActivity.downerView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            }
        }
    };
}