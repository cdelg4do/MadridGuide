package com.cdelg4do.madridguide;

import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.model.Shop;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.model.Shop class.
 */
public class ShopTests extends AndroidTestCase {

    public static final String NEW_SHOP_NAME = "A shop";
    public static final String NEW_SHOP_IMAGE_URL = "A shop";
    public static final String NEW_SHOP_LOGO_IMAGE_URL = "A shop";
    public static final String NEW_SHOP_ADDRESS = "A shop";
    public static final String NEW_SHOP_URL = "A shop";
    public static final String NEW_SHOP_DESCRIPTION = "A shop";
    public static final float NEW_SHOP_LATITUDE = 40.42f;
    public static final float NEW_SHOP_LONGITUDE = -3.69f;


    public void testCanCreateAShop() {

        Shop sut = new Shop(0,NEW_SHOP_NAME);    // s.u.t. = System Under Test

        assertNotNull(sut);
    }

    public void testAnewShopStoresDataCorrectly() {

        Shop sut = new Shop(23,NEW_SHOP_NAME);

        assertEquals(23, sut.getId() );
        assertEquals(NEW_SHOP_NAME, sut.getName() );
    }

    public void testANewShopStoresDataInPropertiesCorrectly() {

        Shop sut = new Shop(25, NEW_SHOP_NAME)
                .setImageUrl(NEW_SHOP_IMAGE_URL)
                .setLogoImgUrl(NEW_SHOP_LOGO_IMAGE_URL)
                .setAddress(NEW_SHOP_ADDRESS)
                .setUrl(NEW_SHOP_URL)
                .setDescription(NEW_SHOP_DESCRIPTION)
                .setLatitude(NEW_SHOP_LATITUDE)
                .setLongitude(NEW_SHOP_LONGITUDE);

        assertEquals(sut.getImageUrl(), NEW_SHOP_IMAGE_URL);
        assertEquals(sut.getLogoImgUrl(), NEW_SHOP_LOGO_IMAGE_URL);
        assertEquals(sut.getAddress(), NEW_SHOP_ADDRESS);
        assertEquals(sut.getUrl(), NEW_SHOP_URL);
        assertEquals(sut.getDescription(), NEW_SHOP_DESCRIPTION);
        assertEquals(sut.getLatitude(), NEW_SHOP_LATITUDE);
        assertEquals(sut.getLongitude(), NEW_SHOP_LONGITUDE);
    }


}
