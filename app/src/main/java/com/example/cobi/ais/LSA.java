package com.example.cobi.ais;

import android.location.Location;

/**
 * Created by cobi on 11.02.15.
 */
public class LSA {
    private String name;
    private Location lsaLocation;
    //private double latitude;
    //private double longitude;
    private boolean dependsOnTraffic;
    private SZPL[] szpls;

    private float distance;

    public LSA(String name, float distance, Location lsaLocation) {
        this.name = name;
        this.distance = distance;
        this.lsaLocation = lsaLocation;
    }

    public LSA(String name, Location lsaLocation, boolean dependsOnTraffic) {
        this.name = name;
        this.lsaLocation = lsaLocation;
        //this.latitude = latitude;
        //this.longitude = longitude;
        this.dependsOnTraffic = dependsOnTraffic;
    }

    public LSA(String name, Location lsaLocation, boolean dependsOnTraffic, SZPL[] szpls) {
        this.name = name;
        this.lsaLocation = lsaLocation;
        //this.latitude = latitude;
        //this.longitude = longitude;
        this.dependsOnTraffic = dependsOnTraffic;
        this.szpls = szpls;
    }

    public String getName() {
        return name;
    }

    public Location getLsaLocation() {
        return lsaLocation;
    }

   /* public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }*/

    public boolean isDependsOnTraffic() {
        return dependsOnTraffic;
    }

    public SZPL[] getSzpls() {
        return szpls;
    }

    public float getDistance() {
        return distance;
    }
}
