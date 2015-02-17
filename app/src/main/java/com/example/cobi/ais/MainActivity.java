package com.example.cobi.ais;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.InputStream;

public class MainActivity extends ActionBarActivity {
    private static TextView gpsTextView;
    private static TextView lsaTextView;
    private GpsTracker gpstracker;
    private InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showStartDialog();

        gpstracker = new GpsTracker();
        gpsTextView = (TextView) findViewById(R.id.gps);

        inputStream = getResources().openRawResource(R.raw.lsas);
        JSONParser.fetchJSON(inputStream); //liest LSA JSON

        JSONParser.getLsaArray();


        if(!gpstracker.gpsIsActive(this)) {
            gpsTextView.setText("Bitte aktiviere GPS");
        }
        gpstracker.startGpsTracker();
    }

    private void showStartDialog(){
       // new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog));
        builder.setMessage(R.string.onStart);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showPosition(String string) {
        gpsTextView.setText(string);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override // Daten übergeben?
    protected void onPause() {
        gpstracker.quitGpsTracker();
        super.onPause();
    }
    @Override //aktiv
    protected void onResume() {
        gpstracker.startGpsTracker();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
