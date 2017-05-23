package com.cdelg4do.madridguide.manager.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.cdelg4do.madridguide.util.Constants.IMAGE_MANAGER_DISK_CACHE_SIZE_MB;
import static com.squareup.picasso.NetworkPolicy.OFFLINE;


/**
 * This class manages all the tasks related to downloading, caching and showing remote images.
 */
public class ImageCacheManager {

    private static final String CACHE_DIR_NAME = "picasso-cache";

    private static ImageCacheManager sharedInstance;    // ImageCacheManager is a singleton

    public interface ImageCacheProcessListener {
        void onImageCachingSuccess();
        void onImageCachingError();
    }

    private WeakReference<Context> context;
    private LruCache picassoCache;
    private Downloader picassoDownloader;


    // The constructor is private, call getInstance to get a reference to the singleton
    private ImageCacheManager(Context context) {

        this.context = new WeakReference<Context>(context);

        // Setup a custom memory cache for Picasso, so that we can keep a reference to it
        // (to be able to clean it later if necessary)
        picassoCache = new LruCache(context);

        // Setup a custom downloader for Picasso, so that we can define an appropriate disk cache size
        // (Picasso does not manage disk cache directly, lets the downloader to do it)
        long diskCacheSizeBytes = IMAGE_MANAGER_DISK_CACHE_SIZE_MB * 1024 * 1024;
        picassoDownloader = new OkHttp3Downloader(context, diskCacheSizeBytes);

        // Configure a custom instance of Picasso, and set it as the default Picasso instance
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.memoryCache(picassoCache);
        builder.downloader(picassoDownloader);
        Picasso.setSingletonInstance(builder.build());

        // Picasso logging options
        Picasso.with(context).setLoggingEnabled(false);
        Picasso.with(context).setIndicatorsEnabled(false);
    }

    /**
     * Returns a reference to the manager.
     *
     * @param context   a context for the operations.
     * @return          always an initialized ImageCacheManager object.
     */
    public static synchronized ImageCacheManager getInstance(Context context) {

        if (sharedInstance == null)
            sharedInstance = new ImageCacheManager(context.getApplicationContext());

        return sharedInstance;
    }

    /**
     * Removes both the memory and the disk image caches
     */
    public void clearCache() {

        picassoCache.clear();

        File cacheDir = new File(context.get().getCacheDir(), CACHE_DIR_NAME);
        if (cacheDir.exists() && cacheDir.isDirectory())
        {
            for (String aFile : cacheDir.list())
                new File(cacheDir, aFile).delete();
        }
    }

    /**
     * Forces the download of a remote image (bypass the local caches) and stores it in the cache
     *
     * @param imageUrl  url of the remote image.
     * @param listener  the object which is waiting to take action when the operation finishes
     */
    public void forceCacheOfRemoteImage(final @NonNull String imageUrl, final @NonNull ImageCacheProcessListener listener) {

        if (listener == null)
            return;

        if (imageUrl == null) {
            listener.onImageCachingError();
            return;
        }


        Log.d("ImageCacheManager", "Requesting remote image (" + imageUrl +")");

        Picasso.with(context.get())
                .load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE)     // Do not use the memory cache here
                .networkPolicy(NetworkPolicy.NO_CACHE)   // Do not look for the image in the disk
                .fetch(new Callback() {

                    @Override
                    public void onSuccess() {
                        Log.d("ImageCacheManager","Succesfully downloaded remote image ("+ imageUrl +")");
                        listener.onImageCachingSuccess();
                    }

                    @Override
                    public void onError() {
                        Log.e("ImageCacheManager","Failed to download "+ imageUrl);
                        listener.onImageCachingError();
                    }
                });
    }

    /**
     * Asynchronously loads a previously cached image into an ImageView, then calls a listener.
     * NOTE: if the requested image is not cached, it will NOT look for it on the Internet.
     *
     * @param target        the ImageView to load the image on.
     * @param imageUrl      the url of the image to be shown.
     * @param placeholderId id of the resource image used as placeholder during the operation.
     * @param brokenImageId id of the resource image to show in case the operation fails.
     * @param listener      object waiting for the operation to complete.
     */
    public void loadCachedImage(final @NonNull ImageView target, final @NonNull String imageUrl, final int placeholderId, final int brokenImageId, final Callback listener) {

        // Try to get the image only from the local cache, not from the network
        Picasso.with(context.get())
                .load(imageUrl)
                .placeholder(placeholderId)
                .networkPolicy(OFFLINE)     // Do not look for the image on the Internet
                .error(brokenImageId)
                .into(target, new Callback() {

                    @Override
                    public void onSuccess() {
                        Log.d("ImageCacheManager", "Image retrieved from local cache (" + imageUrl + ")");

                        if (listener != null)
                            listener.onSuccess();
                    }

                    @Override
                    public void onError() {

                        Log.e("ImageCacheManager", "Failed to retrieve image from local cache (" + imageUrl + ")");

                        if (listener != null)
                            listener.onError();
                    }
                });
    }

    /**
     * Asynchronously loads a previously cached image into an ImageView (does not have any callback).
     * NOTE: if the requested image is not cached, it will NOT look for it remotely.
     *
     * @param target        the ImageView to load the image on.
     * @param imageUrl      the url of the image to be shown.
     * @param placeholderId id of the resource image used as placeholder during the operation.
     * @param brokenImageId id of the resource image to show in case the operation fails.
     */
    public void loadCachedImage(final @NonNull ImageView target, final @NonNull String imageUrl, final int placeholderId, final int brokenImageId) {

        loadCachedImage(target, imageUrl, placeholderId, brokenImageId, null);
    }
}

