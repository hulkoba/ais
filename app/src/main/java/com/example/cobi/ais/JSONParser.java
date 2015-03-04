package com.example.cobi.ais;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by cobi on 09.02.15.
 */
public class JSONParser {

    private static final List<LSA> lsaList = new ArrayList<>();

    private void parseJSON(String in) {

        try {
            JSONObject jsonObject = new JSONObject(in);
            JSONArray lsas = jsonObject.getJSONArray("lsas");
            LSA lsaObject;

            for(int i = 0;i<lsas.length();i++) {

                JSONObject lsa = lsas.getJSONObject(i);

                String lsaName = lsa.getString("name");

                // Location Objekt erstellen
                Location lsaLocation = new Location("");
                lsaLocation.setLatitude(lsa.getDouble("lat"));
                lsaLocation.setLongitude(lsa.getDouble("lon"));

                Boolean dependsOnTraffic = lsa.getBoolean("dependsOnTraffic");

                if (dependsOnTraffic){

                    // Wenn LSA verkehrsabhängig ist, dann ohne Schaltplan speichern
                    lsaObject = new LSA(lsaName, lsaLocation, true);
                } else {

                    // Wenn nicht, Schaltpläne durchlaufen und konvertieren
                    JSONArray timetable = lsa.getJSONArray("timetable");
                    final SZPL[] szplArray = new SZPL[timetable.length()];

                    for (int j = 0; j < timetable.length(); j++) {

                        JSONObject jsonSzpl = timetable.getJSONObject(j); //jsonPlan
                        JSONArray jsonDays = jsonSzpl.getJSONArray("days");
                        int[]days = new int[jsonDays.length()];

                        // JSONArray to Array + convert DayStrings into Date-Integers
                        for (int k = 0; k < jsonDays.length(); k++) {
                            String day = String.valueOf(jsonDays.get(k))  ;
                            switch(day) {
                                case "Mo":
                                    days[k] = 2;
                                    break;
                                case "Di":
                                    days[k] = 3;
                                    break;
                                case "Mi":
                                    days[k] = 4;
                                    break;
                                case "Do":
                                    days[k] = 5;
                                    break;
                                case "Fr":
                                    days[k] = 6;
                                    break;
                                case "Sa" :
                                    days[k] = 7;
                                    break;
                                default:
                                    days[k] = 1; break;
                            }
                        }

                        // SZPL szpl = new SZPL(days,timeFrom,timeTo,greenFrom,greenTo);
                        SZPL szpl = new SZPL(days,jsonSzpl.getInt("timeFrom"),jsonSzpl.getInt("timeTo"),jsonSzpl.getInt("greenFrom"),jsonSzpl.getInt("greenTo"));
                        // Signalschaltplan in Array einfuegen
                        szplArray[j] = szpl;
                    } // timetable ende

                    // neues Ampelobjekt mit Signalschaltplan
                    lsaObject = new LSA(lsaName, lsaLocation, dependsOnTraffic, szplArray);
                }
                lsaList.add(lsaObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //converts file into String
    public void fetchJSON(java.io.InputStream inputStream){
        java.util.Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String jsonData = scanner.hasNext() ? scanner.next() : "";
        parseJSON(jsonData);
    }

    public static List<LSA> getLsaList() {
        return lsaList;
    }
}
