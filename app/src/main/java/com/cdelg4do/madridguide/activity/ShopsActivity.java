package com.cdelg4do.madridguide.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.fragment.ShopListFragment;
import com.cdelg4do.madridguide.interactor.GetAllShopsFromLocalCacheInteractor;
import com.cdelg4do.madridguide.manager.db.DBConstants;
import com.cdelg4do.madridguide.manager.db.provider.MadridGuideProvider;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.navigator.Navigator;
import com.cdelg4do.madridguide.util.Utils;
import com.cdelg4do.madridguide.view.OnElementClickedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static com.cdelg4do.madridguide.util.Constants.MAP_CENTER_LATITUDE;
import static com.cdelg4do.madridguide.util.Constants.MAP_CENTER_LONGITUDE;
import static com.cdelg4do.madridguide.util.Utils.MessageType.DIALOG;


/**
 * This class represents the screen showing the shop list.
 * Implements the LoaderManager.LoaderCallbacks interface to load data from a content resolver.
 */
public class ShopsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE_ASK_LOCATION_PERMISSION = 123;
    private static final int SHOPS_LOADER_ID = 0;

    private MapFragment mapFragment;
    private ShopListFragment shopListFragment;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        // Get the map fragment
        // (note: a MapFragment is a native Fragment, NOT a support fragment)
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.activity_shops_fragment_map);

        // Get a reference to the fragment's map object
        // (is an async call, the map needs to be initiated first so it can be gotten syncronously)
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                setupMap();
            }
        });

        // Get the shop list fragment
        shopListFragment = (ShopListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shops_fragment_shops);

        // Load all shops from the database using a loader:
        // This is made by using a (Support)LoaderManager that initializes the loader.
        // (first argument of initLoader() is the id for the new loader, if the activity
        // is going to have several loaders, a different id for each one must be used).
        // The loader's behavior is defined in onCreateLoader(), onLoadFinished() & onLoaderReset().

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(SHOPS_LOADER_ID, null, this);


        /*
        // This is an alternate way to load the shop list: using an interactor that loads data from the
        // database in the background, then manages the data loaded in the foreground:

        new GetAllShopsFromLocalCacheInteractor().execute(this,
                new GetAllShopsFromLocalCacheInteractor.GetAllShopsFromLocalCacheInteractorListener() {

                    @Override
                    public void onGetAllShopsFromLocalCacheFinished(Shops shops) {
                        shopListFragment.setShopsAndUpdateFragmentUI(shops);
                    }
                }
        );
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
    // (not implemented)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    // Auxiliary methods:

    // Initial settings for the map object (if it has already initialized)
    private void setupMap() {

        if (map == null)
            return;

        // Set the map type, user cannot rotate it, and show the zoom buttons and the my-location button
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);


        // Center the map
        LatLng mapCenter = new LatLng(MAP_CENTER_LATITUDE, MAP_CENTER_LONGITUDE);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mapCenter)
                .zoom(12)
                .build();

        map.animateCamera( CameraUpdateFactory.newCameraPosition(cameraPosition) );

        // Add the shops to the map
        addShopMarkers();

        // Show the user location
        enableUserLocationOnMap();
    }


    private void enableUserLocationOnMap() {

        // Starting on API 23, it is necessary to perform a runtime check to see
        // if the app has been granted permission to do something before doing it.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            map.setMyLocationEnabled(true);
        }

        // If the app has not been granted permission yet,
        // ask the user and check response in onRequestPermissionsResult()
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CODE_ASK_LOCATION_PERMISSION) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                map.setMyLocationEnabled(true);
            }
            else {
                Utils.showMessage(this,"MadridGuide will not be able to show your location on the map!", DIALOG, "Warning");
            }
        }
    }


    private void addShopMarkers() {

        new GetAllShopsFromLocalCacheInteractor().execute(this,
                new GetAllShopsFromLocalCacheInteractor.GetAllShopsFromLocalCacheInteractorListener() {

                    @Override
                    public void onGetAllShopsFromLocalCacheFinished(Shops shops) {

                        List<Shop> shopList = shops.allShops();

                        for (int i=0; i<shopList.size(); i++)
                            addShopMarker( shopList.get(i) );
                    }
                }
        );

    }


    private void addShopMarker(Shop shop) {

        if (map == null)
            return;

        String shopName = shop.getName();
        LatLng shopLocation = new LatLng(shop.getLatitude(),shop.getLongitude());

        MarkerOptions marker = new MarkerOptions().title(shopName).position(shopLocation);
        map.addMarker(marker);
    }
}
