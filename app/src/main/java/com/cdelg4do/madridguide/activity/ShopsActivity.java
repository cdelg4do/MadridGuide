package com.cdelg4do.madridguide.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.fragment.ShopListFragment;
import com.cdelg4do.madridguide.manager.db.DBConstants;
import com.cdelg4do.madridguide.manager.db.provider.MadridGuideProvider;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.navigator.Navigator;
import com.cdelg4do.madridguide.view.OnElementClickedListener;


/**
 * This class represents the screen showing the shop list.
 * Implements the LoaderManager.LoaderCallbacks interface to load data from a content resolver.
 */
public class ShopsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SHOPS_LOADER_ID = 0;

    private ShopListFragment shopListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        // Get the ShopList fragment
        shopListFragment = (ShopListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shops_fragment_shops);


        // Load all shops from the database using a loader:
        // This is made by using a (Support)LoaderManager that inits the loader.
        // (first argument of initLoader() is the id for the new loader, if the activity
        // is going to have several loaders, a different id for each one must be used).
        // The loader's behavior is defined in onCreateLoader(), onLoadFinished() & onLoaderReset().

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(SHOPS_LOADER_ID, null, this);


        /*
        // This is an alternate way to load the shop list: using a closure that loads data from the
        // database in the background, then manages the data loaded in the foreground:
        // Build a Shops object with all shops from the local database (in background)
        // and then pass it to Fragment to populate the shop list (in the main thread)

        GetAllShopsFromLocalCacheInteractor loadLocalShops = new GetAllShopsFromLocalCacheInteractor();

        loadLocalShops.execute(this, new GetAllShopsFromLocalCacheInteractor.OnGetAllShopsFromLocalCacheInteractorCompletion() {

            @Override
            public void completion(Shops shops) {
                shopListFragment.setShopsAndUpdateFragmentUI(shops);
            }
        });
        */


        // Add an anonymous OnElementClickedListener to the fragment,
        // that navigates to the Shop Detail activity when a cell is clicked
        shopListFragment.setOnElementClickedListener(new OnElementClickedListener<Shop>() {

            @Override
            public void onElementClicked(Shop shop, int position) {
                Navigator.navigateFromShopsActivityToShopDetailActivity(ShopsActivity.this, shop);
            }
        });
    }


    // Implementation of the LoaderManager.LoaderCallbacks<Cursor> interface:

    // Called after a new loader is init
    // (creates the loader that launches a background query against a ContentResolver)
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        CursorLoader cursorLoader = null;

        // Determine which loader is being initiated (in case the activity has several loaders)
        switch (loaderId) {

            case SHOPS_LOADER_ID:

                cursorLoader = new CursorLoader(
                        this,                                // Loader's context
                        MadridGuideProvider.SHOPS_URI,      // Uri to get the data from a resolver
                        DBConstants.ALL_COLUMNS_SHOP,      // Fields to retrieve
                        null,                                // where
                        null,                                // where arguments
                        null                                 // order by
                );

                break;

            default:
                break;
        }

        return cursorLoader;
    }

    // Called when a previously created loader has finished its load, runs in the main thread
    // (creates a new Shops object from the returned data, and passes it to the fragment)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        final Shops shops = Shops.buildShopsFromCursor(data);
        shopListFragment.setShopsAndUpdateFragmentUI(shops);
    }

    // Called when a previously created loader is being reset, and thus making its data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
