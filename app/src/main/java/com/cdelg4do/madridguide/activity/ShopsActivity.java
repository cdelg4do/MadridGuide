package com.cdelg4do.madridguide.activity;

import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_ADDRESS;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_DESCRIPTION_EN;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_DESCRIPTION_ES;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_NAME;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_LATITUDE;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_LONGITUDE;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_ZOOM;
import static com.cdelg4do.madridguide.util.Utils.MessageType.DIALOG;
import static com.cdelg4do.madridguide.util.Utils.MessageType.TOAST;


/**
 * This class represents the screen showing the shop map and the shop list.
 * Implements the LoaderManager.LoaderCallbacks interface to load data from a content resolver.
 */
public class ShopsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String SAVED_STATE_SHOP_LIST_KEY = "SAVED_STATE_SHOP_LIST_KEY";
    private static final String SAVED_STATE_MAP_LATITUDE_KEY = "SAVED_STATE_MAP_LATITUDE_KEY";
    private static final String SAVED_STATE_MAP_LONGITUDE_KEY = "SAVED_STATE_MAP_LONGITUDE_KEY";
    private static final String SAVED_STATE_MAP_ZOOM_KEY = "SAVED_STATE_MAP_ZOOM_KEY";

    private static final int REQUEST_CODE_ASK_FOR_LOCATION_PERMISSION = 123;

    private static final int ID_SHOPS_LOADER = 0;
    private static final String SHOPS_LOADER_SELECTION_KEY = "SHOPS_LOADER_SELECTION_KEY";
    private static final String SHOPS_LOADER_SELECTION_ARGS_KEY = "SHOPS_LOADER_SELECTION_ARGS_KEY";

    private SearchView searchView;
    private SupportMapFragment mapFragment;
    private ShopListFragment shopListFragment;
    private GoogleMap map;
    private ArrayList<Marker> currentMapMarkers;

    private Shops currentShops; // a reference to the shops currently shown, at any moment


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        setTitle(R.string.activity_shops_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the shop list fragment and set a listener to use when a row is selected
        shopListFragment = (ShopListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shops_fragment_shops);

        shopListFragment.setOnElementClickedListener(new OnElementClickedListener<Shop>() {
            @Override
            public void onElementClicked(Shop shop, int position) {
                Navigator.navigateFromShopsActivityToShopDetailActivity(ShopsActivity.this, shop);
            }
        });

        // Get the map fragment and a reference to the map object (this last needs an async call),
        // then configure the map and finish the Activity's setup
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shops_fragment_map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                setupMap();

                // If the activity is being restored (for example after an orientation change),
                // get back to the previous state
                if (savedInstanceState != null) {
                    restoreActivityState(savedInstanceState);
                }

                // If the activity is "new", initialize a CursorLoader to get all existing Shops
                else {
                    LoaderManager loaderManager = getSupportLoaderManager();
                    loaderManager.initLoader(ID_SHOPS_LOADER, null, ShopsActivity.this);
                }
            }
        });
    }


    // In case of need, we just have to save the shops currently shown and the current map position
    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.d("ShopsActivity","Saving activity state...");

        LinkedList<Shop> shopList = (LinkedList<Shop>) currentShops.allElements();
        double lat = map.getCameraPosition().target.latitude;
        double lon = map.getCameraPosition().target.longitude;
        Float zoom = map.getCameraPosition().zoom;

        outState.putSerializable(SAVED_STATE_SHOP_LIST_KEY, shopList);
        outState.putDouble(SAVED_STATE_MAP_LATITUDE_KEY, lat);
        outState.putDouble(SAVED_STATE_MAP_LONGITUDE_KEY, lon);
        outState.putFloat(SAVED_STATE_MAP_ZOOM_KEY, zoom);

        super.onSaveInstanceState(outState);
    }


    // Get the shops and the map position that were showing before destroying the activity,
    // and then paint it all like it was before
    private void restoreActivityState(final @NonNull Bundle savedInstanceState) {

        Log.d("ShopsActivity","Restoring activity state...");

        List<Shop> shopList = (List) savedInstanceState.getSerializable(SAVED_STATE_SHOP_LIST_KEY);
        double lat = savedInstanceState.getDouble(SAVED_STATE_MAP_LATITUDE_KEY, INITIAL_MAP_LATITUDE);
        double lon = savedInstanceState.getDouble(SAVED_STATE_MAP_LONGITUDE_KEY, INITIAL_MAP_LONGITUDE);
        float zoom = savedInstanceState.getFloat(SAVED_STATE_MAP_ZOOM_KEY, INITIAL_MAP_ZOOM);

        currentShops = Shops.buildShopsFromList(shopList);
        map.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), zoom) );

        if (shopList.size() == 0)
            return;

        addShopMarkersToMap(currentShops);
        shopListFragment.setShopsAndUpdateFragmentUI(currentShops);
    }


    // Options menu for this activity (including the search bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shops_activity_options_menu, menu);

        // Configure the search view
        MenuItem searchItem = menu.findItem(R.id.shops_activity_menu_item_search);
        searchView = (SearchView) searchItem.getActionView();
        setupSearchView();

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // This method is called after the activity asked the user for some permission during runtime
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CODE_ASK_FOR_LOCATION_PERMISSION) {

            if (ContextCompat.checkSelfPermission(this,
                    ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {

                String msg = getString(R.string.permission_granted);
                Utils.showMessage(this, msg, TOAST, null);

                map.setMyLocationEnabled(true);
            }
            else {
                String title = getString(R.string.permission_denied_location_title);
                String msg = getString(R.string.permission_denied_location_msg);
                Utils.showMessage(this, msg, DIALOG, title);
            }
        }
    }


    // Implementation of the LoaderManager.LoaderCallbacks<Cursor> interface:

    // Called after LoaderManager.initLoader() and loaderManager.restartLoader(),
    // creates a loader that queries a ContentResolver in background
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        CursorLoader cursorLoader = null;

        // Determine which loader is being initiated (in case the activity has several loaders)
        switch (loaderId) {

            case ID_SHOPS_LOADER:

                // Default selection (no filter)
                String selection = null;
                String[] selectionArgs = null;

                // Get the necessary arguments to filter the query (if they were provided)
                if (args != null &&
                    args.getString(SHOPS_LOADER_SELECTION_KEY,null) != null &&
                    args.getStringArray(SHOPS_LOADER_SELECTION_ARGS_KEY) != null)
                {
                    selection = args.getString(SHOPS_LOADER_SELECTION_KEY,null);
                    selectionArgs = args.getStringArray(SHOPS_LOADER_SELECTION_ARGS_KEY);
                }

                cursorLoader = new CursorLoader(
                        this,                                // Loader's context
                        MadridGuideProvider.SHOPS_URI,      // Uri to get the data from a resolver
                        DBConstants.ALL_COLUMNS_SHOP,      // Fields to retrieve
                        selection,                           // where
                        selectionArgs,                       // where arguments
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

            case ID_SHOPS_LOADER:

                // Create a new Shops object from the returned data, keep a reference to it
                // (in case we need to restore the activity), and update the UI with it
                currentShops = Shops.buildShopsFromCursor(data);

                if (currentShops.size() > 0) {
                    String msgHead = getString(R.string.activity_shops_showing_results_head);
                    String msgTail = getString(R.string.activity_shops_showing_results_tail);
                    Utils.showMessage(this, msgHead +" "+ currentShops.size() +" "+ msgTail, TOAST, null);

                    shopListFragment.setShopsAndUpdateFragmentUI(currentShops);
                    addShopMarkersToMap(currentShops);
                }
                else {
                    String msg = getString(R.string.activity_shops_showing_results_none);
                    Utils.showMessage(this, msg, TOAST, null);
                }

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

    // Initial settings for the map object
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
        // if the app has been granted permission to do something, before doing it.
        if (ContextCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        // If the app has not been granted permission yet, ask the user to grant it
        // (if he already rejected this in the past, show him a short explanation first)
        else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,ACCESS_FINE_LOCATION)) {

                String title = getString(R.string.permission_location_rationale_title);
                String msg = getString(R.string.permission_location_rationale_msg);
                Utils.showAcceptDialog(this, title, msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ActivityCompat.requestPermissions(ShopsActivity.this,
                                new String[]{ACCESS_FINE_LOCATION},
                                REQUEST_CODE_ASK_FOR_LOCATION_PERMISSION
                        );
                    }
                });
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_FOR_LOCATION_PERMISSION
                );
            }
        }
    }


    // Add markers to the map, corresponding to a given Shops object
    private void addShopMarkersToMap(@NonNull Shops shops) {

        if (map == null || shops == null)
            return;

        List<Shop> shopList = shops.allElements();
        currentMapMarkers = new ArrayList<>();

        for (Shop shop: shopList) {

            LatLng shopLocation = new LatLng(shop.getLatitude(),shop.getLongitude());

            // Adding the title here, allows to show it in the default info window of the marked
            // (in case no custom info window is configured)
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(shopLocation)
                    .title(shop.getName());

            // Add to the map a new marker, and store a reference to the Shop in the marker
            // (it will be used when showing custom info windows)
            Marker newMarker = map.addMarker(markerOptions);
            newMarker.setTag(shop);

            // Keep a reference to the added marker (in case we want clean the map later)
            currentMapMarkers.add(newMarker);
        }
    }


    // Removes all existing markers from the map, and empties the list
    private void clearPreviousSearchResults() {

        if (currentMapMarkers != null) {
            for (Marker marker : currentMapMarkers)
                marker.remove();
        }

        currentShops = Shops.buildShopsEmpty();
        shopListFragment.setShopsAndUpdateFragmentUI(currentShops);
    }


    // Sets the hint text and the behavior when the user types on the search box,
    // when he clicks the search button, and when he closes the search view.
    private void setupSearchView() {

        if (searchView == null)
            return;

        searchView.setQueryHint( getString(R.string.activity_shops_menu_search_title) );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {

                clearPreviousSearchResults();   // Clear map and empty the list
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                removeFocusFromSearchView();
                queryShopsByString(query);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {

                queryShopsByString("");   // Start a new query without any text filter
                return false;
            }
        });
    }


    // Removes the focus from the search view and hides the screen keyboard
    // (useful when the user clicks the search button)
    private void removeFocusFromSearchView() {

        if (searchView != null)
            searchView.clearFocus();

        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    // Start a new query filtering by all shops that contain a given string
    // in any of these fields: name, description (EN/ES) and address.
    // (if it is the empty string, then no filter will apply)
    private void queryShopsByString(@NonNull String queryString) {

        // Default filter arguments (no filter)
        Bundle args = null;

        // If there is a string to filter, build appropriate arguments for the loader
        if ( queryString != null && !queryString.isEmpty() ) {

            String selection =
                    KEY_SHOP_NAME +" LIKE ? OR "+
                    KEY_SHOP_DESCRIPTION_EN +" LIKE ? OR "+
                    KEY_SHOP_DESCRIPTION_ES +" LIKE ? OR "+
                    KEY_SHOP_ADDRESS + " LIKE ?";

            String[] selectionArgs = new String[] {
                    "%"+ queryString +"%",
                    "%"+ queryString +"%",
                    "%"+ queryString +"%",
                    "%"+ queryString +"%"
            };

            args = new Bundle();
            args.putString(SHOPS_LOADER_SELECTION_KEY, selection);
            args.putStringArray(SHOPS_LOADER_SELECTION_ARGS_KEY, selectionArgs);
        }

        // Restart the loader with the new arguments
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(ID_SHOPS_LOADER, args, this);
    }
}
