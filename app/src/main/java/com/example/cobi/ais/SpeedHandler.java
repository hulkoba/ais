package com.example.cobi.ais;

import android.location.Location;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimerTask;

/**
 * Created by cobi on 18.02.15.
 */
public class SpeedHandler extends TimerTask{
    String countdown = "";

    Date today = new Date();
    Calendar c = Calendar.getInstance(Locale.GERMANY);


 //   private LSA nearestLSA = null;
    private SZPL currentSzpl = null;

    //private GpsHandler gpsHandler;
    //private static SZPL currentSzpl;

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
        Log.d("+++", nearestLSA + "\n");

        detectCurrentSzpl(nearestLSA);
    }

    private void detectCurrentSzpl(LSA nearest){
       // SZPL currentSzpl = null;
        c.setTime(today);

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        SZPL[] szpls = nearest.getSzpls();
      //  Log.d("+++ szpls +++", String.valueOf(szpls) + "\n");
        for (SZPL szpl : szpls){
      //      Log.d("+++ szpl +++", szpl + "\n");
           // for (int i : szpl.getDays()){
           //     if(i == dayOfWeek && szpl.getTimeFrom()<=hourOfDay && szpl.getTimeTo()>=hourOfDay){
                    //currentSzpl = szpl;
           //     }
           // }
        }
        //Log.d("+++", currentSzpl + "\n");

       //calculate(currentSzpl);
    }

    protected void calculate(SZPL current){
        c.setTime(today);
        int greenFrom = current.getGreenFrom();
        int greenTo = current.getGreenTo();

        int currentSecond = c.get(Calendar.SECOND);

    }

    @Override
    public void run() {
        int currentSecond = c.get(Calendar.SECOND);
        if(currentSecond>=currentSzpl.getGreenFrom()&&currentSecond<=currentSzpl.getGreenTo()){
            Log.d("###","Ampel ist grün");
        } else {
            Log.d("###","Ampel ist rot");
        }
    }

    // And From your main() method or any other method
    // Timer timer = new Timer();
    // timer.schedule(new SpeedHandler(), 0, 1000);
}
