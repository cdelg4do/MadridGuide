package com.cdelg4do.madridguide.manager.db.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.model.Shop;

import static com.cdelg4do.madridguide.manager.db.ShopDAO.INVALID_ID;


/**
 * This class is the app content provider.
 * (provides access to app data through URIs)
 */
public class MadridGuideProvider extends ContentProvider {

    public static final String MADRIDGUIDE_PROVIDER = "com.cdelg4do.madridguide.provider";

    public static final Uri SHOPS_URI = Uri.parse("content://" + MADRIDGUIDE_PROVIDER + "/shops");

    // Constants to differentiate between the different URI requests
    public static final int ALL_SHOPS = 1;
    public static final int SINGLE_SHOP = 2;


    private static UriMatcher uriMatcher;   // used to analyze and match the received URIs

    // Static blocks are executed only the first time the class is loaded into memory
    // (used to initialize the static fields of a class)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // content://com.cdelg4do.madridguide.provider/shops
        uriMatcher.addURI(MADRIDGUIDE_PROVIDER, "shops", ALL_SHOPS);

        // content://com.cdelg4do.madridguide.provider/shops/427
        uriMatcher.addURI(MADRIDGUIDE_PROVIDER, "shops/#", SINGLE_SHOP);
    }


    // Called when the provider is created
    @Override
    public boolean onCreate() {
        return false;
    }

    // Gets the type of a given uri
    // (useful if the provider is going to receive requests from outside the application)
    @Nullable
    @Override
    public String getType(Uri uri) {

        String type = null;

        switch ( uriMatcher.match(uri) ) {

            case SINGLE_SHOP:
                type = "vnd.android.cursor.item/vnd." + MADRIDGUIDE_PROVIDER;
                break;

            case ALL_SHOPS:
                type = "vnd.android.cursor.dir/vnd." + MADRIDGUIDE_PROVIDER;
                break;

            default:
                break;
        }

        return type;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String sortOrder) {

        Cursor cursor = null;

        ShopDAO shopDAO;

        switch ( uriMatcher.match(uri) ) {

            case SINGLE_SHOP:
                String rowID = uri.getPathSegments().get(1);
                shopDAO = new ShopDAO( getContext() );
                cursor = shopDAO.queryCursor( Long.parseLong(rowID) );
                break;

            case ALL_SHOPS:
                shopDAO = new ShopDAO( getContext() );
                cursor = shopDAO.queryCursor();
                break;

            default:
                break;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        Uri insertedUri = null;

        switch ( uriMatcher.match(uri) ) {

            case ALL_SHOPS:
                Shop shop = Shop.buildShopFromContentValues(contentValues);
                ShopDAO shopDAO = new ShopDAO( getContext() );
                long insertedId = shopDAO.insert(shop);

                if (insertedId == INVALID_ID)
                    return null;

                insertedUri = ContentUris.withAppendedId(SHOPS_URI, insertedId);
                break;

            default:
                break;
        }

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(insertedUri, null);

        // Return the URI of the inserted row
        return insertedUri;
    }


    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        int deletedRowCount;

        ShopDAO shopDAO;

        switch ( uriMatcher.match(uri) ) {

            case SINGLE_SHOP:
                String rowID = uri.getPathSegments().get(1);
                shopDAO = new ShopDAO( getContext() );
                deletedRowCount = shopDAO.delete( Long.parseLong(rowID) );
                break;

            case ALL_SHOPS:
                shopDAO = new ShopDAO( getContext() );
                deletedRowCount = shopDAO.deleteAll();
                break;

            default:
                deletedRowCount = 0;
                break;
        }

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of deleted items.
        return deletedRowCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
