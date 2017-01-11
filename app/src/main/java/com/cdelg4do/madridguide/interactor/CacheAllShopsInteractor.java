package com.cdelg4do.madridguide.interactor;


import android.content.Context;

import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.util.MainThread;

import static com.cdelg4do.madridguide.manager.db.ShopDAO.INVALID_ID;


/**
 * This class is an interactor in charge of:
 *
 * - First (in background): stores in the local cache (database) all shops from a given Shops object.
 *
 * - Second (in the UI thread): tells the received CacheAllShopsInteractorListener if the operation succeeded or not.
 */
public class CacheAllShopsInteractor {

    // This interface describes the behavior of a listener waiting for the completion of the async operation
    public interface CacheAllShopsInteractorListener {

        void onCacheAllShopsCompletion(boolean success);
    }

    /**
     * Stores the shops data on the local cache (database).
     *
     * @param context   context for the operation.
     * @param shops     object containing the data to be stored on the local cache.
     * @param listener  listener that will process the result of the operation.
     */
    public void execute(final Context context, final Shops shops, final CacheAllShopsInteractorListener listener) {

        if (listener == null)
            return;

        new Thread(new Runnable() {

            @Override
            public void run() {

                ShopDAO shopDAO = new ShopDAO(context);
                boolean success = true;

                for (Shop shop: shops.allShops()) {

                    success = ( shopDAO.insert(shop) != INVALID_ID );

                    if (!success)
                        break;
                }

                // We cannot use the variable success inside the MainThread closure
                // (because it cannot be declared as final)
                if (success) {
                    MainThread.run(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCacheAllShopsCompletion(true);
                        }
                    });
                }
                else {
                    MainThread.run(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCacheAllShopsCompletion(false);
                        }
                    });
                }
            }

        }).start();
    }

}
