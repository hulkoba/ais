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
    private SZPL currentSzpl = null;
    private Location myNewLocation;
    Date today = new Date();
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
            + "entspricht" + (location.getSpeed()*3.6) + "km/h \n"
            + "Peilung: " + location.getBearing());

        //location.setLatitude(location.getLatitude());
        //location.setLongitude(location.getLongitude());
        MainActivity.showPosition(getS());
        //nach 5 Metern Bewegung Entfernung zur LSA neu berechnen
      //  if(location.distanceTo(myNewLocation) >= 5){
         //   myNewLocation = location;
            getNearestLSA(location);
       // }
    }

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
        detectCurrentSzpl(nearestLSA);
    }

    private void detectCurrentSzpl(LSA nearest){
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
    }

    public SZPL getCurrentSzpl() {
        return currentSzpl;
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
        myNewLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        MainActivity.showPosition(getS());
    }
    public void quitGpsTracker() {
        locationManager.removeUpdates(this);
        MainActivity.showPosition("Ciao");
    }
}

