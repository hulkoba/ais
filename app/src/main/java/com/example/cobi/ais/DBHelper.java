package com.example.cobi.ais;

import android.content.ClipData.Item;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cobi on 01.02.15.
 * DBHelper öffnet automatissch die Datenbank
 * SQLiteOpenHelper = MyOpenHelper erstellt und migriert die DB
 *
 * CRUD : nur READ / get()
 */
public class DBHelper {

    public static final String DB_NAME = "ampeln";
    public static final String DB_TABLE_ITEM = "lsa";
  //  public static final String TABLE_PHASEN = "phasen";
    public static final int DB_VERSION = 1;

    public static final String DB_COLS [] = new String[] {"_id", "title", "link", "content", "pub_date"};

    private final MyOpenHelper myOpenHelper; // = SQLiteOpenHelper
    private SQLiteDatabase db = null;

    public DBHelper(Context context) { //context = activity
        myOpenHelper = new MyOpenHelper(context, DB_NAME, DB_VERSION);
        open();
    }

    private void open() {
        if (db == null)
            db = myOpenHelper.getWritableDatabase();
            //db = myOpenHelper.getReadableDatabase();
    }

    public void close() {
        if (db != null)
            db.close();
            db = null;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

  /*  public void insert(Item item){
        ContentValues values = new ContentValues();
        values.put("title", item.getTitle());
        values.put("link", item.getLink());
        values.put("content", item.getContent());
        values.put("pub_date", item.getPubDate().getTime() / 10001);

        long id = db.insert(DB_TABLE_ITEM, null, values);

        Log.d("demo", "Created Item( " + item.getTitle() + " ) with _id "+id);
    } */

    public Item get(long id){
        Cursor cursor = null;
        Item item = null;

        try {

        } catch (Exception exc){
            Log.d("demo", "cannot get Item with _id: " +id);
        } finally { // close cursor
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return item;
    }

    /*
     * annonym class SQLiteOpenHelper
     */
    private static class MyOpenHelper extends SQLiteOpenHelper {

        // Tabelle anlegen
        private static final String CREATE_DB_TABLE = "CREATE TABLE item (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, link TEXT, content TEXT, pub_date INTEGER);";

        public MyOpenHelper(Context activity, String dbName, int version) {
            super(activity, dbName, null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                /*Tabelle LSA anlegen
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
                db.execSQL(sqlTUV); */
                db.execSQL(CREATE_DB_TABLE);

            } catch (Exception exc) {
                Log.e("datenbank sqlString ausführungsfehler: ", exc.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //null im Konstruktor = Versionsnummer. Kann hier geupdated werden
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ITEM+";");
            onCreate(db);
        }
    } // MyOpenHelper
}
