package com.cdelg4do.madridguide;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.model.Shop;

import java.util.Locale;

import static com.cdelg4do.madridguide.util.Constants.LANG_DEFAULT;
import static com.cdelg4do.madridguide.util.Constants.LANG_ENGLISH;
import static com.cdelg4do.madridguide.util.Constants.LANG_SPANISH;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.model.Shop class.
 */
public class ShopTests extends AndroidTestCase {

    public static final String NEW_SHOP_NAME = "A shop";
    public static final String NEW_SHOP_IMAGE_URL = "http://my_image_url";
    public static final String NEW_SHOP_LOGO_IMAGE_URL = "http://my_logo_url";
    public static final String NEW_SHOP_ADDRESS = "An address";
    public static final String NEW_SHOP_URL = "http://my_shop_url";
    public static final String NEW_SHOP_DESCRIPTION_EN = "A shop";
    public static final String NEW_SHOP_DESCRIPTION_ES = "Una tienda";
    public static final String NEW_SHOP_OPENING_HOURS_EN = "Monday to Friday 09:30 - 17:00";
    public static final String NEW_SHOP_OPENING_HOURS_ES = "Lunes a Viernes 09:30 - 17:00";
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
                .setDescription(LANG_ENGLISH, NEW_SHOP_DESCRIPTION_EN)
                .setDescription(LANG_SPANISH, NEW_SHOP_DESCRIPTION_ES)
                .setOpeningHours(LANG_ENGLISH, NEW_SHOP_OPENING_HOURS_EN)
                .setOpeningHours(LANG_SPANISH, NEW_SHOP_OPENING_HOURS_ES)
                .setLatitude(NEW_SHOP_LATITUDE)
                .setLongitude(NEW_SHOP_LONGITUDE);

        assertEquals(sut.getImageUrl(), NEW_SHOP_IMAGE_URL);
        assertEquals(sut.getLogoImgUrl(), NEW_SHOP_LOGO_IMAGE_URL);
        assertEquals(sut.getAddress(), NEW_SHOP_ADDRESS);
        assertEquals(sut.getUrl(), NEW_SHOP_URL);
        assertEquals(sut.getDescription(LANG_ENGLISH), NEW_SHOP_DESCRIPTION_EN);
        assertEquals(sut.getDescription(LANG_SPANISH), NEW_SHOP_DESCRIPTION_ES);
        assertEquals(sut.getOpeningHours(LANG_ENGLISH), NEW_SHOP_OPENING_HOURS_EN);
        assertEquals(sut.getOpeningHours(LANG_SPANISH), NEW_SHOP_OPENING_HOURS_ES);
        assertEquals(sut.getLatitude(), NEW_SHOP_LATITUDE);
        assertEquals(sut.getLongitude(), NEW_SHOP_LONGITUDE);
    }

    public void testShopReturnsLocalizedDataCorrectly() {

        String localizedDescription, localizedOpeningHours;

        Shop sut = new Shop(25, NEW_SHOP_NAME)
                .setDescription(LANG_ENGLISH, NEW_SHOP_DESCRIPTION_EN)
                .setDescription(LANG_SPANISH, NEW_SHOP_DESCRIPTION_ES)
                .setOpeningHours(LANG_ENGLISH, NEW_SHOP_OPENING_HOURS_EN)
                .setOpeningHours(LANG_SPANISH, NEW_SHOP_OPENING_HOURS_ES);

        setLocale("es");
        localizedDescription = sut.getLocalizedDescription();
        localizedOpeningHours = sut.getLocalizedOpeningHours();

        assertEquals(localizedDescription, sut.getDescription(LANG_SPANISH));
        assertEquals(localizedOpeningHours, sut.getOpeningHours(LANG_SPANISH));

        setLocale("fr");
        localizedDescription = sut.getLocalizedDescription();
        localizedOpeningHours = sut.getLocalizedOpeningHours();

        assertEquals(localizedDescription, sut.getDescription(LANG_DEFAULT));
        assertEquals(localizedOpeningHours, sut.getOpeningHours(LANG_DEFAULT));
    }


    // Auxiliary Methods:

    private void setLocale(String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources res = getContext().getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
