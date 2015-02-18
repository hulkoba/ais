package com.example.cobi.ais;

/**
 * Created by cobi on 11.02.15.
 */
public class LSA {
    private String name;
    private double latitude;
    private double longitude;
    private boolean dependsOnTraffic;

    private SZPL[] szpls;

    public LSA(String name, double latitude, double longitude, boolean dependsOnTraffic) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dependsOnTraffic = dependsOnTraffic;
    }

    public LSA(String name, double latitude, double longitude, boolean dependsOnTraffic, SZPL[] szpls) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dependsOnTraffic = dependsOnTraffic;
        this.szpls = szpls;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isDependsOnTraffic() {
        return dependsOnTraffic;
    }

    public SZPL[] getSzpls() {
        return szpls;
    }

}
