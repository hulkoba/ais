package com.example.cobi.ais;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by cobi on 13.01.15.
 */
class GpsTracker implements LocationListener {

    private LocationManager locationManager;

    private LSA nearestLSA = null;
    public OnSetListener onSetListener = null;
    private Location myNewLocation = null;

    Calendar c = Calendar.getInstance(Locale.GERMANY);

    private String s;
    public String getS() {
        return s;
    }
    public void setS(String s) {
        this.s = s;
    }

    // Provider verfügbar --> GPS aktiviert???
    public boolean gpsIsActive(Activity a) {
        locationManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    // wird aufgerufen, wenn neue Positionsdaten vorhanden sind
    public void onLocationChanged(Location location) {
        setS("Position: \n"
            + String.format("%9.6f", location.getLatitude()) + ", "
            + String.format("%9.6f", location.getLongitude()) + "\n"
            + "Geschwindigkeit: " + location.getSpeed() + " m/s \n"
            + "entspricht" + (location.getSpeed()*3.6) + "km/h \n");

        //location.setLatitude(location.getLatitude());
        //location.setLongitude(location.getLongitude());
        MainActivity.showPosition(getS());

        //nach 5 Metern Bewegung Entfernung zur LSA neu berechnen
        //TODO höhere Distanz geben (7m?)
        if (myNewLocation == null) {
            getNearestLSA(location);
            myNewLocation = location;
        } else if(location.distanceTo(myNewLocation) >= 7){
            myNewLocation = location;
            getNearestLSA(location);
        }
    }

    protected void getNearestLSA(Location myLocation){
        Log.d("getNearestLocation", "getNearestLocation");
        //LSA nearestLSA = null;
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
            // TODO erst bei 300m Entfernung
            if (minDistance > currentDistance[0]) {
                minDistance = currentDistance[0];
                nearestLSA = lsa;
                Log.d("current distance: ", currentDistance[0] + " min distance " + minDistance +"\n" + lsa.getName());
            }
        } //iterate lsas end
        if(nearestLSA != null) {
            getCurrentSzpl();
        }
    }

    private void getCurrentSzpl(){
        SZPL currentSzpl = null;
        Log.d("getCurrentSzpl", "getCurrentSzpl");
        c.setTime(new Date());

        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        SZPL[] szpls = nearestLSA.getSzpls();

        for (SZPL szpl : szpls){
            for (int i : szpl.getDays()){
                if(i == c.get(Calendar.DAY_OF_WEEK) && szpl.getTimeFrom()<=hourOfDay && szpl.getTimeTo()>=hourOfDay){
                    currentSzpl = szpl;
                    Log.d(" ### ", i + "\n");
                    if (onSetListener != null) {
                        onSetListener.onSzplSet(currentSzpl);
                    }
                }
            }
        }
    }
    public void setOnSetListener(OnSetListener listener) {
            onSetListener = listener;
    }

    //wird bei Zustandsänderungen aufgerufen
    public void onStatusChanged(String provider, int status, Bundle extras) {
       switch (status) {
           case LocationProvider.AVAILABLE:
               // setS("GPS ist wieder verfügbar\n");
                break;
           case LocationProvider.OUT_OF_SERVICE:
                setS("GPS ist nicht verfügbar");
                break;
           case  LocationProvider.TEMPORARILY_UNAVAILABLE:
                setS("GPS ist momentan nicht erreichbar");
                break;
        }
    }

    //gewählter Provider aktiviert?
    public void onProviderEnabled(String provider) {
        setS("GPS Signal wird gesucht\n");
        MainActivity.showPosition(getS());
    }
    //gewählter Lieferant  abgeschaltet?
    public void onProviderDisabled(String provider) {
        setS("Bitte aktiviere GPS\n");
        MainActivity.showPosition(getS());
    }

    public void startGpsTracker() {                                         //3sekunden
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        //myNewLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        MainActivity.showPosition(getS());
    }
    public void quitGpsTracker() {
        locationManager.removeUpdates(this);
        MainActivity.showPosition("Ciao");
    }
}

