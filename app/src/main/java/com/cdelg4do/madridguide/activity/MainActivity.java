package com.cdelg4do.madridguide.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.interactor.CacheEntitiesInteractor;
import com.cdelg4do.madridguide.interactor.CacheAllImagesInteractor;
import com.cdelg4do.madridguide.interactor.DeleteLocalCacheInteractor;
import com.cdelg4do.madridguide.interactor.DownloadEntitiesInteractor;
import com.cdelg4do.madridguide.interactor.DownloadEntitiesInteractor.DownloadEntitiesInteractorListener;
import com.cdelg4do.madridguide.manager.net.NetworkManager;
import com.cdelg4do.madridguide.manager.net.NetworkManager.ConnectionType;
import com.cdelg4do.madridguide.model.Experiences;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.navigator.Navigator;
import com.cdelg4do.madridguide.util.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cdelg4do.madridguide.interactor.DownloadEntitiesInteractor.RemoteSource.ACTIVITIES;
import static com.cdelg4do.madridguide.interactor.DownloadEntitiesInteractor.RemoteSource.SHOPS;
import static com.cdelg4do.madridguide.manager.net.NetworkManager.ConnectionType.NONE;
import static com.cdelg4do.madridguide.manager.net.NetworkManager.ConnectionType.WIFI;
import static com.cdelg4do.madridguide.util.Constants.CACHE_MAX_DAYS_LIMIT;
import static com.cdelg4do.madridguide.util.Constants.PREFS_CACHE_DATE_KEY;
import static com.cdelg4do.madridguide.util.Utils.MessageType.DIALOG;

/**
 * This class represents the initial screen.
 */
public class MainActivity extends AppCompatActivity {

    // Reference to UI elements (bind with Butterknife)
    @BindView(R.id.activity_main_btn_shops) Button btnShops;
    @BindView(R.id.activity_main_btn_experiences) Button btnExperiences;

    // Reference to the entities downloaded after refreshing from the server
    private Shops downloadedShops;
    private Experiences downloadedExperiences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("");

        // Initial settings for the shops button (set listener & disable it)
        setupActivityButtons();

