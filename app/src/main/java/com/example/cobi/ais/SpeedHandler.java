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

    private final Calendar calendar = Calendar.getInstance(Locale.GERMANY);
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

        calendar.setTime(new Date());
        // Aktuelle Stunde
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        // verkehrsabhängige LSA hat keinen Schaltplan
        if(nearestLSA.isDependsOnTraffic()){
            showMepView();
        } else {

            SZPL[] szpls = nearestLSA.getSzpls();

            for (SZPL szpl : szpls) {
                for (int i : szpl.getDays()) {
                    // stimmen Wochentag und Zeitabschnitt überein?
                    if (i == calendar.get(Calendar.DAY_OF_WEEK) && szpl.getTimeFrom() <= hourOfDay && szpl.getTimeTo() >= hourOfDay) {
                        currentSzpl = szpl;
                    }
                }
            }
        }

        if(currentSzpl != null ) {
            // greentFrom + greenTo ist auf 999 gesetzt, wenn die Ampel aus ist
            if (currentSzpl.getGreenFrom() == 999) showMepView();
            else {
                getOptSpeed(currentSzpl);
            }
        }
        if (mainActivity.trafficImageView.getVisibility() == View.VISIBLE) {
            mainActivity.trafficImageView.setVisibility(View.INVISIBLE);
        }

    }

    private void showMepView(){
        if(mainActivity.trafficImageView.getVisibility() == View.INVISIBLE) {
            mainActivity.trafficImageView.setVisibility(View.VISIBLE);
        }

        if(mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
            mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
        }
        if(mainActivity.okImageView.getVisibility() == View.VISIBLE) {
            mainActivity.okImageView.setVisibility(View.INVISIBLE);
        }
        if(mainActivity.stopImageView.getVisibility() == View.VISIBLE) {
            mainActivity.stopImageView.setVisibility(View.INVISIBLE);
        }
        if(mainActivity.slowImageView.getVisibility() == View.VISIBLE) {
            mainActivity.slowImageView.setVisibility(View.INVISIBLE);
        }
        if(mainActivity.slowerImageView.getVisibility() == View.VISIBLE) {
            mainActivity.slowerImageView.setVisibility(View.INVISIBLE);
        }
        if(mainActivity.fastImageView.getVisibility() == View.VISIBLE) {
            mainActivity.fastImageView.setVisibility(View.INVISIBLE);
        }
        if(mainActivity.fasterImageView.getVisibility() == View.VISIBLE) {
            mainActivity.fasterImageView.setVisibility(View.INVISIBLE);
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
        final float mySpeed = myLocation.getSpeed();

        calendar.setTime(new Date());
        // aktuelle Sekunde
        int t1 = calendar.get(Calendar.SECOND);
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
        final float recomenndedSpeed = s / deltaT;
        // recomenndedAccelleration=v/(t2-t1)²
        final double recomenndedAccelleration = recomenndedSpeed / Math.pow(deltaT, 2.0);
        Log.d("v: " ,"\n"+ recomenndedSpeed +"\n in km/h: " + (recomenndedSpeed*3.6)+"\nrecomenndedAccelleration= " +recomenndedAccelleration +"\nmySpeed "+mySpeed);

        // mit den Ergebnissen die GUI updaten
        updateGUI(mySpeed, recomenndedSpeed, recomenndedAccelleration);
    }

    /*
     * GUI bestimmen
     */
    private void updateGUI(float mySpeed, float recomenndedSpeed, double recomenndedAccelleration) {

        // empfohlene Geschwindigkeit = aktuelles Tempo
        if (Math.round(mySpeed) == Math.round(recomenndedSpeed)) {
            Log.d("\nok", "ok");
            ok=true;
            x = false; up = false; upper=false; down=false; downer = false;

        } else if (mySpeed < recomenndedSpeed && recomenndedSpeed < Constants.MAX_SPEED && recomenndedSpeed > Constants.MIN_SPEED && recomenndedAccelleration < Constants.MAX_ACCELERATION) {

            // langsamer als empfohlen, also schneller fahren
            Log.d("\nup", "schnell");

            up = true;
            ok = false; x = false; upper = false; down = false; downer = false;

        } else if (mySpeed < recomenndedSpeed && (mySpeed + Constants.DIFF_SPEED) < recomenndedSpeed && recomenndedSpeed < Constants.MAX_SPEED && recomenndedSpeed > Constants.MIN_SPEED && recomenndedAccelleration < Constants.MAX_ACCELERATION) {

            // viel langsamer als empfohlen, also viel schneller fahren
            Log.d("\nup", "schneller");

            up = true; upper = true;
            ok = false; x = false; down=false; downer = false;


        } else if (mySpeed > recomenndedSpeed && recomenndedSpeed < Constants.MAX_SPEED && recomenndedSpeed > Constants.MIN_SPEED && recomenndedAccelleration < Constants.MAX_ACCELERATION) {

            //schneller als empfohlen --> langsamer fahren
            Log.d("\ndown", "langsam");

            down = true;
            ok = false; x = false; upper = false; up = false; downer = false;


        } else if ( mySpeed > recomenndedSpeed && (mySpeed - Constants.DIFF_SPEED) > recomenndedSpeed && recomenndedSpeed < Constants.MAX_SPEED && recomenndedSpeed > Constants.MIN_SPEED && recomenndedAccelleration < Constants.MAX_ACCELERATION) {

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

    private void calculateCountdown(int greenFrom, int greenTo){
        calendar.setTime(new Date());
        final int currentSecond = calendar.get(Calendar.SECOND);

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
                calculateCountdown(currentSzpl.getGreenFrom(), currentSzpl.getGreenTo());
            }

            // Anhalten in jedem Fall erforderlich
            if (x){

                mainActivity.stopImageView.setVisibility(View.VISIBLE);

                mainActivity.fastImageView.setVisibility(View.INVISIBLE);
                mainActivity.fasterImageView.setVisibility(View.INVISIBLE);
                mainActivity.slowImageView.setVisibility(View.INVISIBLE);
                mainActivity.slowerImageView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.okImageView.setVisibility(View.INVISIBLE);
                mainActivity.trafficImageView.setVisibility(View.INVISIBLE);

            // Geschwindigkeit ist okay?
            } else if (ok){
                mainActivity.okImageView.setVisibility(View.VISIBLE);

                mainActivity.fastImageView.setVisibility(View.INVISIBLE);
                mainActivity.fasterImageView.setVisibility(View.INVISIBLE);
                mainActivity.slowImageView.setVisibility(View.INVISIBLE);
                mainActivity.slowerImageView.setVisibility(View.INVISIBLE);
                mainActivity.stopImageView.setVisibility(View.INVISIBLE);
                mainActivity.countdownTextView.setVisibility(View.INVISIBLE);
                mainActivity.trafficImageView.setVisibility(View.INVISIBLE);

            // Aufforderung schneller zu fahren?
            } else if (up) {

                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.fastImageView.setVisibility(View.VISIBLE);

                mainActivity.fasterImageView.setVisibility(View.INVISIBLE);
                mainActivity.slowImageView.setVisibility(View.INVISIBLE);
                mainActivity.slowerImageView.setVisibility(View.INVISIBLE);
                mainActivity.okImageView.setVisibility(View.INVISIBLE);
                mainActivity.stopImageView.setVisibility(View.INVISIBLE);
                mainActivity.trafficImageView.setVisibility(View.INVISIBLE);

                // bei Aufforderung noch schneller, zweiten Pfeil auch einblenden
                if(up && upper){
                    mainActivity.fasterImageView.setVisibility(View.VISIBLE);
                }

            // Aufforderung langsamer zu fahren?
            } else if (down) {

                mainActivity.countdownTextView.setVisibility(View.VISIBLE);
                mainActivity.slowImageView.setVisibility(View.VISIBLE);

                mainActivity.slowerImageView.setVisibility(View.INVISIBLE);
                mainActivity.fastImageView.setVisibility(View.INVISIBLE);
                mainActivity.fasterImageView.setVisibility(View.INVISIBLE);
                mainActivity.okImageView.setVisibility(View.INVISIBLE);
                mainActivity.stopImageView.setVisibility(View.INVISIBLE);
                mainActivity.trafficImageView.setVisibility(View.INVISIBLE);

                // bei Aufforderung noch langsamer, zweiten Pfeil auch einblenden
                if (down && downer) {
                    mainActivity.slowerImageView.setVisibility(View.VISIBLE);
                }
            }

            // ist die TextView sichtbar, dann Countdown anzeigen
            if (mainActivity.countdownTextView.getVisibility() == View.VISIBLE) {
                mainActivity.countdownTextView.setText(String.valueOf(countdown));
            }

            // sekündlich updaten
            handler.postDelayed(this, 1000);
        }
    };
}