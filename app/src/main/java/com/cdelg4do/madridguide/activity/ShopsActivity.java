package com.cdelg4do.madridguide.activity;

import android.Manifest;
import android.content.Context;
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
import java.util.List;

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
 * This class represents the screen showing the shop list.
 * Implements the LoaderManager.LoaderCallbacks interface to load data from a content resolver.
 */
public class ShopsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE_ASK_FOR_LOCATION_PERMISSION = 123;
    private static final int ID_SHOPS_LOADER = 0;
    private static final String SHOPS_LOADER_SELECTION_KEY = "SHOPS_LOADER_SELECTION_KEY";
    private static final String SHOPS_LOADER_SELECTION_ARGS_KEY = "SHOPS_LOADER_SELECTION_ARGS_KEY";

    private SearchView searchView;
    private SupportMapFragment mapFragment;
    private ShopListFragment shopListFragment;
    private GoogleMap map;
    private ArrayList<Marker> currentMapMarkers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        setTitle(R.string.activity_shops_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the map fragment
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

        // Add a listener to the fragment (to navigate to the Detail activity when a row is clicked)
        shopListFragment.setOnElementClickedListener(new OnElementClickedListener<Shop>() {
            @Override
            public void onElementClicked(Shop shop, int position) {
                Navigator.navigateFromShopsActivityToShopDetailActivity(ShopsActivity.this, shop);
            }
        });


        // Initialize a loader to load all shops from the local cache (database).
        // (first argument of initLoader() is the id for the new loader, if the activity
        // is going to have several loaders, a different id for each one must be used)
        //
        // The loader's properties are defined in onCreateLoader()
        // Binding the returned data to the Activity views is done in onLoadFinished()
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(ID_SHOPS_LOADER, null, this);
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
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                String msg = getString(R.string.permission_granted);
                Utils.showMessage(this, msg, TOAST, null);

                map.setMyLocationEnabled(true);
            }
            else {
                String title = getString(R.string.warning);
                String msg = getString(R.string.permission_denied_location);
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

                // Create a new Shops object from the returned data, and update the UI with it
                final Shops shops = Shops.buildShopsFromCursor(data);
                if (shops.size() > 0) {
                    String msgHead = getString(R.string.activity_shops_showing_results_head);
                    String msgTail = getString(R.string.activity_shops_showing_results_tail);
                    Utils.showMessage(this, msgHead +" "+ shops.size() +" "+ msgTail, TOAST, null);

                    shopListFragment.setShopsAndUpdateFragmentUI(shops);
                    addShopMarkersToMap(shops);
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
        // if the app has been granted permission to do something before doing it.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            map.setMyLocationEnabled(true);
        }

        // If the app has not been granted permission yet,
        // ask the user and check response in onRequestPermissionsResult()
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_FOR_LOCATION_PERMISSION);
        }
    }


    // Add markers to the map, corresponding to a given Shops object
    private void addShopMarkersToMap(@NonNull Shops shops) {

        if (map == null || shops == null)
            return;

        List<Shop> shopList = shops.allShops();
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

        for (Marker marker: currentMapMarkers)
            marker.remove();

        Shops emptyShops = Shops.buildShopsEmpty();
        shopListFragment.setShopsAndUpdateFragmentUI(emptyShops);
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
