package com.example.cobi.ais;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

/**
 * Created by cobi on 09.02.15.
 */
public class JSONParser {
    private double lat;
    private double lon;
    private String lsaName;
    private boolean dependsOnTraffic;

    private static void parseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            JSONObject kreuzung = reader.getJSONObject("Bornholmer/Björnson");
            Object pos = kreuzung.get("Position");

            Log.d("output ĺsa:  " , "#########" + pos.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fetchJSON(java.io.InputStream inputStream){
        //converts file into String
        java.util.Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String data = scanner.hasNext() ? scanner.next() : "";

        parseJSON(data);
       // Log.d("output ĺsa:  " , "#########" + data);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getLsaName() {
        return lsaName;
    }

    public boolean isDependsOnTraffic() {
        return dependsOnTraffic;
    }
}
