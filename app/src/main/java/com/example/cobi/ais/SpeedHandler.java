package com.example.cobi.ais;


import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cobi on 18.02.15.
 */
public class SpeedHandler{

    private final Calendar c = Calendar.getInstance(Locale.GERMANY);
    private final Handler handler = new Handler();
    private final MainActivity mainActivity;

    private SZPL currentSzpl;

    private Location lsaLocation;
    private Location myLocation;

    private int countdown;

    private boolean x, ok, up, upper, down, downer = false;

    public SpeedHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // Schaltplan der nächsten LSA holen, wenn keiner Vorhanden --> verkehrsabhängig --> keine Vorausage möglich
    protected void getCurrentSzpl(LSA nearestLSA, Location loc){
        Log.d("getCurrentSzpl", "getCurrentSzpl");

        lsaLocation = nearestLSA.getLsaLocation();
        myLocation = loc;

        c.setTime(new Date());
        // Aktuelle Stunde
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        // verkehrsabhängige LSA hat keinen Schaltplan
        if(nearestLSA.isDependsOnTraffic()){
            if(mainActivity.mepView.getVisibility() == View.INVISIBLE) {
                mainActivity.mepView.setVisibility(View.VISIBLE);
            }

            if(mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.okView.getVisibility() == View.VISIBLE) {
                mainActivity.okView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.xView.getVisibility() == View.VISIBLE) {
                mainActivity.xView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.downView.getVisibility() == View.VISIBLE) {
                mainActivity.downView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.downerView.getVisibility() == View.VISIBLE) {
                mainActivity.downerView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.upView.getVisibility() == View.VISIBLE) {
                mainActivity.upView.setVisibility(View.INVISIBLE);
            }
            if(mainActivity.upperView.getVisibility() == View.VISIBLE) {
                mainActivity.upperView.setVisibility(View.INVISIBLE);
            }
        } else {

            if (mainActivity.mepView.getVisibility() == View.VISIBLE) {
                mainActivity.mepView.setVisibility(View.INVISIBLE);
            }


            SZPL[] szpls = nearestLSA.getSzpls();

            for (SZPL szpl : szpls) {
                for (int i : szpl.getDays()) {
                    // stimmen Wochentag und Zeitabschnitt überein?
                    if (i == c.get(Calendar.DAY_OF_WEEK) && szpl.getTimeFrom() <= hourOfDay && szpl.getTimeTo() >= hourOfDay) {
                        currentSzpl = szpl;
                    }
                }
            }
        }

        if(currentSzpl != null) {
            getOptSpeed(currentSzpl);
        }
    }

   /*
   * Ist die Ampel grün oder rot?
   */
    private String getPhase(int currentSecond, int greenFrom, int greenTo){
       if(currentSecond >= greenFrom && currentSecond <= greenTo){
            // Ampel ist grün
            return "green";
        } else {
            // Ampel ist rot
            return "red";
        }
    }

    /*
     * Geschwindigkeit berechnen
     * v = s / (t2 - t1)
     * a = v / (t2 - t1)²
     * t1 = aktuelle Sekunde
     * t2 = Ampel schaltet auf rot
     * s = Abstand zwischen Fahrrad und Ampel
     * a = notwendige Beschleunigung
     * speed = eigene Geschwindigkeit
     *
     * Timer updatet jede Sekunde die GUI
     */
    void getOptSpeed(SZPL szpl){
        final int greenFrom = szpl.getGreenFrom();
        final int greenTo = szpl.getGreenTo();
        final float speed = myLocation.getSpeed();

        c.setTime(new Date());
        // aktuelle Sekunde
        int t1 = c.get(Calendar.SECOND);
        // Ampel schaltet auf rot
        int t2 =greenTo + 1;

        if(t2 < t1 || myLocation == null || lsaLocation == null) {
            return;
        }
        // Zeitabschnitt = deltaT
        float deltaT = t2 - t1;
        // s = Abstand zur Ampel
        float s = myLocation.distanceTo(lsaLocation);

        // v=s/(t2-t1)
        final float v = s / deltaT;
        // a=v/(t2-t1)²
        final double a = v / Math.pow(deltaT, 2.0);
        Log.d("v: " ,"\n"+ v +"\n in km/h: " + (v*3.6)+"\na= " +a +"\nspeed "+speed);

        // mit den Ergebnissen die GUI updaten
        UpdateGUI(speed, v, a);
    }

