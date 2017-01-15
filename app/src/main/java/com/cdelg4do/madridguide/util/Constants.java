package com.cdelg4do.madridguide.util;

import com.cdelg4do.madridguide.MadridGuideApp;
import com.cdelg4do.madridguide.R;

/**
 * This class contains static constants not shown to the final user (for logging, debug, etc).
 */
public class Constants {
    public static final String appName = "com.cdelg4do.madridguide";

    public static final String GOOGLE_MAPS_API_KEY = MadridGuideApp.getAppContext().getString(R.string.google_maps_api_key);

    public static final String LANG_ENGLISH = "en";
    public static final String LANG_SPANISH = "es";
    public static final String LANG_DEFAULT = LANG_ENGLISH;

    public static final int IMAGE_MANAGER_DISK_CACHE_SIZE_MB = 85;

    public static final String INTENT_KEY_DETAIL_SHOP = appName + ".INTENT_KEY_DETAIL_SHOP";
    public static final String INTENT_KEY_DETAIL_EXPERIENCE = appName + ".INTENT_KEY_DETAIL_EXPERIENCE";

    public static final String PREFS_CACHE_DATE_KEY = "CACHE_DATE";
    public static final int CACHE_MAX_DAYS_LIMIT = 7;

    public static final float INITIAL_MAP_LATITUDE = (float) 40.41665363547475;
    public static final float INITIAL_MAP_LONGITUDE = (float) -3.7038104608654976;
    public static final int INITIAL_MAP_ZOOM = 12;

    public static final int DETAIL_MAP_ZOOM = 17;
    public static final int DETAIL_MAP_WIDTH = 640;
    public static final int DETAIL_MAP_HEIGHT = 480;
    public static final int DETAIL_MAP_SCALE = 2;
    public static final String DETAIL_MAP_TYPE = "roadmap";
    public static final String DETAIL_MAP_MARKER_COLOR = "0xF24B37";

    public static final int DEFAULT_SHOP_LOGO_ID = R.drawable.default_placeholder;
    public static final int ERROR_SHOP_LOGO_ID = R.drawable.default_error_placeholder;
    public static final int DEFAULT_SHOP_IMAGE_ID = R.drawable.empty_background;
    public static final int ERROR_SHOP_IMAGE_ID = R.drawable.empty_background;
}
