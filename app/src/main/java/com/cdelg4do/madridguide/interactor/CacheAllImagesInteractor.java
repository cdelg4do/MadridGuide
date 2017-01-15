package com.cdelg4do.madridguide.interactor;


import android.content.Context;
import android.support.annotation.NonNull;

import com.cdelg4do.madridguide.manager.image.ImageCacheManager;
import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Experiences;
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

    // Possible status for an image request
    enum Status {
        PENDING,
        SUCCESS,
        ERROR
    }

    // This interface describes the behavior of a listener waiting for the completion of the operation
    public interface CacheAllImagesInteractorListener {

        void onCacheAllImagesCompletion(int errors);
    }

    // The queue of remote images to be cached
    private Map<String,Status> jobQueue;

    /**
     * Stores all the shop images on the local cache.
     * First queues the download of all the images (parallel caching), then waits for all to finish.
     *
     * @param context   context for the operation.
     * @param shops     object that contains the data corresponding to the shop images.
     * @param listener  listener that will be called once the whole caching process is over.
     */
    public void execute(final Context context,
                        final @NonNull Shops shops,
                        final @NonNull Experiences experiences,
                        final @NonNull CacheAllImagesInteractorListener listener) {

        if (listener == null)
            return;

        // Populate the queue with all images that must be downloaded (initially, all jobs are pending)
        jobQueue = new LinkedHashMap<>();

        if (shops != null) {

            for (Shop shop : shops.allElements()) {
                jobQueue.put(shop.getLogoImgUrl(), PENDING);
                jobQueue.put(shop.getImageUrl(), PENDING);
                jobQueue.put(shop.getMapUrl(), PENDING);
            }
        }

        if (experiences != null) {

            for (Experience experience : experiences.allElements()) {
                jobQueue.put(experience.getLogoImgUrl(), PENDING);
                jobQueue.put(experience.getImageUrl(), PENDING);
                jobQueue.put(experience.getMapUrl(), PENDING);
            }
        }

        // Start asynchronously caching images from the queue, and wait for all to finish
        processJobQueue(context, listener);
    }


    // Auxiliary methods:

    private void processJobQueue(final Context context, final @NonNull CacheAllImagesInteractorListener listener) {

        if (jobQueue == null || jobQueue.size() == 0) {

            listener.onCacheAllImagesCompletion(0);
            return;
        }

        ImageCacheManager imageMgr = ImageCacheManager.getInstance(context);

        // Once a job finishes, check if it was the last one. If so, return control to the listener
        for (final Map.Entry<String,Status> job: jobQueue.entrySet()) {

            String imageUrl = job.getKey();

            imageMgr.forceCacheOfRemoteImage(imageUrl, new ImageCacheManager.ImageCacheProcessListener() {

                @Override
                public void onImageCachingSuccess() {
                    job.setValue(SUCCESS);

                    if ( allJobsFinished() )
                        listener.onCacheAllImagesCompletion( jobErrorCount() );
                }

                @Override
                public void onImageCachingError() {
                    job.setValue(ERROR);

                    if ( allJobsFinished() )
                        listener.onCacheAllImagesCompletion( jobErrorCount() );
                }
            });
        }
    }


    private boolean allJobsFinished() {

        boolean allFinished = true;
        for (final Map.Entry<String,Status> job: jobQueue.entrySet()) {

            if (job.getValue() == PENDING) {
                allFinished = false;
                break;
            }
        }

        return allFinished;
    }


    private int jobErrorCount() {

        int errors = 0;
        for (final Map.Entry<String,Status> job: jobQueue.entrySet()) {

            if (job.getValue() == ERROR) {
                errors++;
            }
        }

        return errors;
    }
}
