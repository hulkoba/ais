package com.example.cobi.ais;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cobi on 13.01.15.
 */
class GpsTracker implements LocationListener {

    private LocationManager locationManager;
    private Location myNewLocation;
    public  OnSetListener onSetListener = null;
    private List<LSA> nearestLSAs;


    // Provider verfügbar --> GPS aktiviert???
    public boolean gpsIsActive(Activity a) {
        locationManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    // wird aufgerufen, wenn neue Positionsdaten vorhanden sind
    public void onLocationChanged(Location location) {

        //nach höherer Entfernung zur LSA neu berechnen
        //if(myNewLocation == null || location.distanceTo(myNewLocation) >= Constants.MY_DISTANCE){
          //  myNewLocation = location;
            Log.d("\n ", "\n"+ String.valueOf(location.getLatitude())+"\n"+ String.valueOf(location.getLongitude()));
            getNearestLSA(location);
       // }
    }

    private void getNearestLSA(Location myLocation){
        Log.d("count ", "get Nearest");
        List<LSA> lsas = JSONParser.getLsaList();
        LSA nearestLSA = null;
        float distance = 0;
        float minDistance = Float.MAX_VALUE;

        // Ampeln in der Umgebung suchen
        if (nearestLSAs == null ) {
            Log.d("list ", "NearestLSAs == null");
            nearestLSAs = new ArrayList<LSA>();

            for (LSA lsa : lsas) {
                distance = myLocation.distanceTo(lsa.getLsaLocation());
                if (distance <= Constants.MIN_LSA_DISTANCE) {
                    LSA nlsa = new LSA(distance, lsa.getName(), lsa.getLsaLocation(), lsa.isDependsOnTraffic(), lsa.getSzpls());
                    nearestLSAs.add(nlsa);
                }
            }
        // Ampeln in der Umgebung gefunden, noch keine Ampel festgelegt
        } else if(nearestLSAs != null && nearestLSA == null) {
            Log.d("list+lsa ", "Nearest != null && nearestLSA == null");
            for(LSA lsa : nearestLSAs){
                distance = myLocation.distanceTo(lsa.getLsaLocation());
                if (/*distance < lsa.getDistance() &&*/ distance <= Constants.MIN_LSA_DISTANCE  && minDistance > distance){ // && minDistance > distance
                    minDistance = distance;
                    nearestLSA = lsa;
                    Log.d("\n nearest lsa: ", nearestLSA.getName());
                    // LSA gefunden --> per Listener MainActivity benachrichtigen
                    if(onSetListener != null){
                        Log.d("\n nearest LSA: ", nearestLSA.getName());
                        onSetListener.onLSASet(nearestLSA, myLocation);
                    }
                }
            }
        }

        // LSA gesetzt und Entfernung ist höher als gegebene Distanz oder Entfernung ist größer als vorher
        if(nearestLSA != null) {
            if(myLocation.distanceTo(nearestLSA.getLsaLocation()) > Constants.MIN_LSA_DISTANCE || myLocation.distanceTo(nearestLSA.getLsaLocation()) > distance) {
                nearestLSAs = null;
            }
        }
    }

    public void setOnSetListener(OnSetListener listener) {
            onSetListener = listener;
    }

    //wird bei Zustandsänderungen aufgerufen
    public void onStatusChanged(String provider, int status, Bundle extras) {
       switch (status) {
           case LocationProvider.AVAILABLE:
                //setS("GPS ist wieder verfügbar\n");
                break;
           case LocationProvider.OUT_OF_SERVICE:
                //setS("GPS ist nicht verfügbar");
                break;
           case  LocationProvider.TEMPORARILY_UNAVAILABLE:
                //setS("GPS ist momentan nicht erreichbar");
                break;
        }
    }

    //gewählter Provider aktiviert?
    public void onProviderEnabled(String provider) {
        Log.d("onProviderEnabled ","GPS Signal wird gesucht\n");
    }
    //gewählter Lieferant  abgeschaltet?
    public void onProviderDisabled(String provider) {
        Log.d("onProviderDisabled ","Bitte aktiviere GPS\n");
    }

    public void startGpsTracker() {
        // Registrieren für GPS Updates (alle 3 sekunden, nach 5 Metern)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
        // VergleichsPosition ist die letzte bekannte Position
      //  myNewLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    // Location Uptdate Listener abmelden
    public void quitGpsTracker() {
        locationManager.removeUpdates(this);
    }
}

