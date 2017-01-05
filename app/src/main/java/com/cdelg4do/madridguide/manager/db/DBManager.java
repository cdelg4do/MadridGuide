package com.cdelg4do.madridguide.manager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/**
 * This class represents the manager to access the database.
 */
public class DBManager extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_NAME = "madridguide.sqlite";
    public static final int DATABASE_VERSION = 1;

    private static DBManager sharedInstance;    // DBManager is a singleton

    public DBManager(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    // Get access to the singleton
    public synchronized static DBManager getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context (http://bit.ly/6LRzfx)
        if (sharedInstance == null)
            sharedInstance = new DBManager(context.getApplicationContext());

        return sharedInstance;
    }

    // This method is called every time a connection to the db is opened
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Activate foreing keys to have ON_CASCADE deletion
        db.execSQL("PRAGMA foreign_keys = ON");

        // if API LEVEL > 16, use this
        // db.setForeignKeyConstraintsEnabled(true);
    }

    // This method is called when the database file is created
    // (the db creation is lazy: does not happen in the constructor, but when accessed for the 1st time)
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
    }

    // This method is called when opening a database that needs to be upgraded to a newer version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {

            // upgrades for version 1 -> 2:
            case 1:
                Log.i("DBHelper", "Migrating from V1 to V2");
                break;

            // upgrades for version 2 -> 3:
            case 2:
                break;

            // upgrades for version 3 -> 4:
            case 3:
                break;

            default:
                break;
        }
    }


    // Convenience methods to convert types Java <-> SQLite:

    public static int convertBooleanToInt(boolean b) {
        return b ? 1 : 0;
    }

    public static boolean convertIntToBoolean(int b) {
        return b == 0 ? false : true;
    }

    public static Long convertDateToLong(Date date) {

        if (date != null)
            return date.getTime();

        return null;
    }

    public static Date convertLongToDate(Long dateAsLong) {

        if (dateAsLong == null)
            return null;

        return new Date(dateAsLong);
    }


    // Utility methods:

    // Executes all the sql sentences to create the database
    private void createDB(SQLiteDatabase db) {

        for (String sql: DBConstants.CREATE_DATABASE_SCRIPTS) {
            db.execSQL(sql);
        }
    }

    // Returns a connection to the database
    public SQLiteDatabase getDB() {

        SQLiteDatabase db;

        // Note: both getWritableDatabase() and getReadableDatabase() allows to write to the DB
        try {
            db = this.getWritableDatabase();
        }
        catch (SQLiteException ex) {
            db = this.getReadableDatabase();
        }

        return db;
    }
}
