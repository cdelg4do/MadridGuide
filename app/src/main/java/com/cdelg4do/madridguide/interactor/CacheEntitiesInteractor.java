package com.cdelg4do.madridguide.interactor;


import android.content.Context;

import com.cdelg4do.madridguide.manager.db.ExperienceDAO;
import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Experiences;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.util.MainThread;


/**
 * This class is an interactor in charge of:
 *
 * - First (in background): stores in the local cache (database) all entities from a given aggregate object.
 *
 * - Second (in the UI thread): tells a CacheEntitiesInteractorListener if the operation succeeded or not.
 */
public class CacheEntitiesInteractor {

    // This interface describes the behavior of a listener waiting for the completion of the async operation
    public interface CacheEntitiesInteractorListener {

        void onCacheEntitiesCompletion(boolean success);
    }

    /**
     * Stores the downloaded entities data on the local cache (database).
     *
     * @param context   context for the operation.
     * @param entities  the data to be stored on the local cache (should be casted to appropriate type)
     * @param listener  listener that will process the result of the operation.
     */
    public void execute(final Context context, final Object entities, final CacheEntitiesInteractorListener listener) {

        if (listener == null)
            return;

        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean success = true;

                // If storing a Shops object in the database
                if (entities instanceof Shops) {

                    ShopDAO shopDAO = new ShopDAO(context);

                    for (Shop shop: ((Shops)entities).allElements()) {

                        success = ( shopDAO.insert(shop) != ShopDAO.INVALID_ID );
                        if (!success)
                            break;
                    }
                }

                // If storing an Experiences object in the database
                else if (entities instanceof Experiences) {

                    ExperienceDAO experienceDAO = new ExperienceDAO(context);

                    for (Experience experience: ((Experiences)entities).allElements()) {

                        success = ( experienceDAO.insert(experience) != ExperienceDAO.INVALID_ID );
                        if (!success)
                            break;
                    }
                }

                // If not any of the expected types (or if entities is null)
                else {
                    success = false;
                }


                // Use final variable to let the listener know the result inside the MainThread
                final boolean result = success;

                MainThread.run(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCacheEntitiesCompletion(result);
                    }
                });

            }

        }).start();
    }

}
