package com.cdelg4do.madridguide;

import android.app.Application;
import android.content.Context;

import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.model.Shop;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * This class maintains the global application state.
 */

public class MadridGuideApp extends Application {

    // App context: weak reference so that we avoid memory leaks when closing the app
    // (see http://bit.ly/6LRzfx)
    private static WeakReference<Context> appContext;

    // Entry point of the app
    @Override
    public void onCreate() {
        super.onCreate();

        // Keep a copy of the application context
        appContext = new WeakReference<Context>( getApplicationContext() );

        // Settings for Picasso
        Picasso.with(getApplicationContext()).setLoggingEnabled(true);
        Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);

        // Populate the database
        insertTestDataIntoTheDatabase();
    }

    // Override to warn about memory warnings
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    // Get the app context
    public static Context getAppContext() {
        return appContext.get();
    }


    private void insertTestDataIntoTheDatabase() {

        ShopDAO shopDAO = new ShopDAO( getApplicationContext() );

        shopDAO.deleteAll();

        for (int i=0; i<30; i++) {

            Shop shop = new Shop(i, "Shop "+i);
            shop.setLogoImgUrl("http://lorempixel.com/200/200/");

            shopDAO.insert(shop);
        }
    }
}
