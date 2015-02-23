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
    int countdown = 33;

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
            mainActivity.mepView.setVisibility(View.VISIBLE);

            mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
            mainActivity.okView.setVisibility(View.INVISIBLE);
            mainActivity.mepView.setVisibility(View.INVISIBLE);
        } else {

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
            calculate(currentSzpl);
            getOptSpeed(currentSzpl.getGreenTo());
        }
    }

    protected void calculate(SZPL szpl){
        final int greenFrom = szpl.getGreenFrom();
        final int greenTo = szpl.getGreenTo();
        final int redFrom = greenTo + 1;

        Date today = new Date();
        today.setTime(greenFrom*1000);
        Log.d("today","set Time" + today);

        final Timer myTimer = new Timer();
        if(run) {
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    UpdateGUI(greenFrom, greenTo);
                }
            }, 0, 1000);
        }
        run = false;
    }

    private void UpdateGUI(int greenFrom, int greenTo) {
       c.setTime(new Date());
        int currentSecond = c.get(Calendar.SECOND);
       // Log.d("###", "currentSecond " +currentSecond);

        if(currentSecond>=greenFrom&&currentSecond<=greenTo){
           // Log.d("###", "Ampel ist grün");

        } else {
            Log.d("#","Ampel ist rot "+"\ncurr: "+ currentSecond + " \ngreenFrom:" + greenFrom+" \ngreento " +greenTo );
            if(currentSecond < greenFrom) {
                countdown = greenFrom - currentSecond;
            } else {
                countdown = (60-currentSecond)+greenTo;
            }
        }
        if(mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
            myHandler.post(myRunnable);
        }
    }

    private void getOptSpeed(int greenTo){
        Log.d("getOptSpeed", "getOptSpeed");
        // v = s / t2-t1  t1=currentSecond, t2=ampel schaltet auf rot s=abstand ampel-Rad
        c.setTime(new Date());
        int t1 = c.get(Calendar.SECOND);
        int t2 = greenTo + 1; // ampel schaltet auf rot
        if(t2<t1 || myLocation == null || lsaLocation == null) {
            return;
        }
        double deltaT = t2 - t1;
        double s = myLocation.distanceTo(lsaLocation);
        double v = s / deltaT;
        Log.d("v: " , v +"\n in km/h: " + (v*3.6));

        double speed = myLocation.getSpeed();
        if ((v*3.6) >= Constants.MAX_SPEED || (v*3.6) <= Constants.MIN_SPEED){
            mainActivity.xView.setVisibility(View.VISIBLE);

            mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
            mainActivity.okView.setVisibility(View.INVISIBLE);
            mainActivity.mepView.setVisibility(View.INVISIBLE);

        } else if (Math.round(speed) == Math.round(v)) {
            mainActivity.okView.setVisibility(View.VISIBLE);

            mainActivity.xView.setVisibility(View.INVISIBLE);
            mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
            mainActivity.mepView.setVisibility(View.INVISIBLE);
        } else if (speed < v) {
            mainActivity.countdownTextView.setVisibility(View.VISIBLE);

            mainActivity.okView.setVisibility(View.INVISIBLE);
            mainActivity.xView.setVisibility(View.INVISIBLE);
            mainActivity.mepView.setVisibility(View.INVISIBLE);
        } else if (speed > v) {
            mainActivity.countdownTextView.setVisibility(View.VISIBLE);

            mainActivity.okView.setVisibility(View.INVISIBLE);
            mainActivity.xView.setVisibility(View.INVISIBLE);
            mainActivity.mepView.setVisibility(View.INVISIBLE);
        }


    }

    final Runnable myRunnable = new Runnable() {

        public void run() {
            mainActivity.countdownTextView.setText(String.valueOf(countdown));
        }
    };
}
