package com.example.cobi.ais;

import java.sql.Date;

/**
 * Created by cobi on 27.01.15.
 * Klasse zum Aufnehmen eines SQL Datensatzes
 */

public class LSA {
    public long id;
    public Date red;
    public Date green;
    public double lat;
    public double lon;
    public int traffic;

    // Ein Datansatz
    public LSA(Date red, Date green, double lat, double lon, int traffic){
        id = -1; //wird beim Einfügen in DB erzeugt
        this.red = red;
        this.green = green;
        this.lat = lat;
        this.lon = lon;
        this.traffic = traffic;
    };
}
