package com.example.cobi.ais;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by cobi on 13.01.15.
 */
class GpsHandler implements LocationListener {

    private LocationManager locationManager;
    private double distance;
    private Location lsaLocation;
    private Location newLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


    private String s;
    public String getS() {
        return s;
    }
    public void setS(String s) {
        this.s = s;
    }
    private String l;
    public String getL() {
        return l;
    }
    public void setL(String l) {
        this.l += l;
    }

    // Provider verfügbar --> GPS aktiviert???
    public boolean gpsIsActive(Activity a) {
        //newLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
        if(location.distanceTo(newLocation) >= 5){
            newLocation = location;
            getNearestLSA(location);
        }

    }

    private void getNearestLSA(Location myLocation){

        LSA[]lsas = JSONParser.getLsaArray();
        for (int i = 0; i < lsas.length; i++) {
            lsaLocation = lsas[i].getLsaLocation();
            distance = myLocation.distanceTo(lsaLocation);

            if(distance <= 3321.98) {
                setL(" distance zu " + lsas[i].getName() + "\n beträgt " + String.format("%9.2f", distance) + " Meter.\n");
                Log.d("distance: ", getL());
                MainActivity.showDistance(getL());
            }
        }
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

    public void startGpsTracker() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        MainActivity.showPosition(getS());
    }
    public void quitGpsTracker() {
        locationManager.removeUpdates(this);
        MainActivity.showPosition("Ciao");
    }
}

