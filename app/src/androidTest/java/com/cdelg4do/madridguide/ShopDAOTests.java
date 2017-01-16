package com.cdelg4do.madridguide;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.model.Shop;

import java.util.List;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.manager.db.ShopDAO class.
 */
public class ShopDAOTests extends AndroidTestCase {

    public static final String NEW_SHOP_NAME = "A shop";
    public static final String NEW_SHOP_IMAGE_URL = "A shop";
    public static final String NEW_SHOP_LOGO_IMAGE_URL = "A shop";
    public static final String NEW_SHOP_ADDRESS = "A shop";
    public static final String NEW_SHOP_URL = "A shop";
    public static final String NEW_SHOP_DESCRIPTION = "A shop";
    public static final float NEW_SHOP_LATITUDE = 40.42f;
    public static final float NEW_SHOP_LONGITUDE = -3.69f;

    public void testCanInsertANewShop() {

        ShopDAO sut = new ShopDAO( getContext() );

        final int count = getQueryCount(sut);
        final long id = insertTestShop(sut);

        assertTrue(id > 0);
        assertTrue( count+1 == sut.queryCursor().getCount() );
    }

    public void testCanDeleteAShop() {

        final ShopDAO sut = new ShopDAO(getContext());

        final long id = insertTestShop(sut);
        final int count = getQueryCount(sut);

        int affectedRows = sut.delete(id);

        assertEquals(1, affectedRows);
        assertTrue( count-1 == sut.queryCursor().getCount() );
    }

    public void testDeleteAll() {

        final ShopDAO sut = new ShopDAO( getContext() );

        sut.deleteAll();

        final int count = getQueryCount(sut);
        assertEquals(0, count);
    }

    public void testQueryOneShop() {

        final ShopDAO dao = new ShopDAO( getContext() );

        final long id = insertTestShop(dao);

        Shop sut = dao.query(id);

        assertNotNull(sut);
        assertEquals(id, sut.getId() );
        assertEquals(NEW_SHOP_NAME, sut.getName() );
        assertEquals(NEW_SHOP_ADDRESS, sut.getAddress() );
    }

    public void testQueryAllShops() {

        final ShopDAO dao = new ShopDAO( getContext() );

        final int count = getQueryCount(dao);

        insertTestShop(dao);
        insertTestShop(dao);
        insertTestShop(dao);

        List<Shop> sut = dao.query();

        assertNotNull(sut);
        assertTrue(sut.size() > 0);
        assertTrue(sut.size() == count+3);
    }


    // Auxiliary methods:

    private int getQueryCount(ShopDAO shopDAO) {

        final Cursor c = shopDAO.queryCursor();
        return c.getCount();
    }

    private long insertTestShop(ShopDAO shopDAO) {

        final Shop shop = new Shop(1,NEW_SHOP_NAME).setAddress(NEW_SHOP_ADDRESS);
        return shopDAO.insert(shop);
    }

}
