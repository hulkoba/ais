package com.example.cobi.ais;

import android.location.Location;
import java.lang.reflect.Array;

/**
 * Created by cobi on 11.02.15.
 */
public class LSA {
    private String name;
    private Location lsaLocation;
    private boolean dependsOnTraffic;

    private Array days;
    private int duration;
    private int timeFrom;
    private int timeTo;
    private int greenFrom;
    private int greenTo;

    public LSA(int duration, int timeFrom, int timeTo, int greenFrom, int greenTo) {

        this.duration = duration;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.greenFrom = greenFrom;
        this.greenTo = greenTo;
    }

    public LSA(String name, Location lsaLocation, boolean dependsOnTraffic) {
        this.name = name;
        this.lsaLocation = lsaLocation;
        this.dependsOnTraffic = dependsOnTraffic;
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

    public Array getDays() {
        return days;
    }

    public int getDuration() {
        return duration;
    }

    public int getTimeFrom() {
        return timeFrom;
    }

    public int getTimeTo() {
        return timeTo;
    }

    public int getGreenFrom() {
        return greenFrom;
    }

    public int getGreenTo() {
        return greenTo;
    }
}
