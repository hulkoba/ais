package com.example.cobi.ais;

import android.location.Location;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cobi on 18.02.15.
 */
public class SpeedHandler {
    String countdown = "";
    protected SZPL currentSzpl = null;

    Date today = new Date();
    Calendar c = Calendar.getInstance(Locale.GERMANY);



    protected void getNearestLSA(Location myLocation){
        LSA nearestLSA = null;
        float[]currentDistance = new float[1];
        float minDistance = Float.MAX_VALUE;

        LSA[]lsas = JSONParser.getLsaArray();

        for (LSA lsa : lsas) {

            Location.distanceBetween(
                    myLocation.getLatitude(),
                    myLocation.getLongitude(),
                    lsa.getLatitude(),
                    lsa.getLongitude(),
                    currentDistance);

            if (minDistance > currentDistance[0]) {
                minDistance = currentDistance[0];
                nearestLSA = lsa;
            }
        } //iterate lsas end
        getCurrentSzpl(nearestLSA);
    }

    private void getCurrentSzpl(LSA nearest){
       // SZPL currentSzpl = null;
        c.setTime(today);

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        SZPL[] szpls = nearest.getSzpls();

        for (SZPL szpl : szpls){
            for (int i : szpl.getDays()){
                if(i == dayOfWeek && szpl.getTimeFrom()<=hourOfDay && szpl.getTimeTo()>=hourOfDay){
                    currentSzpl = szpl;
                }
            }
        }
      // calculate();
    }

    protected void calculate(){

    }

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
