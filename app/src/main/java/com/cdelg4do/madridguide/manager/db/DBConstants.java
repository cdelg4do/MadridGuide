package com.cdelg4do.madridguide.manager.db;

/**
 * This class contains all constants used by the DB Manager.
 */
public class DBConstants {

    // Table names
    public static final String TABLE_SHOP = "SHOP";
    public static final String TABLE_EXPERIENCE = "EXPERIENCE";


    // Table 'shop' field constants
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


    // Table 'experience' field constants
    public static final String KEY_EXPERIENCE_ID = "_id";   // With "_" to bind data from a CursorAdapter

    public static final String KEY_EXPERIENCE_NAME = "name";
    public static final String KEY_EXPERIENCE_IMAGE_URL = "image_url";
    public static final String KEY_EXPERIENCE_LOGO_IMAGE_URL = "logo_image_url";

    public static final String KEY_EXPERIENCE_ADDRESS = "address";
    public static final String KEY_EXPERIENCE_URL = "url";
    public static final String KEY_EXPERIENCE_DESCRIPTION_EN = "description_en";
    public static final String KEY_EXPERIENCE_DESCRIPTION_ES = "description_es";
    public static final String KEY_EXPERIENCE_OPENING_HOURS_EN = "opening_hours_en";
    public static final String KEY_EXPERIENCE_OPENING_HOURS_ES = "opening_hours_es";

    public static final String KEY_EXPERIENCE_LATITUDE = "latitude";
    public static final String KEY_EXPERIENCE_LONGITUDE = "longitude";

    public static final String[] ALL_COLUMNS_EXPERIENCE = {
            KEY_EXPERIENCE_ID,
            KEY_EXPERIENCE_NAME,
            KEY_EXPERIENCE_IMAGE_URL,
            KEY_EXPERIENCE_LOGO_IMAGE_URL,
            KEY_EXPERIENCE_ADDRESS,
            KEY_EXPERIENCE_URL,
            KEY_EXPERIENCE_DESCRIPTION_EN,
            KEY_EXPERIENCE_DESCRIPTION_ES,
            KEY_EXPERIENCE_OPENING_HOURS_EN,
            KEY_EXPERIENCE_OPENING_HOURS_ES,
            KEY_EXPERIENCE_LATITUDE,
            KEY_EXPERIENCE_LONGITUDE
    };

    public static final String SQL_SCRIPT_CREATE_EXPERIENCE_TABLE =
            "create table " + TABLE_EXPERIENCE
                    + " ( "
                    + KEY_EXPERIENCE_ID + " integer primary key autoincrement, "
                    + KEY_EXPERIENCE_NAME + " text not null, "
                    + KEY_EXPERIENCE_IMAGE_URL + " text, "
                    + KEY_EXPERIENCE_LOGO_IMAGE_URL + " text, "
                    + KEY_EXPERIENCE_ADDRESS + " text, "
                    + KEY_EXPERIENCE_URL + " text, "
                    + KEY_EXPERIENCE_LATITUDE + " real, "
                    + KEY_EXPERIENCE_LONGITUDE + " real, "
                    + KEY_EXPERIENCE_DESCRIPTION_EN + " text, "
                    + KEY_EXPERIENCE_DESCRIPTION_ES + " text, "
                    + KEY_EXPERIENCE_OPENING_HOURS_EN + " text, "
                    + KEY_EXPERIENCE_OPENING_HOURS_ES + " text "
                    + ");";


    // General database scripts
    public static final String DROP_DATABASE_SCRIPTS = "";

    public static final String[] CREATE_DATABASE_SCRIPTS = {
            SQL_SCRIPT_CREATE_SHOP_TABLE,
            SQL_SCRIPT_CREATE_EXPERIENCE_TABLE
    };
}
