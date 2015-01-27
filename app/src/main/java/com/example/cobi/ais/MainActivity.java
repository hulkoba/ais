package com.example.cobi.ais;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private static TextView textView;
    private GpsTracker gpstracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpstracker = new GpsTracker();
        textView = (TextView) findViewById(R.id.gps);

        if(!gpstracker.gpsIsActive(this)) {
            textView.setText("Bitte aktiviere GPS");
        } else {
            gpstracker.startGpsTracker();
        }
    }

    public static void showPosition(String string) {
        textView.setText(string);
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

    @Override
    // kurz vor Beendigung
    protected void onDestroy() {
        super.onDestroy();
        // Empfänger abmelden
        gpstracker.quitGpsTracker();
    }
}