    /*
     * GUI bestimmen
     */
    private void UpdateGUI(float speed, float v, double a) {

        // empfohlene Geschwindigkeit = aktuelles Tempo
        if (Math.round(speed) == Math.round(v)) {
            Log.d("\nok", "ok");
            ok=true;
            x = false; up = false; upper=false; down=false; downer = false;

        } else if (speed < v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {

            // langsamer als empfohlen, also schneller fahren
            Log.d("\nup", "schnell");

            up = true;
            ok = false; x = false; upper = false; down = false; downer = false;

        } else if (speed < v && (speed + Constants.DIFF_SPEED) < v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {

            // viel langsamer als empfohlen, also viel schneller fahren
            Log.d("\nup", "schneller");

            up = true; upper = true;
            ok = false; x = false; down=false; downer = false;


        } else if (speed > v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {

            //schneller als empfohlen --> langsamer fahren
            Log.d("\ndown", "langsam");

            down = true;
            ok = false; x = false; upper = false; up = false; downer = false;


        } else if ( speed > v && (speed - Constants.DIFF_SPEED) > v && v < Constants.MAX_SPEED && v > Constants.MIN_SPEED && a < Constants.MAX_ACCELERATION) {

            // viel schneller als empfohlen >> viel langsamer fahren
            Log.d("\ndown", "langsamer");

            down = true; downer = true;
            ok = false; x = false; upper = false; up = false;
        } else {


            // Geschwindigkeit zu hoch  oder  Geschwindigkeit zu niedrig oder Beschleunigung zu hoch -->> anhalten
            Log.d("\nx", "x");
            x=true;
            ok = false; up = false; upper = false; down = false; downer = false;
        }

        handler.removeCallbacks(viewRunnable);
        handler.post(viewRunnable);
    }

    private void getCountdown(int greenFrom, int greenTo){
        c.setTime(new Date());
        final int currentSecond = c.get(Calendar.SECOND);

        if(getPhase(currentSecond, greenFrom, greenTo).equals("green")){
            // Ampel ist grün
            countdown = 0;
        } else {

            // Ampel ist rot
            if(currentSecond < greenFrom) {
                countdown = greenFrom - currentSecond;
            } else {
                // 1 Minute von der aktuellen Sekunde abziehen, + Dauer in der Ampel auf Grün schaltet
                countdown = (60-currentSecond) + greenFrom;
            }
        }
    }


    // jede Sekunde die Anzeigeelemente aktualisieren + Countdown berechnen
    private final Runnable viewRunnable = new Runnable() {
        public void run() {

            // wenn Signalschaltplan verfügbar, dann Countdown berechnen
            if(currentSzpl != null) {
                getCountdown(currentSzpl.getGreenFrom(), currentSzpl.getGreenTo());
            }

            // Anhalten in jedem Fall erforderlich
            if (x){

                mainActivity.xView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);

            // Geschwindigkeit ist okay?
            } else if (ok){
                mainActivity.okView.setVisibility(View.VISIBLE);

                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);

            // Aufforderung schneller zu fahren?
            } else if (up) {

                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.upView.setVisibility(View.VISIBLE);

                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.downView.setVisibility(View.INVISIBLE);
                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);

                // bei Aufforderung noch schneller, zweiten Pfeil auch einblenden
                if(up && upper){
                    mainActivity.upperView.setVisibility(View.VISIBLE);
                }

            // Aufforderung langsamer zu fahren?
            } else if (down) {

                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.downView.setVisibility(View.VISIBLE);

                mainActivity.downerView.setVisibility(View.INVISIBLE);
                mainActivity.upView.setVisibility(View.INVISIBLE);
                mainActivity.upperView.setVisibility(View.INVISIBLE);
                mainActivity.okView.setVisibility(View.INVISIBLE);
                mainActivity.xView.setVisibility(View.INVISIBLE);
                mainActivity.mepView.setVisibility(View.INVISIBLE);

                // bei Aufforderung noch langsamer, zweiten Pfeil auch einblenden
                if (down && downer) {
                    mainActivity.downerView.setVisibility(View.VISIBLE);
                }
            }

            // ist die TextView sichtbar, dann Countdown anzeigen
            if (mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
                mainActivity.countdownTextView.setText(String.valueOf(countdown));
            }

            handler.postDelayed(this, 1000);
        }
    };
}