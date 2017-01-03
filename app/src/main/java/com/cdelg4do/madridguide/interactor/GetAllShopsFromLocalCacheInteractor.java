package com.cdelg4do.madridguide.interactor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;

import java.util.List;

/**
 * This class is an interactor in charge of:
 *
 * - First (in background): fetches all shops from the local caché (database) and builds a Shops object with them.
 *
 * - Second (in the main thread): pass the Shops object to the received OnGetAllShopsFromLocalCacheInteractorCompletion listener.
 */
public class GetAllShopsFromLocalCacheInteractor {

    // This interface describes the behavior of a listener waiting for the completion of the background operation
    public interface OnGetAllShopsFromLocalCacheInteractorCompletion {

        void completion(Shops shops);
    }

    // This auxiliary static class simplifies the task of executing a Runnable on foreground
    private static class MainThread {

        // Gets a handler of the main thread, then run the received Runnable
        public static void run(final Runnable runnable) {

            new Handler( Looper.getMainLooper() ).post(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            });
        }
    }

    // Executes the interactor's operations in a given context and with a given listener for the completion
    public void execute(final Context context, final OnGetAllShopsFromLocalCacheInteractorCompletion listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Shop> shopList = new ShopDAO(context).query();
                final Shops shops = Shops.buildShopsFromList(shopList);

                MainThread.run(new Runnable() {
                    @Override
                    public void run() {
                        listener.completion(shops);
                    }
                });
            }
        }).start();
    }
}
