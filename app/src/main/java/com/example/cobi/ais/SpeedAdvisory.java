package com.example.cobi.ais;
import android.location.Location;
/**
 * Created by cobi on 27.01.15.
 */
public class SpeedAdvisory {
    private double distance;
    private Location lsa;

    //Abstand in Metern zwischen der Position des aufrufenden Location-Objekts und ziel
    private double getDistance(Location location) {
        return location.distanceTo(lsa);
    };

}
