package com.cdelg4do.madridguide.interactor;


import android.content.Context;
import android.util.Log;

import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.util.MainThread;

import java.util.concurrent.TimeUnit;

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

        void onDeleteLocalCacheFinished();
    }

    /**
     * Stores the shops data on the local cache (database).
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
                try {   TimeUnit.SECONDS.sleep(2);  }
                catch(InterruptedException e) {}

                Log.d("DeleteLocalCache","Deleting Local cache...");
                ShopDAO shopDAO = new ShopDAO(context);

                final int shopCount = shopDAO.deleteAll();
                Log.d("DeleteLocalCache","Deleted "+ shopCount +" shop(s)");

                MainThread.run(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDeleteLocalCacheFinished();
                    }
                });
            }

        }).start();
    }

}
