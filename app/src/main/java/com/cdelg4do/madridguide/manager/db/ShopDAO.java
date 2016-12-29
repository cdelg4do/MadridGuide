package com.cdelg4do.madridguide.manager.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cdelg4do.madridguide.model.Shop;

import java.lang.ref.WeakReference;

import static com.cdelg4do.madridguide.manager.db.DBConstants.TABLE_SHOP;

/**
 * This class manages the operations on the Shop table of the database.
 */
public class ShopDAO implements DAOPersistable<Shop> {

    private WeakReference<Context> context;

    @Override
    public long insert(@NonNull Shop shop) {

        if (shop == null)
            return 0;

        DBManager dbManager = DBManager.getInstance( context.get() );
        SQLiteDatabase db = dbManager.getDB();

        db.beginTransaction();

        long id = DBManager.INVALID_ID; // If the insert succeeds, will take the value of the new record id

        try {
            id = db.insert(TABLE_SHOP, null, this.getContentValues(shop));
            shop.setId(id);
            db.setTransactionSuccessful();  // Commit the transaction
        }
        finally {
            db.endTransaction();    // Rollback
        }

        dbManager.close();
        dbManager = null;

        return id;
    }

    @Override
    public void update(final long id, final @NonNull Shop shop) {

    }

    @Override
    public void delete(final long id) {

    }

    @Override
    public void deleteAll() {

    }

    @Nullable
    @Override
    public Cursor queryCursor() {
        return null;
    }

    @Override
    public Object query(final long id) {
        return null;
    }
}
