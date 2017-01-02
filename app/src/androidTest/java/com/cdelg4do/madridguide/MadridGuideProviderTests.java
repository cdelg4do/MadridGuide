package com.cdelg4do.madridguide;

import android.content.ContentResolver;
import android.database.Cursor;
import android.test.AndroidTestCase;

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
}
