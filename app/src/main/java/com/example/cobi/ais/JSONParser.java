package com.example.cobi.ais;

import android.util.Log;

import org.json.JSONArray;
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


    private static void parseJSONlsasr(String in) {
        try {

            JSONObject mainObj = new JSONObject(in);
            // Creating JSONArray from JSONObject
            JSONArray positions = mainObj.getJSONArray("Position");
            //Log.d("output ĺsa:  " , String.format("#########%s", positions));

            //for(Iterator<JSONObject> iterator = mainObj.iterator(); iterator.hasNext(););

            JSONArray days = mainObj.getJSONArray("days"); // many days in object
            Log.d("output ĺsa:  " ,  days.toString());
            //days has some Objects
            for (int i = 0; i<=days.length();i++){
                days.get(0);
                Log.d("output day:  " ,  days.get(0).toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static void parseJSONlsas(String in) {
        try {

            JSONObject mainObj = new JSONObject(in);
            // Creating JSONArray from JSONObject
            JSONArray positions = mainObj.getJSONArray("Position");
            //Log.d("output ĺsa:  " , String.format("#########%s", positions));



            JSONArray days = mainObj.getJSONArray("days");
            Log.d("output ĺsa:  " ,  days.toString());
            //days has some Objects
            for (int i = 0; i<=days.length();i++){
                days.get(0);
                Log.d("output day:  " ,  days.get(0).toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //String
    }

    public static void fetchJSON(java.io.InputStream inputStream){
        //converts file into String
        java.util.Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String jsonData = scanner.hasNext() ? scanner.next() : "";

        parseJSONlsasr(jsonData);
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
