package com.cdelg4do.madridguide.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.fragment.ShopListFragment;
import com.cdelg4do.madridguide.interactor.GetAllShopsFromLocalCacheInteractor;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.navigator.Navigator;
import com.cdelg4do.madridguide.view.OnElementClickedListener;

/**
 * This class represents the screen showing the shop list.
 */
public class ShopsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShopListFragment shopListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        // Get the ShopList fragment
        shopListFragment = (ShopListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shops_fragment_shops);


        // Build a Shops object with all shops from the local database (in background)
        // and then pass it to Fragment to populate the shop list (in the main thread)
        GetAllShopsFromLocalCacheInteractor loadLocalShops = new GetAllShopsFromLocalCacheInteractor();

        loadLocalShops.execute(this, new GetAllShopsFromLocalCacheInteractor.OnGetAllShopsFromLocalCacheInteractorCompletion() {

            @Override
            public void completion(Shops shops) {
                shopListFragment.setShopsAndUpdateFragmentUI(shops);
            }
        });


        // Add an anonymous OnElementClickedListener to the fragment,
        // that navigates to the Shop Detail activity when a cell is clicked
        shopListFragment.setOnElementClickedListener(new OnElementClickedListener<Shop>() {

            @Override
            public void onElementClicked(Shop shop, int position) {
                Navigator.navigateFromShopsActivityToShopDetailActivity(shop, ShopsActivity.this);
            }
        });
    }


    // Implementation of the LoaderManager.LoaderCallbacks<Cursor> interface:

    // Called when a new loader is created: launch query
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    // Called when a previously created loader has finished its load
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    // Called when a previously created loader is being reset, and thus making its data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