        // Check if the local data are too old (or do not exist)
        if (isLocalDataOlderThan(CACHE_MAX_DAYS_LIMIT)) {

            initiateDataRefreshFromServer();
        }
        else {

            finishActivitySetup(false);
        }
    }

    // Options menu for this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activiy_options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.main_activity_menu_item_refresh:

                initiateDataRefreshFromServer();
                return true;

            case R.id.main_activity_menu_item_about:

                String title = getString(R.string.main_activity_about_title);
                String msg = getString(R.string.main_activity_about_msg);
                Utils.showMessage(this, msg, DIALOG, title);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    // Auxiliary methods:

    // Initial settings for the buttons in this activity (set enabled/disabled and listeners)
    private void setupActivityButtons() {

        // The user cannot click any button until the app has downloaded data from the server
        if ( !deviceHasLocalData() ) {
            btnShops.setEnabled(false);
            btnExperiences.setEnabled(false);
        }

        btnShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigator.navigateFromMainActivityToShopsActivity(MainActivity.this);
            }
        });

        btnExperiences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigator.navigateFromMainActivityToExperiencesActivity(MainActivity.this);
            }
        });
    }

    // If this is the first time the app runs, or if the last data refresh failed, returns false.
    // Otherwise, returns true.
    private boolean deviceHasLocalData() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long cacheDateMs = prefs.getLong(PREFS_CACHE_DATE_KEY, 0);

        if (cacheDateMs>0)
            return true;
        else
            return false;
    }

    // Determines if there is need to refresh the locally stored data
    // (if there is no local data yet, returns true as well)
    private boolean isLocalDataOlderThan(int days) {

        Date currentDate = new Date();
        long maxMiliseconds = 1000 * 60 * 60 * 24 * (long)days;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Date cacheDate = new Date( prefs.getLong(PREFS_CACHE_DATE_KEY,0) );

        return (currentDate.getTime() - cacheDate.getTime() > maxMiliseconds);
    }


    /**
     * This is the first in the call sequence to refresh the locally stored data:
     *
     * 1- This method:              Check the device's connection and start the process (if possible).
     * 2- discardLocalData()        Clean local cache (database + images).
     * 3- getShopsFromServer()      Download and parse Shops data from the server.
     * 4- persistShopsData()        Store the shops data in the local cache (database).
     * 5- getActivitiesFromServer() Download and parse Activities data from the server.
     * 6- persistActivitiesData()   Store the activities data in the local cache (database).
     * 5- cacheAllImages()          Download and locally cache the images of all shops and activities.
     * 6- finishActivitySetup()     Finish all the settings.
     */
    private void initiateDataRefreshFromServer() {

        ConnectionType deviceConnection = NetworkManager.getInternetConnectionType(this);

        if (deviceConnection == NONE) {
            String title = getString(R.string.refreshDialog_noInternetTitle);
            String msg = getString(R.string.refreshDialog_noInternetMsg);

            Utils.showMessage(this, msg, DIALOG, title);
        }

        else if (deviceConnection == WIFI) {
            discardLocalData();
        }

        // On non-wifi connections, ask the user before proceeding
        else {
            String title = getString(R.string.refreshDialog_noWifiTitle);
            String msg = getString(R.string.refreshDialog_noWifiMsg);
            Utils.showCancelAcceptDialog(this, title, msg, null, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    discardLocalData();
                }
            });
        }
    }

    private void discardLocalData() {

        String progressMsg = getString(R.string.discardLocalData_progressMsg);
        final ProgressDialog pDialog = Utils.newProgressDialog(this, progressMsg);
        pDialog.show();

        // Use a DeleteLocalCacheInteractor to delete the local cache,
        // then try to download data from all shops.
        new DeleteLocalCacheInteractor().execute(MainActivity.this,
                new DeleteLocalCacheInteractor.DeleteLocalCacheInteractorListener() {

                    @Override
                    public void onDeleteLocalCacheCompletion() {
                        getShopsFromServer(pDialog);
                    }
                }
        );
    }

    private void getShopsFromServer(final ProgressDialog pDialog) {

        pDialog.setMessage(getString(R.string.downloadShops_progressMsg));

        // Use a DownloadEntitiesInteractor to download data from all shops,
        // then try to store the data into the local cache.
        new DownloadEntitiesInteractor().execute(MainActivity.this, SHOPS,
                new DownloadEntitiesInteractorListener() {

                    @Override
                    public void onDownloadEntitiesFail(Exception e) {

                        pDialog.dismiss();
                        Log.e("MainActivity","Failed to download the shops info: "+ e.toString() );

                        String title = getString(R.string.error);
                        String msg = getString(R.string.downloadShops_errorMsg);
                        Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                    }

                    @Override
                    public void onDownloadEntitiesSuccess(Object downloadedEntities) {

                        downloadedShops = (Shops) downloadedEntities;
                        Log.d("MainActivity","Successfully downloaded info for "+ downloadedShops.size() +" shop(s)");

                        persistShopsData(pDialog);
                    }
                }
        );
    }

    private void persistShopsData(final ProgressDialog pDialog) {

        // Use a CacheEntitiesInteractor to store the received data in the local cache
        new CacheEntitiesInteractor().execute(MainActivity.this, downloadedShops,
                new CacheEntitiesInteractor.CacheEntitiesInteractorListener() {

                    @Override
                    public void onCacheEntitiesCompletion(boolean success) {

                        if (!success) {
                            pDialog.dismiss();
                            Log.e("MainActivity","Unable to store the downloaded shops into the local cache");

                            String title = getString(R.string.error);
                            String msg = getString(R.string.cacheShops_errorMsg);
                            Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                            return;
                        }

                        getActivitiesFromServer(pDialog);
                    }
                }
        );
    }

    private void getActivitiesFromServer(final ProgressDialog pDialog) {

        pDialog.setMessage(getString(R.string.downloadActivities_progressMsg));

        // Use a DownloadEntitiesInteractor to download data from all activities,
        // then try to store the data into the local cache.
        new DownloadEntitiesInteractor().execute(MainActivity.this, ACTIVITIES,
                new DownloadEntitiesInteractor.DownloadEntitiesInteractorListener() {

                    @Override
                    public void onDownloadEntitiesFail(Exception e) {

                        pDialog.dismiss();
                        Log.e("MainActivity","Failed to download the activities info: "+ e.toString() );

                        String title = getString(R.string.error);
                        String msg = getString(R.string.downloadActivities_errorMsg);
                        Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                    }

                    @Override
                    public void onDownloadEntitiesSuccess(Object downloadedEntities) {

                        downloadedExperiences = (Experiences) downloadedEntities;
                        Log.d("MainActivity","Successfully downloaded info for "+ downloadedExperiences.size() +" shop(s)");

                        persistActivitiesData(pDialog);
                    }
                }
        );
    }

    private void persistActivitiesData(final ProgressDialog pDialog) {

        // Use a CacheEntitiesInteractor to store the received data in the local cache
        new CacheEntitiesInteractor().execute(MainActivity.this, downloadedExperiences,
                new CacheEntitiesInteractor.CacheEntitiesInteractorListener() {

                    @Override
                    public void onCacheEntitiesCompletion(boolean success) {

                        if (!success) {
                            pDialog.dismiss();
                            Log.e("MainActivity","Unable to store the downloaded activities into the local cache");

                            String title = getString(R.string.error);
                            String msg = getString(R.string.cacheActivities_errorMsg);
                            Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                            return;
                        }

                        cacheAllImages(pDialog);
                    }
                }
        );
    }

    private void cacheAllImages(final ProgressDialog pDialog) {

        pDialog.setMessage(getString(R.string.cacheImages_progressMsg));

        // Use a CacheAllImagesInteractor to download and cache the pictures of all shops and activities
        new CacheAllImagesInteractor().execute(
                MainActivity.this,
                downloadedShops,
                downloadedExperiences,
                new CacheAllImagesInteractor.CacheAllImagesInteractorListener() {

                    @Override
                    public void onCacheAllImagesCompletion(int errors) {
                        pDialog.dismiss();

                        // If some image failed, show a warning message but continue to the last step anyway
                        if ( errors > 0 ) {
                            Log.e("MainActivity", errors +" image(s) could not be stored on local cache");

                            String title = getString(R.string.warning);
                            String msg = ""+ errors +" "+ getString(R.string.cacheImages_warningMsg);
                            Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                        }

                        finishActivitySetup(true);
                    }
                }
        );
    }

    private void finishActivitySetup(boolean dataWereRefreshed) {

        // If the local data were just refreshed, then replace the last-refresh-date with current date.
        if (dataWereRefreshed) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .putLong(PREFS_CACHE_DATE_KEY, new Date().getTime())
                    .apply();
        }

        // Finally, the user can click on the activity buttons
        btnShops.setEnabled(true);
        btnExperiences.setEnabled(true);
    }

}
