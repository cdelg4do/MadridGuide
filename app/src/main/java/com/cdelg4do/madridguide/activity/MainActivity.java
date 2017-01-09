package com.cdelg4do.madridguide.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.interactor.CacheAllImagesInteractor;
import com.cdelg4do.madridguide.interactor.CacheAllShopsInteractor;
import com.cdelg4do.madridguide.interactor.DeleteLocalCacheInteractor;
import com.cdelg4do.madridguide.interactor.DownloadAllShopsInteractor;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.navigator.Navigator;
import com.cdelg4do.madridguide.util.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cdelg4do.madridguide.util.Constants.CACHE_MAX_DAYS_LIMIT;
import static com.cdelg4do.madridguide.util.Constants.PREFS_CACHE_DATE_KEY;
import static com.cdelg4do.madridguide.util.Utils.MessageType.DIALOG;

/**
 * This class represents the initial screen.
 */
public class MainActivity extends AppCompatActivity {

    // Reference to UI elements (bind with Butterknife)
    @BindView(R.id.activity_main_btn_shops) Button btnShops;
    @BindView(R.id.activity_main_btn_clear) Button btnClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind all views of this activity (with Butterknife)
        ButterKnife.bind(this);

        // Initial settings for the shops button (set listener & disable it)
        setupShopsButton();

        // Check if the local data are too old (or do not exist)
        if (isCacheOlderThan(CACHE_MAX_DAYS_LIMIT)) {
            discardLocalDataAndRefreshFromServer();
        }
        else {
            finishCacheSetup(false);
        }
    }


    // Auxiliary methods:

    private void setupShopsButton() {

        btnShops.setEnabled(false);

        btnShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigator.navigateFromMainActivityToShopsActivity(MainActivity.this);
            }
        });



        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                discardLocalDataAndRefreshFromServer();
            }
        });

    }

    private boolean isCacheOlderThan(int maxDays) {

        Date currentDate = new Date();
        long maxMiliseconds = 1000 * 60 * 60 * 24 * (long)maxDays;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Date cacheDate = new Date( prefs.getLong(PREFS_CACHE_DATE_KEY,0) );

        return (currentDate.getTime() - cacheDate.getTime() > maxMiliseconds);
    }

    // Starts a sequence of interactors (unless one of them fails) to perform
    // operations in the following order:
    //
    // 1- This method:          Clean local cache (database + images)
    // 2- getShopsFromServer(): Download and parse Shop data from the server
    // 3- persistShopsData():   Store the shop data in the local cache (database)
    // 4- cacheAllShopImages(): Download and locally cache the images of all shops.
    // 5- finishCacheSetup():   Finish all the settings to start working
    private void discardLocalDataAndRefreshFromServer() {

        final ProgressDialog pDialog = Utils.newProgressDialog(this,getString(R.string.deleteLocalCache_progressMsg));
        pDialog.show();

        // Use a DeleteLocalCacheInteractor to delete the local cache,
        // then try to download data from all shops.
        new DeleteLocalCacheInteractor().execute(MainActivity.this,
                new DeleteLocalCacheInteractor.DeleteLocalCacheInteractorListener() {

                    @Override
                    public void onDeleteLocalCacheFinished() {
                        //pDialog.dismiss();
                        getShopsFromServer(pDialog);
                    }
                }
        );
    }

    private void getShopsFromServer(final ProgressDialog pDialog) {

        //final ProgressDialog pDialog = Utils.newProgressDialog(this,getString(R.string.downloadShops_progressMsg));
        //pDialog.show();
        pDialog.setMessage(getString(R.string.downloadShops_progressMsg));

        // Use a DownloadAllShopsInteractor to download data from all shops,
        // then try to store the data into the local cache.
        new DownloadAllShopsInteractor().execute(MainActivity.this,
                new DownloadAllShopsInteractor.DownloadAllShopsInteractorListener() {

                    @Override
                    public void onGetAllShopsFailed(Exception e) {

                        pDialog.dismiss();
                        Log.e("MainActivity","Failed to download the shops info: "+ e.toString() );

                        String title = getString(R.string.error);
                        String msg = getString(R.string.downloadShops_errorMsg);
                        Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                    }

                    @Override
                    public void onGetAllShopsFinished(Shops shops) {
                        Log.d("MainActivity","Successfully downloaded info for "+ shops.size() +" shop(s)");

                        persistShopsData(shops,pDialog);
                    }
                }
        );
    }

    private void persistShopsData(final Shops shops, final ProgressDialog pDialog) {

        // Use a CacheAllShopsInteractor to store the received data in the local cache
        new CacheAllShopsInteractor().execute(MainActivity.this, shops,
                new CacheAllShopsInteractor.CacheAllShopsInteractorListener() {

                    @Override
                    public void onCacheAllShopsFinished(boolean success) {

                        if (!success) {
                            pDialog.dismiss();
                            Log.e("MainActivity","Unable to store the downloaded shops into the local cache");

                            String title = getString(R.string.error);
                            String msg = getString(R.string.cacheShops_errorMsg);
                            Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                            return;
                        }

                        cacheAllShopImages(shops,pDialog);
                    }
                }
        );
    }

    private void cacheAllShopImages(final Shops shops, final ProgressDialog pDialog) {

        //final ProgressDialog pDialog = Utils.newProgressDialog(this,getString(R.string.downloadShopImages_progressMsg));
        //pDialog.show();
        pDialog.setMessage(getString(R.string.downloadShopImages_progressMsg));

        // Use a CacheAllImagesInteractor to download and cache the pictures of all shops
        new CacheAllImagesInteractor().execute(MainActivity.this, shops,
                new CacheAllImagesInteractor.CacheAllImagesInteractorListener() {

                    @Override
                    public void onCacheAllImagesFinished(int errors) {
                        pDialog.dismiss();

                        if ( errors > 0 ) {
                            Log.e("MainActivity", errors +" Shop images could not be stored on local cache");

                            String title = getString(R.string.error);
                            String msg = getString(R.string.downloadShopImages_errorMsg) + errors;
                            Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                            //return;
                        }

                        finishCacheSetup(true);
                    }
                }
        );
    }

    private void finishCacheSetup(boolean dataWereRefreshed) {

        // If the local data were just refreshed, then replace the last-refresh-date with current date.
        if (dataWereRefreshed) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .putLong(PREFS_CACHE_DATE_KEY, new Date().getTime())
                    .apply();
        }

        // Finally, the user can click on the shops button
        btnShops.setEnabled(true);
    }

}
