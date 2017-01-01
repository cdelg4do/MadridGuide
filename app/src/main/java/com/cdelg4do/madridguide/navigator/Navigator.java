package com.cdelg4do.madridguide.navigator;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.cdelg4do.madridguide.activity.MainActivity;
import com.cdelg4do.madridguide.activity.ShopDetailActivity;
import com.cdelg4do.madridguide.activity.ShopsActivity;
import com.cdelg4do.madridguide.model.Shop;

import static com.cdelg4do.madridguide.util.Constants.INTENT_KEY_DETAIL_SHOP;

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

    /**
     * Navigates from an instance of ShopsActivity to another of ShopDetailActivity
     *
     * @param shopsActivity context for the intent created during the operation
     * @return              a reference to the intent created (useful for testing)
     */
    public static Intent navigateFromShopsActivityToShopDetailActivity(final @NonNull Shop shop, final ShopsActivity shopsActivity) {

        final Intent i = new Intent(shopsActivity, ShopDetailActivity.class);
        i.putExtra(INTENT_KEY_DETAIL_SHOP,shop);
        shopsActivity.startActivity(i);
        return i;
    }
}
