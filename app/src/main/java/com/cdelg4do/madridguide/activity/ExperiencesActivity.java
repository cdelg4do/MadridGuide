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
import com.cdelg4do.madridguide.adapter.ExperienceInfoWindowAdapter;
import com.cdelg4do.madridguide.fragment.ExperienceListFragment;
import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Experiences;
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
import static com.cdelg4do.madridguide.manager.db.DBConstants.ALL_COLUMNS_EXPERIENCE;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_ADDRESS;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_DESCRIPTION_EN;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_DESCRIPTION_ES;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_NAME;
import static com.cdelg4do.madridguide.manager.db.provider.MadridGuideProvider.EXPERIENCES_URI;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_LATITUDE;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_LONGITUDE;
import static com.cdelg4do.madridguide.util.Constants.INITIAL_MAP_ZOOM;
import static com.cdelg4do.madridguide.util.Utils.MessageType.DIALOG;
import static com.cdelg4do.madridguide.util.Utils.MessageType.TOAST;


/**
 * This class represents the screen showing the activity map and the activity list.
 * Implements the LoaderManager.LoaderCallbacks interface to load data from a content resolver.
 */
public class ExperiencesActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String SAVED_STATE_EXPERIENCE_LIST_KEY = "SAVED_STATE_EXPERIENCE_LIST_KEY";
    private static final String SAVED_STATE_MAP_LATITUDE_KEY = "SAVED_STATE_MAP_LATITUDE_KEY";
    private static final String SAVED_STATE_MAP_LONGITUDE_KEY = "SAVED_STATE_MAP_LONGITUDE_KEY";
    private static final String SAVED_STATE_MAP_ZOOM_KEY = "SAVED_STATE_MAP_ZOOM_KEY";

    private static final int REQUEST_CODE_ASK_FOR_LOCATION_PERMISSION = 123;

    private static final int ID_EXPERIENCES_LOADER = 0;
    private static final String EXPERIENCES_LOADER_SELECTION_KEY = "EXPERIENCES_LOADER_SELECTION_KEY";
    private static final String EXPERIENCES_LOADER_SELECTION_ARGS_KEY = "EXPERIENCES_LOADER_SELECTION_ARGS_KEY";

    private SearchView searchView;
    private SupportMapFragment mapFragment;
    private ExperienceListFragment experienceListFragment;
    private GoogleMap map;
    private ArrayList<Marker> currentMapMarkers;

    private Experiences currentExperiences; // a reference to the experiences currently shown, at any moment


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiences);

        setTitle(R.string.activity_experiences_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the experience list fragment and set a listener to use when a row is selected
        experienceListFragment = (ExperienceListFragment) getSupportFragmentManager().
                findFragmentById(R.id.activity_experiences_fragment_experiences);

        experienceListFragment.setOnElementClickedListener(new OnElementClickedListener<Experience>() {
            @Override
            public void onElementClicked(Experience experience, int position) {
                Navigator.navigateFromExperiencesActivityToExperienceDetailActivity(ExperiencesActivity.this, experience);
            }
        });

        // Get the map fragment and a reference to the map object (this last needs an async call),
        // then configure the map and finish the Activity's setup
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_experiences_fragment_map);

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

                // If the activity is "new", initialize a CursorLoader to get all existing Experiences
                else {
                    LoaderManager loaderManager = getSupportLoaderManager();
                    loaderManager.initLoader(ID_EXPERIENCES_LOADER, null, ExperiencesActivity.this);
                }
            }
        });
    }


    // In case of need, we have to save the experiences currently shown and the current map position
    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.d("ExperiencesActivity","Saving activity state...");

        LinkedList<Experience> experienceList = (LinkedList<Experience>) currentExperiences.allElements();
        double lat = map.getCameraPosition().target.latitude;
        double lon = map.getCameraPosition().target.longitude;
        Float zoom = map.getCameraPosition().zoom;

        outState.putSerializable(SAVED_STATE_EXPERIENCE_LIST_KEY, experienceList);
        outState.putDouble(SAVED_STATE_MAP_LATITUDE_KEY, lat);
        outState.putDouble(SAVED_STATE_MAP_LONGITUDE_KEY, lon);
        outState.putFloat(SAVED_STATE_MAP_ZOOM_KEY, zoom);

        super.onSaveInstanceState(outState);
    }


    // Get the experiences and the map position that were showing before destroying the activity,
    // and then paint it all like it was before
    private void restoreActivityState(final @NonNull Bundle savedInstanceState) {

        Log.d("ExperiencesActivity","Restoring activity state...");

        List<Experience> experienceList = (List) savedInstanceState.getSerializable(SAVED_STATE_EXPERIENCE_LIST_KEY);
        double lat = savedInstanceState.getDouble(SAVED_STATE_MAP_LATITUDE_KEY, INITIAL_MAP_LATITUDE);
        double lon = savedInstanceState.getDouble(SAVED_STATE_MAP_LONGITUDE_KEY, INITIAL_MAP_LONGITUDE);
        float zoom = savedInstanceState.getFloat(SAVED_STATE_MAP_ZOOM_KEY, INITIAL_MAP_ZOOM);

        currentExperiences = Experiences.buildExperiencesFromList(experienceList);
        map.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), zoom) );

        if (experienceList.size() == 0)
            return;

        addExperienceMarkersToMap(currentExperiences);
        experienceListFragment.setExperiencesAndUpdateFragmentUI(currentExperiences);
    }


    // Options menu for this activity (including the search bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.experiences_activity_options_menu, menu);

        // Configure the search view
        MenuItem searchItem = menu.findItem(R.id.experiences_activity_menu_item_search);
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

            case ID_EXPERIENCES_LOADER:

                // Default selection (no filter)
                String selection = null;
                String[] selectionArgs = null;

                // Get the necessary arguments to filter the query (if they were provided)
                if (args != null &&
                        args.getString(EXPERIENCES_LOADER_SELECTION_KEY,null) != null &&
                        args.getStringArray(EXPERIENCES_LOADER_SELECTION_ARGS_KEY) != null)
                {
                    selection = args.getString(EXPERIENCES_LOADER_SELECTION_KEY,null);
                    selectionArgs = args.getStringArray(EXPERIENCES_LOADER_SELECTION_ARGS_KEY);
                }

                cursorLoader = new CursorLoader(
                        this,                          // Loader's context
                        EXPERIENCES_URI,             // Uri to get the data from a resolver
                        ALL_COLUMNS_EXPERIENCE,     // Fields to retrieve
                        selection,                     // where
                        selectionArgs,                 // where arguments
                        null                           // order by
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

            case ID_EXPERIENCES_LOADER:

                // Create a new Experiences object from the returned data, keep a reference to it
                // (in case we need to restore the activity), and update the UI with it
                currentExperiences = Experiences.buildExperiencesFromCursor(data);
                if (currentExperiences.size() > 0) {
                    String msgHead = getString(R.string.activity_experiences_showing_results_head);
                    String msgTail = getString(R.string.activity_experiences_showing_results_tail);
                    Utils.showMessage(this, msgHead +" "+ currentExperiences.size() +" "+ msgTail, TOAST, null);

                    experienceListFragment.setExperiencesAndUpdateFragmentUI(currentExperiences);
                    addExperienceMarkersToMap(currentExperiences);
                }
                else {
                    String msg = getString(R.string.activity_experiences_showing_results_none);
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
        map.setInfoWindowAdapter(new ExperienceInfoWindowAdapter(this));

        // Define a listener to take action when the user clicks on the info window of a marker
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Experience experience = (Experience) marker.getTag();
                Navigator.navigateFromExperiencesActivityToExperienceDetailActivity(ExperiencesActivity.this, experience);
            }
        });

        // Center the map to its initial position and zoom
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target( new LatLng(INITIAL_MAP_LATITUDE, INITIAL_MAP_LONGITUDE) )
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

                        ActivityCompat.requestPermissions(ExperiencesActivity.this,
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


    // Add markers to the map, corresponding to a given Experiences object
    private void addExperienceMarkersToMap(@NonNull Experiences experiences) {

        if (map == null || experiences == null)
            return;

        List<Experience> experienceList = experiences.allElements();
        currentMapMarkers = new ArrayList<>();

        for (Experience experience: experienceList) {

            LatLng experienceLocation = new LatLng(experience.getLatitude(),experience.getLongitude());

            // Adding the title here, allows to show it in the default info window of the marked
            // (in case no custom info window is configured)
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(experienceLocation)
                    .title(experience.getName());

            // Add to the map a new marker, and store a reference to the Experience in the marker
            // (it will be used when showing custom info windows)
            Marker newMarker = map.addMarker(markerOptions);
            newMarker.setTag(experience);

            // Keep a reference to the added marker (in case we want to clean the map later)
            currentMapMarkers.add(newMarker);
        }
    }


    // Removes all existing markers from the map, and empties the list
    private void clearPreviousSearchResults() {

        if (currentMapMarkers != null) {
            for (Marker marker : currentMapMarkers)
                marker.remove();
        }

        currentExperiences = Experiences.buildExperiencesEmpty();
        experienceListFragment.setExperiencesAndUpdateFragmentUI(currentExperiences);
    }


    // Sets the hint text and the behavior when the user types on the search box,
    // when he clicks the search button, and when he closes the search view.
    private void setupSearchView() {

        if (searchView == null)
            return;

        searchView.setQueryHint( getString(R.string.activity_experiences_menu_search_title) );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {

                clearPreviousSearchResults();   // Clear map and empty the list
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                removeFocusFromSearchView();
                queryExperiencesByString(query);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {

                queryExperiencesByString("");   // Start a new query without any text filter
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


    // Start a new query filtering by all experiences that contain a given string
    // in any of these fields: name, description (EN/ES) and address.
    // (if it is the empty string, then no filter will apply)
    private void queryExperiencesByString(@NonNull String queryString) {

        // Default filter arguments (no filter)
        Bundle args = null;

        // If there is a string to filter, build appropriate arguments for the loader
        if ( queryString != null && !queryString.isEmpty() ) {

            String selection =
                    KEY_EXPERIENCE_NAME +" LIKE ? OR "+
                    KEY_EXPERIENCE_DESCRIPTION_EN +" LIKE ? OR "+
                    KEY_EXPERIENCE_DESCRIPTION_ES +" LIKE ? OR "+
                    KEY_EXPERIENCE_ADDRESS + " LIKE ?";

            String[] selectionArgs = new String[] {
                    "%"+ queryString +"%",
                    "%"+ queryString +"%",
                    "%"+ queryString +"%",
                    "%"+ queryString +"%"
            };

            args = new Bundle();
            args.putString(EXPERIENCES_LOADER_SELECTION_KEY, selection);
            args.putStringArray(EXPERIENCES_LOADER_SELECTION_ARGS_KEY, selectionArgs);
        }

        // Restart the loader with the new arguments
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(ID_EXPERIENCES_LOADER, args, this);
    }
}
