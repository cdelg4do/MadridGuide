package com.cdelg4do.madridguide;

import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;

import java.util.ArrayList;
import java.util.List;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.model.Shops class.
 */
public class ShopsTests extends AndroidTestCase {

    public void testCreatingAShopsWithNullListReturnsNonNullShops() {

        Shops sut = Shops.newInstance(null);

        assertNotNull(sut);
        assertNotNull( sut.allShops() );
    }

    public void testCreatingAShopsWithAListReturnsNonNullShops() {

        List<Shop> testData = createTestShopList();

        Shops sut = Shops.newInstance(testData);

        assertNotNull(sut);
        assertNotNull(sut.allShops() );
        assertEquals(sut.allShops(), testData);
        assertEquals(sut.allShops().size(), testData.size() );
    }


    // Auxiliary methods:

    private List<Shop> createTestShopList() {

        List<Shop> data = new ArrayList<>();
        data.add( new Shop(1, "Shop 1").setAddress("Addr 1") );
        data.add( new Shop(2, "Shop 2").setAddress("Addr 2") );

        return data;
    }
}
