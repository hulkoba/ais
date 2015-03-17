package com.example.cobi.ais;

/**
 * Created by cobi on 22.02.15.
 */
public class Constants {

    // Genauigkeit des GPS Sensors
    static final int LOCATION_ACCURACY = 20;

    // 2 Sekunden
    static  final int UPDATE_INTERVAL = 2000;

    // 5 Meter
    static final int MIN_DISTANCE_CHANGE = 5;

    // 300 Meter
    static final int MIN_LSA_DISTANCE = 300;

    // 30 km/h --> 8,33333 m/s
    static final double MAX_SPEED = (30/3.6);
    // 1 m/s  --> 3,6 km/h
    static final int DIFF_SPEED = (1);
    // 5 km/h --> 1.39 m/s
    static final double MIN_SPEED = (5/3.6);
    // 2 m/s²
    static final double MAX_ACCELERATION = 2;
}
