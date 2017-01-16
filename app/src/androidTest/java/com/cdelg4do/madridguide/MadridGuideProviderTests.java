package com.cdelg4do.madridguide;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Shop;

import static com.cdelg4do.madridguide.manager.db.DBConstants.ALL_COLUMNS_EXPERIENCE;
import static com.cdelg4do.madridguide.manager.db.DBConstants.ALL_COLUMNS_SHOP;
import static com.cdelg4do.madridguide.manager.db.provider.MadridGuideProvider.EXPERIENCES_URI;
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

    public void testQueryAllExperiences() {

        ContentResolver cr = getContext().getContentResolver();

        Cursor c = cr.query(EXPERIENCES_URI, ALL_COLUMNS_EXPERIENCE, null, null, null);
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

    public void testInsertExperience() {

        ContentResolver cr = getContext().getContentResolver();

        final Cursor beforeCursor = cr.query(EXPERIENCES_URI, ALL_COLUMNS_EXPERIENCE, null, null, null);
        final int beforeCount = beforeCursor.getCount();

        final Experience experience = new Experience(1, "A new activity");
        final Uri insertedUri = cr.insert(EXPERIENCES_URI, experience.toContentValues());

        final Cursor afterCursor = cr.query(EXPERIENCES_URI, ALL_COLUMNS_EXPERIENCE, null, null, null);
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

}
