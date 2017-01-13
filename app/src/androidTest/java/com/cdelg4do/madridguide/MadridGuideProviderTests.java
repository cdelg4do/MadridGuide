package com.cdelg4do.madridguide;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.model.Shop;

import static com.cdelg4do.madridguide.manager.db.DBConstants.ALL_COLUMNS_SHOP;
import static com.cdelg4do.madridguide.manager.db.provider.MadridGuideProvider.SHOPS_URI;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.manager.db.provider.MadridGuideProvider class.
 */
public class MadridGuideProviderTests extends AndroidTestCase {

    public void testQueryAllShops() {

        ContentResolver cr = getContext().getContentResolver();

        Cursor c = cr.query(SHOPS_URI, ALL_COLUMNS_SHOP, null, null, null);
        assertNotNull(c);
    }

    public void testInsertShop() {

        ContentResolver cr = getContext().getContentResolver();

        final Cursor beforeCursor = cr.query(SHOPS_URI, ALL_COLUMNS_SHOP, null, null, null);
        final int beforeCount = beforeCursor.getCount();

        final Shop shop = new Shop(1, "Little shop of horrors");
        final Uri insertedUri = cr.insert(SHOPS_URI, shop.toContentValues());

        final Cursor afterCursor = cr.query(SHOPS_URI, ALL_COLUMNS_SHOP, null, null, null);
        final int afterCount = afterCursor.getCount();

        assertNotNull(insertedUri);
        assertEquals(beforeCount+1, afterCount);
    }

    public void testDeleteShop() {

        ContentResolver cr = getContext().getContentResolver();

        final Shop shop = new Shop(1, "Little shop of horrors");
        final Uri insertedUri = cr.insert(SHOPS_URI, shop.toContentValues());

        final Cursor beforeCursor = cr.query(SHOPS_URI, ALL_COLUMNS_SHOP, null, null, null);
        final int beforeCount = beforeCursor.getCount();

        cr.delete(insertedUri, null, null);

        final Cursor afterCursor = cr.query(SHOPS_URI, ALL_COLUMNS_SHOP, null, null, null);
        final int afterCount = afterCursor.getCount();

        assertNotNull(insertedUri);
        assertEquals(beforeCount-1, afterCount);
    }

/*
    public void testQueryShopsByString() {

        ContentResolver cr = getContext().getContentResolver();

        String queryString = "Serrano";

        String selection = KEY_SHOP_NAME +" LIKE ? OR "+
                KEY_SHOP_DESCRIPTION_EN +" LIKE ? OR "+
                KEY_SHOP_DESCRIPTION_ES +" LIKE ? OR "+
                KEY_SHOP_ADDRESS + " LIKE ?";

        String[] selectionArgs = new String[] {
                "%"+ queryString +"%",
                "%"+ queryString +"%",
                "%"+ queryString +"%",
                "%"+ queryString +"%"
        };

        Cursor c = cr.query(SHOPS_URI, ALL_COLUMNS_SHOP, selection, selectionArgs, null);

        Shops shops = Shops.buildShopsFromCursor(c);

        assertEquals(shops.size(),2);

        for (Shop shop: shops.allElements())
            Log.d("TEST", "NAME: "+ shop.getName() +" ID: "+ shop.getId() +" ADDRESS: "+ shop.getAddress());

        assertEquals(shops.get(0).getName(),"Adolfo Dominguez");
    }
*/

}
