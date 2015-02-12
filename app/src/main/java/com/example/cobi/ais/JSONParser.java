package com.example.cobi.ais;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

/**
 * Created by cobi on 09.02.15.
 */
public class JSONParser {
    private static final LSA[] lsaArray = new LSA[23];

    private static void parseJSON(String in) {

        try {
            JSONObject jsonObject = new JSONObject(in);
            JSONArray lsas = jsonObject.getJSONArray("lsas");
            //Log.d("############## 1 ", lsas+ " +++");
            LSA lsaObject;

            for(int i = 0;i<lsas.length();i++) {
                JSONObject lsa = lsas.getJSONObject(i);

               // Log.d("\n lsa: " + lsa, "\n i: " +i + "\n");
                String lsaName = lsa.getString("name");
                Location lsaLocation = new Location("");
                lsaLocation.setLatitude(lsa.getDouble("lat"));
                lsaLocation.setLongitude(lsa.getDouble("lon"));
                Boolean dependsOnTraffic = lsa.getBoolean("dependsOnTraffic");

                if (dependsOnTraffic){
                    lsaObject = new LSA(lsaName, lsaLocation, true);
                } else {
                    JSONArray timetable = lsa.getJSONArray("timetable");
                  //  Log.d("\n### timetable ### \n",timetable + " \n"  );
                    for (int j = 0; j < timetable.length()-1; j++) {

                        JSONObject jsonSzpl = timetable.getJSONObject(j); //jsonPlan
                       // Log.d("\nszpl: \n", szpl + "\n" );

                        int duration = jsonSzpl.getInt("duration");
                        int timeFrom = jsonSzpl.getInt("timeFrom");
                        int timeTo = jsonSzpl.getInt("timeTo");
                        int greenFrom = jsonSzpl.getInt("greenFrom");
                        int greenTo = jsonSzpl.getInt("greenTo");

                        SZPL szpl = new SZPL(duration,timeFrom,timeTo,greenFrom,greenTo);
                        Log.d("\nszplObject\n",szpl + " \n"  );

                    } // timetable ende


                    lsaObject = new LSA(lsaName, lsaLocation, dependsOnTraffic);
                }

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
        parseJSON(jsonData);
    }

    public static LSA[] getLsaArray() {
        return lsaArray;
    }
}
