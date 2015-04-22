package com.example.cobi.ais;

/**
 * Created by cobi on 22.04.15.
 */
public class FakeLocation {


    private double Latitude;
    private double Longitude;
    private final double Accuracy = 11;


    public FakeLocation(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }


    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getAccuracy() {
        return Accuracy;
    }

}
