package com.example.cobi.ais;

import android.os.Handler;

/**
 * Created by cobi on 22.04.15.
 */
public class MyFakeLocation {

    private final Handler locationHandler = new Handler();

    private double fakeLat = 53.4592628;
    private double newLat;
    private double newLon;
    private double fakeLon = 12.2589130;

    FakeLocation fakeLocation ;

    private  void fakeLocation(double lat, double lon) {
        fakeLocation = new FakeLocation(fakeLat, fakeLon);


        locationHandler.removeCallbacks(locationRunnable);
        locationHandler.post(locationRunnable);
    }

    private final Runnable locationRunnable = new Runnable() {
        public void run() {
            newLat = fakeLat + 0.001;
            newLon = fakeLon + 0.001;

            fakeLocation.setLatitude(newLat);
            fakeLocation.setLongitude(newLon);



            // sekündlich updaten
            locationHandler.postDelayed(this, 1000);
        }
    };
}
