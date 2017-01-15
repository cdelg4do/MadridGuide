package com.cdelg4do.madridguide.interactor;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cdelg4do.madridguide.manager.db.ExperienceDAO;
import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.manager.image.ImageCacheManager;
import com.cdelg4do.madridguide.util.MainThread;

import java.util.concurrent.TimeUnit;

import static com.cdelg4do.madridguide.util.Constants.PREFS_CACHE_DATE_KEY;

/**
 * This class is an interactor in charge of:
 *
 * - First (in background): deleting the local cache.
 *
 * - Second (in the main thread): return control to the received DeleteLocalCacheInteractorListener.
 */
public class DeleteLocalCacheInteractor {

    // This interface describes the behavior of a listener waiting for the completion of the async operation
    public interface DeleteLocalCacheInteractorListener {

        void onDeleteLocalCacheCompletion();
    }

    /**
     * Deletes all locally cached data (shops, experiences & images).
     *
     * @param context   context for the operation.
     * @param listener  listener that will process the result of the operation.
     */
    public void execute(final Context context, final DeleteLocalCacheInteractorListener listener) {

        if (listener == null)
            return;

        new Thread(new Runnable() {

            @Override
            public void run() {

                // Just a little delay to give the progress dialog time to show
                Log.d("DeleteLocalCache","Deleting all locally cached data...");
                try { TimeUnit.SECONDS.sleep(2); } catch(InterruptedException e) {}

                removeLastRefreshDate(context);
                Log.d("DeleteLocalCache","Deleted last refresh date from preferences");

                ShopDAO shopDAO = new ShopDAO(context);
                int deleteCount = shopDAO.deleteAll();
                Log.d("DeleteLocalCache","Deleted "+ deleteCount +" shop(s)");

                ExperienceDAO experienceDAO = new ExperienceDAO(context);
                deleteCount = experienceDAO.deleteAll();
                Log.d("DeleteLocalCache","Deleted "+ deleteCount +" experience(s)");

                ImageCacheManager.getInstance(context).clearCache();
                Log.d("DeleteLocalCache","Deleted local images cache");

                MainThread.run(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDeleteLocalCacheCompletion();
                    }
                });
            }

        }).start();
    }


    // Auxiliary methods:

    private void removeLastRefreshDate(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit()
                .remove(PREFS_CACHE_DATE_KEY)
                .apply();
    }

}
