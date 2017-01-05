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
    @BindView(R.id.activity_main_btn_shops)
    Button btnShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind all views of this activity (with Butterknife)
        ButterKnife.bind(this);

        // Initial settings for the shops button (set listener & disable it)
        setupShopsButton();

        // If the local cache is too old (or does not exist)
        // then clean it and download the most recent data from server
        if (isCacheOlderThan(CACHE_MAX_DAYS_LIMIT)) {
            updateLocalCacheFromServer();
        }
        else {
            btnShops.setEnabled(true);
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
    }

    private boolean isCacheOlderThan(int maxDays) {

        Date currentDate = new Date();
        long maxMiliseconds = 1000 * 60 * 60 * 24 * (long)maxDays;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Date cacheDate = new Date( prefs.getLong(PREFS_CACHE_DATE_KEY,0) );

        return (currentDate.getTime() - cacheDate.getTime() > maxMiliseconds);
    }

    private void updateLocalCacheFromServer() {

        // Progress dialog
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setTitle( getString(R.string.defaultProgressTitle) );
        pDialog.setMessage( getString(R.string.deleteLocalCache_progressMsg) );
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        // Use a DeleteLocalCacheInteractor to delete the local cache,
        // then try to download data from all shops.
        new DeleteLocalCacheInteractor().execute(MainActivity.this,
                new DeleteLocalCacheInteractor.DeleteLocalCacheInteractorListener() {

                    @Override
                    public void onDeleteLocalCacheFinished() {
                        pDialog.dismiss();
                        cacheShopsFromServer();
                    }
                }
        );
    }

    private void cacheShopsFromServer() {

        // Progress dialog
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setTitle( getString(R.string.defaultProgressTitle) );
        pDialog.setMessage( getString(R.string.downloadShops_progressMsg) );
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        // Use a DownloadAllShopsInteractor to download data from all shops,
        // then try to store the data into the local cache.
        new DownloadAllShopsInteractor().execute(MainActivity.this,
                new DownloadAllShopsInteractor.GetAllShopsInteractorListener() {

                    @Override
                    public void onGetAllShopsFinished(Shops shops) {
                        populateShopsCache(shops,pDialog);
                    }

                    @Override
                    public void onGetAllShopsFailed(Exception e) {

                        pDialog.dismiss();
                        Log.d("MainActivity","onGetAllShopsFailed: "+ e.toString() );

                        String title = getString(R.string.error);
                        String msg = getString(R.string.downloadShops_errorMsg);
                        Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                    }
                }
        );
    }

    private void populateShopsCache(final Shops shops, final ProgressDialog pDialog) {

        // Use a CacheAllShopsInteractor to store the received data in the local cache
        new CacheAllShopsInteractor().execute(MainActivity.this, shops,
                new CacheAllShopsInteractor.CacheAllShopsInteractorListener() {

                    @Override
                    public void onCacheAllShopsFinished(boolean success) {

                        pDialog.dismiss();

                        if (success) {
                            saveCacheDate();
                            btnShops.setEnabled(true);
                        }

                        else {
                            String title = getString(R.string.error);
                            String msg = getString(R.string.cacheShops_errorMsg);
                            Utils.showMessage(MainActivity.this, msg, DIALOG, title);
                        }

                    }
                }
        );
    }

    private void saveCacheDate() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefs.edit()
                .putLong(PREFS_CACHE_DATE_KEY, new Date().getTime())
                .apply();
    }

}
