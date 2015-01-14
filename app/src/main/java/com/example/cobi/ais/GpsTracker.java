package com.example.cobi.ais;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;


/**
 * Created by cobi on 13.01.15.
 */
public class GpsTracker implements LocationListener {

    public LocationManager locationManager;
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

    public void onLocationChanged(Location location) {
        setS("Alte Position "+getS() + " \n Neue Position: "
            + String.format("%9.6f", location.getLatitude()) + ", "
            + String.format("%9.6f", location.getLongitude()) + "\n"
            + "Geschwindigkeit: " + location.getSpeed() + " m/s \n");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.AVAILABLE) {
             setS("GPS ist wieder verfügbar\n");
        } else if (status == LocationProvider.OUT_OF_SERVICE) {
             setS("GPS ist nicht verfügbar");
        } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
             setS("GPS ist momentan nicht erreichbar");
        }
    }

    public void onProviderEnabled(String provider) {
        setS("GPS Empfänger ist aktiviert\n");
    }

    public void onProviderDisabled(String provider) {
        setS("GPS Empfänger ist nicht aktiviert\n");
    }

    public void showGPS() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        double lat = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
        double lon = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
        String g = Double.toString(lat);
        String f = Double.toString(lon);
        setS("latitide "+g+"\nlongitude "+f);
    }
}

