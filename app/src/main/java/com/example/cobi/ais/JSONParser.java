package com.example.cobi.ais;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

/**
 * Created by cobi on 09.02.15.
 */
public class JSONParser {
    private static final LSA[] lsaArray = new LSA[23];

    private void parseJSON(String in) {

        try {
            JSONObject jsonObject = new JSONObject(in);
            JSONArray lsas = jsonObject.getJSONArray("lsas");
            LSA lsaObject;

            for(int i = 0;i<lsas.length();i++) {
                final SZPL[] szplArray = new SZPL[7];
                JSONObject lsa = lsas.getJSONObject(i);

                String lsaName = lsa.getString("name");
                Location lsaLocation = new Location("");
                lsaLocation.setLatitude(lsa.getDouble("lat"));
                lsaLocation.setLongitude(lsa.getDouble("lon"));
                Boolean dependsOnTraffic = lsa.getBoolean("dependsOnTraffic");

                if (dependsOnTraffic){
                    lsaObject = new LSA(lsaName, lsaLocation, true);
                } else {
                    JSONArray timetable = lsa.getJSONArray("timetable");

                    for (int j = 0; j < timetable.length(); j++) {

                        JSONObject jsonSzpl = timetable.getJSONObject(j); //jsonPlan

                        int duration = jsonSzpl.getInt("duration");
                        int timeFrom = jsonSzpl.getInt("timeFrom");
                        int timeTo = jsonSzpl.getInt("timeTo");
                        int greenFrom = jsonSzpl.getInt("greenFrom");
                        int greenTo = jsonSzpl.getInt("greenTo");

                        JSONArray jsonDays = jsonSzpl.getJSONArray("days");
                        String[]days = new String[6]; // 7 days a week
                        //JSONArray to Array
                        for (int k = 0; k < jsonDays.length(); k++) {
                            days[k] = jsonDays.get(k).toString();
                        }

                        SZPL szpl = new SZPL(days, duration,timeFrom,timeTo,greenFrom,greenTo);
                       // SZPL szpl = new SZPL(days, jsonSzpl.getInt("duration"),jsonSzpl.getInt("timeFrom"),jsonSzpl.getInt("timeTo"),jsonSzpl.getInt("greenFrom"),jsonSzpl.getInt("greenTo"));

                        szplArray[j] = szpl;

                    } // timetable ende

                    lsaObject = new LSA(lsaName, lsaLocation, dependsOnTraffic, szplArray);
                }
                lsaArray[i] = lsaObject;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void fetchJSON(java.io.InputStream inputStream){
        //converts file into String
        java.util.Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String jsonData = scanner.hasNext() ? scanner.next() : "";
        parseJSON(jsonData);
    }

    public static LSA[] getLsaArray() {
        return lsaArray;
    }

}
