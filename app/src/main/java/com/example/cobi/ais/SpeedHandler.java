package com.example.cobi.ais;


import android.location.Location;
import android.os.Handler;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cobi on 18.02.15.
 */
public class SpeedHandler{

    private AdviceListener adviceListener;

    private final Calendar calendar = Calendar.getInstance(Locale.GERMANY);
    private final Handler handler = new Handler();

    private SZPL currentSzpl;

    private Location lsaLocation;
    private FakeLocation myLocation;

    private int countdown;

    private boolean stop, ok, fast, faster, slow, slower = false;



    // Schaltplan der nächsten LSA holen, wenn keiner Vorhanden --> verkehrsabhängig --> keine Vorausage möglich
    protected void fetchCurrentSzpl(LSA nearestLSA, Location loc){

        lsaLocation = nearestLSA.getLsaLocation();
       // myLocation = loc;

        calendar.setTime(new Date());
        // Aktuelle Stunde
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        // verkehrsabhängige LSA hat keinen Schaltplan
        if(nearestLSA.isDependsOnTraffic()){
            adviceListener.lsaIsTrafficDependent();
           // showTrafficImageView();
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
            // greentFrom ist auf 999 gesetzt, wenn die Ampel aus ist, gelbes ausrufungszeichen anzeigen
            if (currentSzpl.getGreenFrom() == 999) adviceListener.lsaIsTrafficDependent();
            else {
                calculateOptSpeed(currentSzpl);
            }
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
     * @param recommendedSpeed =; v = s / (t2 - t1)
     * @param recommendedAccelleration =: a = v / (t2 - t1)²
     * @param t1 = aktuelle Sekunde
     * @param t2 = Ampel schaltet auf rot
     * @param s = Abstand zwischen Fahrrad und Ampel
     * @param recommendesAccelleration = notwendige Beschleunigung
     * @param speed = eigene Geschwindigkeit
     *
     * Handler updatet jede Sekunde die GUI
     */
    void calculateOptSpeed(SZPL szpl){

        final int greenTo = szpl.getGreenTo();
      //  final float mySpeed = myLocation.getSpeed();
        final double mySpeed = 5.1;

        calendar.setTime(new Date());
        // aktuelle Sekunde
        int t1 = calendar.get(Calendar.SECOND);
        // Ampel schaltet auf rot
        int t2 = greenTo + 1;

        if(t2 < t1 || myLocation == null || lsaLocation == null) {
            return;
        }
        // Zeitabschnitt = deltaT
        float deltaT = t2 - t1;

        // s = Abstand zur Ampel
        double s = myLocation.getDistanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), lsaLocation.getLatitude(), lsaLocation.getLongitude());
       // float s = myLocation.distanceTo(lsaLocation);

        // v = s / (t2-t1)
        final double recommendedSpeed = s / deltaT;

        // recommendedAccelleration = v/(t2-t1)²
        final double recommendedAccelleration = recommendedSpeed / Math.pow(deltaT, 2.0);

        // mit den Ergebnissen die GUI updaten
        updateGUI(mySpeed, recommendedSpeed, recommendedAccelleration);
    }

    /*
     * GUI bestimmen
     */
    private void updateGUI(double mySpeed, double recomenndedSpeed, double recomenndedAccelleration) {

        // empfohlene Geschwindigkeit = aktuelles Tempo
        if (Math.round(mySpeed) == Math.round(recomenndedSpeed)) {

            ok = true;
            stop = false; fast = false; faster =false; slow =false; slower = false;

        } else if ((mySpeed < recomenndedSpeed) && (recomenndedSpeed < Constants.MAX_SPEED) && (recomenndedSpeed > Constants.MIN_SPEED) && (recomenndedAccelleration < Constants.MAX_ACCELERATION)) {

            // langsamer als empfohlen, also schneller fahren
            fast = true;
            ok = false; stop = false; faster = false; slow = false; slower = false;

            // viel langsamer als empfohlen, also viel schneller fahren
            if((mySpeed + Constants.DIFF_SPEED) < recomenndedSpeed){
                fast = false;
                faster = true;
            }

        } else if ((mySpeed > recomenndedSpeed) && (recomenndedSpeed < Constants.MAX_SPEED) && (recomenndedSpeed > Constants.MIN_SPEED) && (recomenndedAccelleration < Constants.MAX_ACCELERATION)) {

            //schneller als empfohlen --> langsamer fahren
            slow = true;
            ok = false; stop = false; faster = false; fast = false; slower = false;

            // viel schneller als empfohlen >> viel langsamer fahren
            if((mySpeed - Constants.DIFF_SPEED) > recomenndedSpeed){
                slow = false;
                slower = true;
            }

        } else {

            // Geschwindigkeit zu hoch  oder  Geschwindigkeit zu niedrig oder Beschleunigung zu hoch -->> anhalten
            stop =true;
            ok = false; fast = false; faster = false; slow = false; slower = false;
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
            if (stop){
                adviceListener.needToStop();

            // Geschwindigkeit ist okay?
            } else if (ok){
                adviceListener.speedIsOk();

            // Aufforderung schneller zu fahren?
            } else if (fast) {

                adviceListener.needToIncreaseSpeed(countdown);

            } else if(faster){
            // bei Aufforderung noch schneller, zweiten Pfeil auch einblenden
                adviceListener.seriouslyNeedToIncreaseSpeed(countdown);


            // Aufforderung langsamer zu fahren?
            } else if (slow) {

                adviceListener.needToDecreaseSpeed(countdown);
            } else if (slower) {
            // bei Aufforderung noch langsamer, zweiten Pfeil auch einblenden
                adviceListener.seriouslyNeedToDecreaseSpeed(countdown);
            }

            // sekündlich updaten
            handler.postDelayed(this, 1000);
        }
    };

    public void setAdviceListener(AdviceListener adviceListener) {
        this.adviceListener = adviceListener;
    }
}