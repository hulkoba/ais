package com.example.cobi.ais;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cobi on 27.01.15.
 */
public class DBAccess extends SQLiteOpenHelper {
    private SQLiteDatabase db;

    public DBAccess(Context activity, String dbName) {
        super(activity, dbName, null, 1);
        db = getWritableDatabase();
        //db = getReadableDatabase(); // nur Lesezugriff
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            // Tabelle LSA anlegen
            String sqlLSA = "CREATE TABLE lsaDB " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(20) NOT NULL, " +
                    "red DATE, " +
                    "green DATE, " +
                    "lat REAL NOT NULL, " +
                    "lon REAL NOT NULL, " + // REAL=Fließkommazahlen
                    "traffic INTEGER NOT NULL)"; //0=false, 1=true

            //Schaltplan
            String sqlTUV = "CREATE TABLE tuvDB" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(20) NOT NULL, " +
                    "red DATE, " +
                    "green DATE)";

            db.execSQL(sqlLSA);
            db.execSQL(sqlTUV);

        } catch (Exception exc) {
            Log.e("datenbankfehler: ", exc.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //1 im Konstruktor = Versionsnummer. Kann hier geupdated werden
    }

    @Override //nur um sicher zu gehen
    public synchronized void close() {
        if(db != null) {db.close(); db = null;}
        super.close();
    }
}
