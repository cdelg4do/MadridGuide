package com.cdelg4do.madridguide.manager.db;

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
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_SHOP_ID;
import static com.cdelg4do.madridguide.manager.db.DBConstants.TABLE_SHOP;


/**
 * This class manages the operations on the Shop table of the database.
 */
public class ShopDAO implements DAOPersistable<Shop> {

    public static final long INVALID_ID = -1;

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

        long id = INVALID_ID; // If the insert succeeds, will be replaced with the id of the new record

        try {
            id = db.insert(TABLE_SHOP, null, shop.toContentValues() );
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
    public int deleteAll() {
        return db.delete(TABLE_SHOP, "1", null);
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
     * Gets a cursor to all rows from the Shop table with an specific id.
     */
    @Nullable
    @Override
    public Cursor queryCursor(long id) {

        Cursor c = db.query(TABLE_SHOP, ALL_COLUMNS_SHOP, KEY_SHOP_ID + " = ?", new String[]{"" + id}, null, null, null);

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

        Shop shop = Shop.buildShopFromCursor(c);
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
            Shop newShop = Shop.buildShopFromCursor(c);
            shopList.add(newShop);
        }
        while ( c.moveToNext() );

        return shopList;
    }
}
