package com.example.cobi.ais;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;


public class MainActivity extends ActionBarActivity{

    private TextView countdownTextView;
    private ImageView okImageView, trafficImageView, stopImageView, fastImageView, fasterImageView, slowImageView, slowerImageView;

    private SpeedHandler speedHandler;
    private GpsTracker gpstracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        checkIfFirstRun();

        init();
        if(!gpstracker.gpsIsActive(this)) {
            showDialog(R.string.achtung, R.string.gpsActivate);
        } else {
            gpstracker.startGpsTracker();
            gpstracker.setLSAListener(new LSAListener() {
                @Override
                public void onNewNearestLSA(LSA lsa, Location loc) {
                    // sobald die nächste Ampel gesetzt ist,
                    // holt der SpeedHandler deren Signalschaltplan
                    speedHandler.fetchCurrentSzpl(lsa, loc);
                }
            });
        }
    }

    private void init(){
        JSONParser jsonParser = new JSONParser();
        InputStream inputStream = getResources().openRawResource(R.raw.lsas);
        jsonParser.fetchJSON(inputStream); //liest LSA JSON

        speedHandler =  new SpeedHandler();
        gpstracker = new GpsTracker();

        // ImageViews
        countdownTextView = (TextView) findViewById(R.id.countdown);
        okImageView = (ImageView) findViewById(R.id.ok);
        trafficImageView = (ImageView) findViewById(R.id.mep);
        stopImageView = (ImageView) findViewById(R.id.stop);
        fastImageView = (ImageView) findViewById(R.id.pfeil);
        fasterImageView = (ImageView) findViewById(R.id.pfeil2);
        slowImageView = (ImageView) findViewById(R.id.pfeil_down);
        slowerImageView = (ImageView) findViewById(R.id.pfeil_down2);
    }

    // Dialog für Rückmeldungen (gps aktivieren, stvo hat Vorrang)
    private void showDialog(int title, int msg){

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog));
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setNeutralButton(R.string.ok,null).show();
    }

    private void checkIfFirstRun(){
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            showDialog(R.string.achtung, R.string.onStart);
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit().putBoolean("isFirstRun", false).apply();
        }
    }

    private void setCountdownTextView(int countdown) {
        if (countdownTextView.getVisibility() == View.INVISIBLE) {
            countdownTextView.setVisibility(View.VISIBLE);
        }
        countdownTextView.setText(String.valueOf(countdown));
    }

    @Override
    protected void onPause() {
        gpstracker.quitGpsTracker();
        super.onPause();
    }
    @Override //aktiv
    protected void onResume() {
        gpstracker.startGpsTracker();
        speedHandler.setAdviceListener(new AdviceListener(){

            @Override
            public void lsaIsTrafficDependent() {
                Log.d("listener", "lsaIsTrafficDependent");
                if(trafficImageView.getVisibility() == View.INVISIBLE) {
                    trafficImageView.setVisibility(View.VISIBLE);
                }

                if(countdownTextView.getVisibility() == View.VISIBLE) {
                    countdownTextView.setVisibility(View.INVISIBLE);
                }
                if(okImageView.getVisibility() == View.VISIBLE) {
                    okImageView.setVisibility(View.INVISIBLE);
                }
                if(stopImageView.getVisibility() == View.VISIBLE) {
                    stopImageView.setVisibility(View.INVISIBLE);
                }
                if(slowImageView.getVisibility() == View.VISIBLE) {
                    slowImageView.setVisibility(View.INVISIBLE);
                }
                if(slowerImageView.getVisibility() == View.VISIBLE) {
                    slowerImageView.setVisibility(View.INVISIBLE);
                }
                if(fastImageView.getVisibility() == View.VISIBLE) {
                    fastImageView.setVisibility(View.INVISIBLE);
                }
                if(fasterImageView.getVisibility() == View.VISIBLE) {
                    fasterImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void needToStop() {
                Log.d("listener", "needToStop");

                if(stopImageView.getVisibility() == View.INVISIBLE) {
                    stopImageView.setVisibility(View.VISIBLE);
                }

                if(countdownTextView.getVisibility() == View.VISIBLE) {
                    countdownTextView.setVisibility(View.INVISIBLE);
                }
                if(okImageView.getVisibility() == View.VISIBLE) {
                    okImageView.setVisibility(View.INVISIBLE);
                }
                if(trafficImageView.getVisibility() == View.VISIBLE) {
                    trafficImageView.setVisibility(View.INVISIBLE);
                }
                if(slowImageView.getVisibility() == View.VISIBLE) {
                    slowImageView.setVisibility(View.INVISIBLE);
                }
                if(slowerImageView.getVisibility() == View.VISIBLE) {
                    slowerImageView.setVisibility(View.INVISIBLE);
                }
                if(fastImageView.getVisibility() == View.VISIBLE) {
                    fastImageView.setVisibility(View.INVISIBLE);
                }
                if(fasterImageView.getVisibility() == View.VISIBLE) {
                    fasterImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void speedIsOk() {
                Log.d("listener", "speedIsOk");

                if (okImageView.getVisibility() == View.INVISIBLE) {
                    okImageView.setVisibility(View.VISIBLE);
                }

                if(countdownTextView.getVisibility() == View.VISIBLE) {
                    countdownTextView.setVisibility(View.INVISIBLE);
                }
                if(stopImageView.getVisibility() == View.VISIBLE) {
                    stopImageView.setVisibility(View.INVISIBLE);
                }
                if(trafficImageView.getVisibility() == View.VISIBLE) {
                    trafficImageView.setVisibility(View.INVISIBLE);
                }
                if(slowImageView.getVisibility() == View.VISIBLE) {
                    slowImageView.setVisibility(View.INVISIBLE);
                }
                if(slowerImageView.getVisibility() == View.VISIBLE) {
                    slowerImageView.setVisibility(View.INVISIBLE);
                }
                if(fastImageView.getVisibility() == View.VISIBLE) {
                    fastImageView.setVisibility(View.INVISIBLE);
                }
                if(fasterImageView.getVisibility() == View.VISIBLE) {
                    fasterImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void needToIncreaseSpeed(int countdown) {
                Log.d("listener", "needToIncrease");
                setCountdownTextView(countdown);

                if (fastImageView.getVisibility() == View.INVISIBLE) {
                    fastImageView.setVisibility(View.VISIBLE);
                }


                if(okImageView.getVisibility() == View.VISIBLE) {
                    okImageView.setVisibility(View.INVISIBLE);
                }
                if(stopImageView.getVisibility() == View.VISIBLE) {
                    stopImageView.setVisibility(View.INVISIBLE);
                }
                if(trafficImageView.getVisibility() == View.VISIBLE) {
                    trafficImageView.setVisibility(View.INVISIBLE);
                }
                if(slowImageView.getVisibility() == View.VISIBLE) {
                    slowImageView.setVisibility(View.INVISIBLE);
                }
                if(slowerImageView.getVisibility() == View.VISIBLE) {
                    slowerImageView.setVisibility(View.INVISIBLE);
                }
                if(fasterImageView.getVisibility() == View.VISIBLE) {
                    fasterImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void seriouslyNeedToIncreaseSpeed(int countdown) {
                Log.d("listener", "seriouslyNeedToIncrease");
                setCountdownTextView(countdown);

                if (fastImageView.getVisibility() == View.INVISIBLE) {
                    fastImageView.setVisibility(View.VISIBLE);
                }
                if (fasterImageView.getVisibility() == View.INVISIBLE) {
                    fasterImageView.setVisibility(View.VISIBLE);
                }

                if(okImageView.getVisibility() == View.VISIBLE) {
                    okImageView.setVisibility(View.INVISIBLE);
                }
                if(stopImageView.getVisibility() == View.VISIBLE) {
                    stopImageView.setVisibility(View.INVISIBLE);
                }
                if(trafficImageView.getVisibility() == View.VISIBLE) {
                    trafficImageView.setVisibility(View.INVISIBLE);
                }
                if(slowImageView.getVisibility() == View.VISIBLE) {
                    slowImageView.setVisibility(View.INVISIBLE);
                }
                if(slowerImageView.getVisibility() == View.VISIBLE) {
                    slowerImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void needToDecreaseSpeed(int countdown) {
                Log.d("listener", "needToDecrease");
                setCountdownTextView(countdown);

                if (slowImageView.getVisibility() == View.INVISIBLE) {
                    slowImageView.setVisibility(View.VISIBLE);
                }


                if(okImageView.getVisibility() == View.VISIBLE) {
                    okImageView.setVisibility(View.INVISIBLE);
                }
                if(stopImageView.getVisibility() == View.VISIBLE) {
                    stopImageView.setVisibility(View.INVISIBLE);
                }
                if(trafficImageView.getVisibility() == View.VISIBLE) {
                    trafficImageView.setVisibility(View.INVISIBLE);
                }
                if(fastImageView.getVisibility() == View.VISIBLE) {
                    fastImageView.setVisibility(View.INVISIBLE);
                }
                if(fasterImageView.getVisibility() == View.VISIBLE) {
                    fasterImageView.setVisibility(View.INVISIBLE);
                }
                if(slowerImageView.getVisibility() == View.VISIBLE) {
                    slowerImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void seriouslyNeedToDecreaseSpeed(int countdown) {
                Log.d("listener", "seriouslyNeedToDecrease");
                setCountdownTextView(countdown);

                if (slowImageView.getVisibility() == View.INVISIBLE) {
                    slowImageView.setVisibility(View.VISIBLE);
                }
                if (slowerImageView.getVisibility() == View.INVISIBLE) {
                    slowerImageView.setVisibility(View.VISIBLE);
                }

                if(okImageView.getVisibility() == View.VISIBLE) {
                    okImageView.setVisibility(View.INVISIBLE);
                }
                if(stopImageView.getVisibility() == View.VISIBLE) {
                    stopImageView.setVisibility(View.INVISIBLE);
                }
                if(trafficImageView.getVisibility() == View.VISIBLE) {
                    trafficImageView.setVisibility(View.INVISIBLE);
                }
                if(fastImageView.getVisibility() == View.VISIBLE) {
                    fastImageView.setVisibility(View.INVISIBLE);
                }
                if(fasterImageView.getVisibility() == View.VISIBLE) {
                    fasterImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
        super.onResume();
    }

    @Override
    protected void onStop() {
        gpstracker.quitGpsTracker();
        super.onStop();
    }

    @Override
    // kurz vor Beendigung,wird von finish() aufgerufen
    protected void onDestroy() {
        // Empfänger abmelden
        gpstracker.quitGpsTracker();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }
}