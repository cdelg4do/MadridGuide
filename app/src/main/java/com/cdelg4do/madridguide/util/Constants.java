package com.cdelg4do.madridguide.util;

/**
 * This class contains static constants not shown to the final user (for logging, debug, etc).
 */
public class Constants {
    public static final String appName = "com.cdelg4do.madridguide";

    public static final String INTENT_KEY_DETAIL_SHOP = appName + ".INTENT_KEY_DETAIL_SHOP";

    public static final String PREFS_CACHE_DATE_KEY = "CACHE_DATE";
    public static final int CACHE_MAX_DAYS_LIMIT = 7;

    public static final float INITIAL_MAP_LATITUDE = (float) 40.41665363547475;
    public static final float INITIAL_MAP_LONGITUDE = (float) -3.7038104608654976;
    public static final int INITIAL_MAP_ZOOM = 12;

    public static final int DEFAULT_SHOP_LOGO_ID = android.R.drawable.ic_menu_camera;
}
