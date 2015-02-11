package com.example.cobi.ais;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by cobi on 09.02.15.
 */
public class JSONParser {
    private static LSA[] lsaArray = new LSA[23];

    private static void parseJSON(String in) {

        try {
            JSONObject jsonObject = new JSONObject(in);
            JSONArray lsas = jsonObject.getJSONArray("lsas");
            //Log.d("############## 1 ", lsas+ " +++");

            for(int i = 0;i<lsas.length();i++) {
                JSONObject lsa = lsas.getJSONObject(i);


                Log.d("lsa: " + lsa, "\n i: " +i + "\n");
                String lsaName = lsa.getString("name");
                Location lsaLocation = new Location("");
                lsaLocation.setLatitude(lsa.getDouble("lat"));
                lsaLocation.setLongitude(lsa.getDouble("lon"));
                Boolean dependsOnTraffic = lsa.getBoolean("dependsOnTraffic");

               // JSONArray timetable = lsa.getJSONArray("timetable");

                LSA lsaObject = new LSA(lsaName, lsaLocation, dependsOnTraffic);
                lsaArray[i] = lsaObject;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("array: ", lsaArray.toString() + "\n");
    }


    public static void fetchJSON(java.io.InputStream inputStream){
        //converts file into String
        java.util.Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String jsonData = scanner.hasNext() ? scanner.next() : "";
        //Log.d("output data:  " ,  jsonData);
        parseJSON(jsonData);
    }

    public static LSA[] getLsaArray() {
        return lsaArray;
    }
}
