package com.example.cobi.ais;

import android.location.Location;

/**
 * Created by cobi on 11.02.15.
 */
public class LSA {
    private String name;
    private Location lsaLocation;
    private boolean dependsOnTraffic;

    private SZPL[] szpls;

    public LSA(String name, Location lsaLocation, boolean dependsOnTraffic) {
        this.name = name;
        this.lsaLocation = lsaLocation;
        this.dependsOnTraffic = dependsOnTraffic;
    }

    public LSA(String name, Location lsaLocation, boolean dependsOnTraffic, SZPL[] szpls) {
        this.name = name;
        this.lsaLocation = lsaLocation;
        this.dependsOnTraffic = dependsOnTraffic;
        this.szpls = szpls;
    }

    public String getName() {
        return name;
    }

    public Location getLsaLocation() {
        return lsaLocation;
    }

    public boolean isDependsOnTraffic() {
        return dependsOnTraffic;
    }

    public SZPL[] getPlans() {
        return szpls;
    }

}
