package com.example.cobi.ais;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by cobi on 22.04.15.
 */
public class FakeLocation extends Location {

    private final Handler locationHandler = new Handler();

    private double Latitude;
    private double Longitude;
    private static final float Accuracy = 11;

    private static final double fakeLat = 53.4592628;
    private static final double fakeLon = 12.2589130;

    private static FakeLocation fakeLocation = null;
    private FakeLocationListener fakeLocationListener;


    public static FakeLocation getInstance() {
        if (fakeLocation == null) {
            fakeLocation = new FakeLocation();
        }
        return fakeLocation;
    }


    protected FakeLocation() {
        super("CUBT");
        this.setLatitude(fakeLat);
        this.setLongitude(fakeLon);
    }


    protected void startFakeTrack() {

        fakeLocation.getInstance();

        locationHandler.removeCallbacks(locationRunnable);
        locationHandler.post(locationRunnable);
    }

    private final Runnable locationRunnable = new Runnable() {
        public void run() {
            double newLat = (fakeLocation.getLatitude() + 0.001);
            double newLon = (fakeLocation.getLongitude() + 0.001);

            fakeLocation.setLatitude(newLat);
            fakeLocation.setLongitude(newLon);

            Log.d("### fakelocation:\n ", fakeLocation.getLatitude() + "\n " + fakeLocation.getLongitude());

            fakeLocationListener.onFakeLocationChange(fakeLocation);


            // alle 3 Sekunden updaten
            locationHandler.postDelayed(this, 3000);
        }
    };

    public double getDistanceBetween(double fromLat, double fromLong,
                                     double toLat, double toLong) {
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }

    public void setFakeLocationListener(FakeLocationListener fakeLocationListener) {
        this.fakeLocationListener = fakeLocationListener;
    }

    public double getLatitude() { return Latitude; }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public float getAccuracy() { return Accuracy; }
}
