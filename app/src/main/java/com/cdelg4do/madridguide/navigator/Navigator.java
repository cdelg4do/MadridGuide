package com.cdelg4do.madridguide.navigator;

import android.content.Intent;

import com.cdelg4do.madridguide.activity.MainActivity;
import com.cdelg4do.madridguide.activity.ShopsActivity;

/**
 * This class manages all the navigation between the activities of the application
 */
public class Navigator {

    /**
     * Navigates from an instance of MainActivity to another of ShopsActivity
     *
     * @param mainActivity  context for the intent created during the operation
     * @return              a reference to the intent created (useful for testing)
     */
    public static Intent navigateFromMainActivityToShopsActivity(final MainActivity mainActivity) {

        final Intent i = new Intent(mainActivity, ShopsActivity.class);
        mainActivity.startActivity(i);
        return i;
    }
}
