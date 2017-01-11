package com.cdelg4do.madridguide.interactor;


import android.content.Context;
import android.support.annotation.NonNull;

import com.cdelg4do.madridguide.manager.image.ImageCacheManager;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.cdelg4do.madridguide.interactor.CacheAllImagesInteractor.Status.ERROR;
import static com.cdelg4do.madridguide.interactor.CacheAllImagesInteractor.Status.PENDING;
import static com.cdelg4do.madridguide.interactor.CacheAllImagesInteractor.Status.SUCCESS;


/**
 * This class is an interactor in charge of:
 *
 * - First (in background): download and locally store (in Picasso's disk cache) all images from a given model object.
 *
 * - Second (in the UI thread): tells the received CacheAllImagesInteractorListener how many errors happened.
 */
public class CacheAllImagesInteractor {

    // Possible status for a url request
    enum Status {
        PENDING,
        SUCCESS,
        ERROR
    }

    // This interface describes the behavior of a listener waiting for the completion of the operation
    public interface CacheAllImagesInteractorListener {

        void onCacheAllImagesCompletion(int errors);
    }

    /**
     * Stores all the shop images on the local cache.
     * First queues the download of all the images (parallel caching), then waits for all to finish.
     *
     * @param context   context for the operation.
     * @param shops     object that contains the data corresponding to the shop images.
     * @param listener  listener that will be called once the whole caching process is over.
     */
    public void execute(final Context context, final @NonNull Shops shops, final @NonNull CacheAllImagesInteractorListener listener) {

        if (listener == null)
            return;

        if (shops != null) {

            // Build a Map to keep track of each url status (in the beginning, all pending)
            Map<String,Status> urlMap = new LinkedHashMap<>();

            for (Shop shop : shops.allShops()) {
                urlMap.put(shop.getLogoImgUrl(), PENDING);
                urlMap.put(shop.getImageUrl(), PENDING);
                urlMap.put(shop.getMapUrl(), PENDING);
            }

            // Start caching urls in the map, and wait for all to finish
            asyncDownloadOfUrlMap(context, urlMap, listener);
        }
    }


    // Auxiliary methods:

    private void asyncDownloadOfUrlMap(final Context context, final @NonNull Map<String,Status> urlMap, final @NonNull CacheAllImagesInteractorListener listener) {

        if (urlMap == null)
            return;

        // For each url: try to download & cache it, then check if there are more pending.
        // If not, return control to the listener.
        for (final Map.Entry<String,Status> entry: urlMap.entrySet()) {

            ImageCacheManager imageMgr = ImageCacheManager.getInstance(context);
            String imageUrl = entry.getKey();

            // No need to mess with main/background threads here, the ImageCacheManager does it all
            imageMgr.forceCacheOfRemoteImage(imageUrl, new ImageCacheManager.ImageCacheProcessListener() {

                @Override
                public void onImageCachingSuccess() {
                    entry.setValue(SUCCESS);

                    if ( noMorePendingDownloads(urlMap) )
                        listener.onCacheAllImagesCompletion( errorCount(urlMap) );
                }

                @Override
                public void onImageCachingError() {
                    entry.setValue(ERROR);

                    if ( noMorePendingDownloads(urlMap) )
                        listener.onCacheAllImagesCompletion( errorCount(urlMap) );
                }
            });
        }
    }

    private boolean noMorePendingDownloads(Map<String,Status> urlMap) {

        int pending = 0;

        for (final Map.Entry<String,Status> entry: urlMap.entrySet()) {

            if (entry.getValue() == PENDING) {
                pending++;
                break;
            }
        }

        return pending == 0;
    }

    private int errorCount(Map<String,Status> urlMap) {

        int errors = 0;

        for (final Map.Entry<String,Status> entry: urlMap.entrySet()) {

            if (entry.getValue() == ERROR) {
                errors++;
            }
        }

        return errors;
    }


}
