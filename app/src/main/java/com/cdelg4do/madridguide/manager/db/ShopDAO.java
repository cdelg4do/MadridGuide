package com.cdelg4do.madridguide.manager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cdelg4do.madridguide.model.Shop;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import static com.cdelg4do.madridguide.manager.db.DBConstants.ALL_COLUMNS_SHOP;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_ADDRESS;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_DESCRIPTION;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_ID;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_IMAGE_URL;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_LATITUDE;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_LOGO_IMAGE_URL;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_LONGITUDE;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_NAME;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_URL;
import static com.cdelg4do.madridguide.manager.db.DBConstants.TABLE_SHOP;

/**
 * This class manages the operations on the Shop table of the database.
 */
public class ShopDAO implements DAOPersistable<Shop> {

    private WeakReference<Context> context;
    private DBManager dbManager;
    private SQLiteDatabase db;


    public ShopDAO(Context context, DBManager dbManager) {
        this.context = new WeakReference<Context>(context);
        this.dbManager = dbManager;
        this.db = dbManager.getDB();
    }

    public ShopDAO(Context context) {
        this(context, DBManager.getInstance(context) );
    }


    /**
     * Insert a shop in the DB.
     *
     * @param shop should not be null.
     * @return 0 if shop is null, INVALID_ID if insert fails, id if insert succeeds.
     */
    @Override
    public long insert(@NonNull Shop shop) {

        if (shop == null)
            return 0;

        db.beginTransaction();

        long id = DBManager.INVALID_ID; // If the insert succeeds, will be replaced with the id of the new record

        try {
            id = db.insert(TABLE_SHOP, null, getContentValuesFromShop(shop) );
            shop.setId(id);
            db.setTransactionSuccessful();  // Commit the transaction
        }
        finally {
            db.endTransaction();    // This is always called, and in case the transaction was unsuccessful
        }                           // (because an exception happened) then performs a rollback

        return id;
    }


    /**
     * Updates a Shop in the database (NOT IMPLEMENTED)
     *
     * @param id the id of the row we want to update.
     * @param shop a Shop containing the data we want to update in the row.
     */
    @Override
    public void update(final long id, final @NonNull Shop shop) {

    }


    /**
     * Deletes a Shop from the database.
     *
     * @param id the id of the row we want to delete.
     * @return the number of rows affected by the operation (should be 0 or 1)
     */
    @Override
    public int delete(final long id) {

        //return db.delete(TABLE_SHOP, KEY_SHOP_ID + " = " + id, null);
        return db.delete(TABLE_SHOP, KEY_SHOP_ID + " = ?", new String[]{"" + id});  // Avoids code injection
    }


    /**
     * Deletes all Shops from the database.
     */
    @Override
    public void deleteAll() {
        db.delete(TABLE_SHOP, null, null);
    }


    /**
     * Gets a cursor to all rows from the Shop table, ordered by id.
     */
    @Nullable
    @Override
    public Cursor queryCursor() {

        Cursor c = db.query(TABLE_SHOP, ALL_COLUMNS_SHOP, null, null, null, null, KEY_SHOP_ID);

        if (c != null && c.getCount() > 0)
            c.moveToFirst();

        return c;
    }


    /**
     * Gets from the database the Shop object with the given id.
     *
     * @param id the id of the row we are searching for.
     * @return null if operation fails or there is no row with that id, otherwise a Shop with the row data.
     */
    @Override
    public @Nullable Shop query(final long id) {

        Cursor c = db.query(TABLE_SHOP, ALL_COLUMNS_SHOP, KEY_SHOP_ID + " = " + id, null, null, null, KEY_SHOP_ID);

        if (c != null && c.getCount() == 1)
            c.moveToFirst();
        else
            return null;

        Shop shop = getShopFromCursor(c);
        return shop;
    }


    /**
     * Gets a List with all the Shops of the database.
     *
     * @return null if the query operation failed, otherwise the List of Shops.
     */
    @Nullable
    @Override
    public List<Shop> query() {

        Cursor c = queryCursor();
        if (c == null || !c.moveToFirst() )
            return null;

        List<Shop> shopList = new LinkedList<>();

        c.moveToFirst();
        do {
            Shop newShop = getShopFromCursor(c);
            shopList.add(newShop);
        }
        while ( c.moveToNext() );

        return shopList;
    }


    // Auxiliary methods:

    // Maps all data from a given Shop object to a new ContentValues object
    // (to use with SQL insert)
    @NonNull
    private ContentValues getContentValuesFromShop(@NonNull Shop shop) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_SHOP_NAME, shop.getName());
        cv.put(KEY_SHOP_ADDRESS, shop.getAddress());
        cv.put(KEY_SHOP_DESCRIPTION, shop.getDescription());
        cv.put(KEY_SHOP_IMAGE_URL, shop.getImageUrl());
        cv.put(KEY_SHOP_LOGO_IMAGE_URL, shop.getLogoImgUrl());
        cv.put(KEY_SHOP_LATITUDE, shop.getLatitude());
        cv.put(KEY_SHOP_LONGITUDE, shop.getLongitude());
        cv.put(KEY_SHOP_URL, shop.getUrl());

        return cv;
    }

    // Gets a Shop object corresponding to the current position of a given cursor
    // (the cursor should be non-null, and already positioned)
    @NonNull
    private Shop getShopFromCursor(@NonNull Cursor c) {

        long id = c.getLong( c.getColumnIndex(KEY_SHOP_ID) );
        String name = c.getString( c.getColumnIndex(KEY_SHOP_NAME) );
        String imageUrl = c.getString( c.getColumnIndex(KEY_SHOP_IMAGE_URL) );
        String logoImageUrl = c.getString( c.getColumnIndex(KEY_SHOP_LOGO_IMAGE_URL) );
        String address = c.getString( c.getColumnIndex(KEY_SHOP_ADDRESS) );
        String url = c.getString( c.getColumnIndex(KEY_SHOP_URL) );
        String description = c.getString( c.getColumnIndex(KEY_SHOP_DESCRIPTION) );
        float latitude = c.getFloat( c.getColumnIndex(KEY_SHOP_LATITUDE) );
        float longitude = c.getFloat( c.getColumnIndex(KEY_SHOP_LONGITUDE) );

        Shop shop = new Shop(id,name);

        shop.setName(name)
                .setImageUrl(imageUrl)
                .setLogoImgUrl(logoImageUrl)
                .setAddress(address)
                .setUrl(url)
                .setDescription(description)
                .setLatitude(latitude)
                .setLongitude(longitude);

        return shop;
    }
}
