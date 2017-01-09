package com.cdelg4do.madridguide.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.adapter.ShopInfoWindowAdapter;
import com.cdelg4do.madridguide.fragment.ShopListFragment;
import com.cdelg4do.madridguide.manager.db.DBConstants;
import com.cdelg4do.madridguide.manager.db.provider.MadridGuideProvider;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.navigator.Navigator;
import com.cdelg4do.madridguide.util.Utils;
import com.cdelg4do.madridguide.view.OnElementClickedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_LATITUDE;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_LONGITUDE;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_ZOOM;
import static com.cdelg4do.madridguide.util.Utils.MessageType.DIALOG;


/**
 * This class represents the screen showing the shop list.
 * Implements the LoaderManager.LoaderCallbacks interface to load data from a content resolver.
 */
public class ShopsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE_ASK_LOCATION_PERMISSION = 123;
    private static final int SHOPS_LOADER_ID = 0;

    private SupportMapFragment mapFragment;
    private ShopListFragment shopListFragment;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        // Get the map fragment (note: a MapFragment is a native Fragment, NOT a support fragment)

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shops_fragment_map);

        // Get a reference to the map (is an async call, so configure the map only after it is received)
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

        // Add a listener to the fragment that navigates to the Shop Detail activity when a cell is clicked
        shopListFragment.setOnElementClickedListener(new OnElementClickedListener<Shop>() {

            @Override
            public void onElementClicked(Shop shop, int position) {
                Navigator.navigateFromShopsActivityToShopDetailActivity(ShopsActivity.this, shop);
            }
        });
    }


    // This method is called after the activity asked the user for some permission during runtime
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


    // Implementation of the LoaderManager.LoaderCallbacks<Cursor> interface:

    // Called after LoaderManager.initLoader(), creates the loader that queries a ContentResolver in background
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

    // Called when a previously created loader has finished its load, this runs in the main thread
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {

            case SHOPS_LOADER_ID:
                // Create a new Shops object from the returned data, and pass it to the fragment
                final Shops shops = Shops.buildShopsFromCursor(data);
                shopListFragment.setShopsAndUpdateFragmentUI(shops);
                addShopMarkers(shops);
                break;

            default:
                break;
        }
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

        // Set an adapter to show customized info windows for the markers
        map.setInfoWindowAdapter(new ShopInfoWindowAdapter(this));

        // Define a listener to take action when the user clicks on the info window of a marker
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Shop shop = (Shop) marker.getTag();
                Navigator.navigateFromShopsActivityToShopDetailActivity(ShopsActivity.this, shop);
            }
        });

        // Center the map to its initial position and zoom
        LatLng mapCenter = new LatLng(INITIAL_MAP_LATITUDE, INITIAL_MAP_LONGITUDE);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mapCenter)
                .zoom(INITIAL_MAP_ZOOM)
                .build();

        map.animateCamera( CameraUpdateFactory.newCameraPosition(cameraPosition) );

        // Show the user location (asks the user for permission, if necessary)
        enableUserLocationOnMap();
    }


    // This method enables the my-location layer to show the user location on the map
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


    /*
    // Load the shops directly from the cache (database) by using an interactor,
    // then add the shop markers to the map.
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
    */


    // Add markers to the map, corresponding to a given Shops object
    private void addShopMarkers(@NonNull Shops shops) {

        if (map == null || shops == null)
            return;

        List<Shop> shopList = shops.allShops();

        for (Shop shop: shopList)
            addShopMarker(shop);
    }


    private void addShopMarker(@NonNull Shop shop) {

        if (map == null || shop == null)
            return;

        LatLng shopLocation = new LatLng(shop.getLatitude(),shop.getLongitude());

        // Adding the title here, allows to show it in the default info window of the marked
        // (in case no custom info window is configured)
        MarkerOptions markerOptions = new MarkerOptions()
                .position(shopLocation)
                .title(shop.getName());

        // Add to the map a new marker, and store a reference to the Shop in the marker
        // (it will be used when showing custom info windows)
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(shop);
    }
}
