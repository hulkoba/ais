package com.example.cobi.ais;

import android.location.Location;

/**
 * Created by cobi on 11.02.15.
 *
 * Die Klasse repräsentiert eine Ampel
 *
 * @param name repräsentiert den Namen der Ampel
 * @param lsaLocation repräsentiert die Position der Ampel als Location-Objekt
 * @param dependsOnTraffic repräsentiert die Verkehrsabhängigkeit
 * @param szpls repräsentiert die Signalschaltpläne
 * @param distance repräsentiert die Distanz (zum Gerät)
 *
 */
public class LSA {
    private String name;
    private Location lsaLocation;
    private boolean dependsOnTraffic;
    private SZPL[] szpls;

    private float distance;

    public LSA(float distance, String name, Location lsaLocation, boolean dependsOnTraffic, SZPL[] szpls) {
        this.distance = distance;
        this.name = name;
        this.lsaLocation = lsaLocation;
        this.dependsOnTraffic = dependsOnTraffic;
        this.szpls = szpls;
    }

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

    public SZPL[] getSzpls() {
        return szpls;
    }

    public float getDistance() {
        return distance;
    }
}
