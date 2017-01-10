package com.cdelg4do.madridguide.manager.db;

/**
 * This class contains all constants used by the DB Manager.
 */
public class DBConstants {

    // Table names
    public static final String TABLE_SHOP = "SHOP";

    // Table field constants
    public static final String KEY_SHOP_ID = "_id";   // With "_" to bind data from a CursorAdapter

    public static final String KEY_SHOP_NAME = "name";
    public static final String KEY_SHOP_IMAGE_URL = "image_url";
    public static final String KEY_SHOP_LOGO_IMAGE_URL = "logo_image_url";

    public static final String KEY_SHOP_ADDRESS = "address";
    public static final String KEY_SHOP_URL = "url";
    public static final String KEY_SHOP_DESCRIPTION_EN = "description_en";
    public static final String KEY_SHOP_DESCRIPTION_ES = "description_es";
    public static final String KEY_SHOP_OPENING_HOURS_EN = "opening_hours_en";
    public static final String KEY_SHOP_OPENING_HOURS_ES = "opening_hours_es";

    public static final String KEY_SHOP_LATITUDE = "latitude";
    public static final String KEY_SHOP_LONGITUDE = "longitude";

    public static final String[] ALL_COLUMNS_SHOP = {
            KEY_SHOP_ID,
            KEY_SHOP_NAME,
            KEY_SHOP_IMAGE_URL,
            KEY_SHOP_LOGO_IMAGE_URL,
            KEY_SHOP_ADDRESS,
            KEY_SHOP_URL,
            KEY_SHOP_DESCRIPTION_EN,
            KEY_SHOP_DESCRIPTION_ES,
            KEY_SHOP_OPENING_HOURS_EN,
            KEY_SHOP_OPENING_HOURS_ES,
            KEY_SHOP_LATITUDE,
            KEY_SHOP_LONGITUDE
    };

    public static final String SQL_SCRIPT_CREATE_SHOP_TABLE =
            "create table " + TABLE_SHOP
                    + " ( "
                    + KEY_SHOP_ID + " integer primary key autoincrement, "  // starting with 1
                    + KEY_SHOP_NAME + " text not null, "
                    + KEY_SHOP_IMAGE_URL + " text, "
                    + KEY_SHOP_LOGO_IMAGE_URL + " text, "
                    + KEY_SHOP_ADDRESS + " text, "
                    + KEY_SHOP_URL + " text, "
                    + KEY_SHOP_LATITUDE + " real, "
                    + KEY_SHOP_LONGITUDE + " real, "
                    + KEY_SHOP_DESCRIPTION_EN + " text, "
                    + KEY_SHOP_DESCRIPTION_ES + " text, "
                    + KEY_SHOP_OPENING_HOURS_EN + " text, "
                    + KEY_SHOP_OPENING_HOURS_ES + " text "
//                    + "FOREIGN KEY(" + <KEY_SHOP_xxxx> + ") REFERENCES " + <TABLE_otherTable> + "(" + <KEY_otherTable_otherField> + ") ON DELETE CASCADE"
                    + ");";

    public static final String DROP_DATABASE_SCRIPTS = "";

    public static final String[] CREATE_DATABASE_SCRIPTS = {
            SQL_SCRIPT_CREATE_SHOP_TABLE
    };
}
