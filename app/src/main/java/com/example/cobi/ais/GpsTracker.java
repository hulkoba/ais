package com.example.cobi.ais;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cobi on 13.01.15.
 *
 * GpsTracker ermittelt Position des Gerätes
 * Wenn sich diese um 5 Meter geändert hat, ermittelt die Klasse GpsTracker die nächste Ampel
 *
 * Der GpsTracker wird von der MainActivity aufgerufen
 * Sobald die nächste Ampel ermittelt ist, wird die MainActivity über einen Listener benachrichtigt
 *
 */
class GpsTracker implements LocationListener {

    private LocationManager locationManager;
    private LSAListener lsaListener = null;
    private List<LSA> listNearestLSAs;


    // Provider verfügbar --> GPS aktiviert???
    public boolean gpsIsActive(Activity a) {

        locationManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    // wird aufgerufen, wenn neue Positionsdaten vorhanden sind
    public void onLocationChanged(Location location) {
        // wenn das GPS Signal von hoher Genauigkeit ist, die nächste Ampel ermitteln
        if (location.hasAccuracy()) {
           if(location.getAccuracy() <= Constants.LOCATION_ACCURACY) {
               getNearestLSA(location);
           }
        }
    }

    private void getNearestLSA(Location myLocation){
        Log.d("func ", "get Nearest");
        List<LSA> lsas = JSONParser.getLsaList();
        LSA nearestLSA = null;
        float distance = 0;
        float minDistance = Float.MAX_VALUE;

        // Ampeln in der Umgebung suchen
        if (listNearestLSAs == null ) {

            listNearestLSAs = new ArrayList<>();

            for (LSA lsa : lsas) {
                distance = myLocation.distanceTo(lsa.getLsaLocation());
                if (distance <= Constants.MIN_LSA_DISTANCE) {
                    LSA nlsa = new LSA(distance, lsa.getName(), lsa.getLsaLocation(), lsa.isDependsOnTraffic(), lsa.getSzpls());
                    listNearestLSAs.add(nlsa);
                }
            }
        // Ampeln in der Umgebung gefunden, noch keine Ampel festgelegt
        } else if(nearestLSA == null) {
            Log.d("nearestLSAs", "getNearestLSA" + listNearestLSAs);
            for(LSA lsa : listNearestLSAs){
                distance = myLocation.distanceTo(lsa.getLsaLocation());
                if (/*(distance < lsa.getDistance()) && */(distance <= Constants.MIN_LSA_DISTANCE)  && (minDistance > distance)){
                    minDistance = distance;
                    nearestLSA = lsa;
                }
            }
            // LSA gefunden --> per Listener MainActivity benachrichtigen
            if((lsaListener != null) && (nearestLSA != null) ) {
                Log.d("\n nearest LSA: ", nearestLSA.getName());
                lsaListener.onNewNearestLSA(nearestLSA, myLocation);
            }
        }

        // LSA gesetzt und Entfernung ist höher als gegebene Distanz oder Entfernung ist größer als vorher
        // Liste wird gelöscht, um bei der nächsten Kreuzung mit neuen Ampeln zu füllen
        if(nearestLSA != null) {
            if((myLocation.distanceTo(nearestLSA.getLsaLocation()) > Constants.MIN_LSA_DISTANCE) || (myLocation.distanceTo(nearestLSA.getLsaLocation()) > distance)) {
                listNearestLSAs = null;
            }
        }
    }

    public void setLSAListener(LSAListener listener) {
        lsaListener = listener;
    }

    //wird bei Zustandsänderungen aufgerufen
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    //gewählter Provider aktiviert?
    public void onProviderEnabled(String provider) {

    }
    //gewählter Lieferant  abgeschaltet?
    public void onProviderDisabled(String provider) {

    }

    public void startGpsTracker() {
        // Registrieren für GPS Updates (alle  2 sekunden, nach 5 Metern)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.UPDATE_INTERVAL, Constants.MIN_DISTANCE_CHANGE, this);
    }

    // Location Uptdate Listener abmelden
    public void quitGpsTracker() {
        locationManager.removeUpdates(this);
    }
}

