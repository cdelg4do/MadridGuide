package com.cdelg4do.madridguide;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.model.Experience;

import java.util.Locale;

import static com.cdelg4do.madridguide.util.Constants.LANG_DEFAULT;
import static com.cdelg4do.madridguide.util.Constants.LANG_ENGLISH;
import static com.cdelg4do.madridguide.util.Constants.LANG_SPANISH;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.model.Experience class.
 */
public class ExperienceTests extends AndroidTestCase {

    private static final String NEW_EXPERIENCE_NAME = "An experience";
    private static final String NEW_EXPERIENCE_IMAGE_URL = "http://my_experience_image_url";
    private static final String NEW_EXPERIENCE_LOGO_IMAGE_URL = "http://my_experience_logo_url";
    private static final String NEW_EXPERIENCE_ADDRESS = "An address";
    private static final String NEW_EXPERIENCE_URL = "http://my_experience_url";
    private static final String NEW_EXPERIENCE_DESCRIPTION_EN = "This is a new experience";
    private static final String NEW_EXPERIENCE_DESCRIPTION_ES = "Esta es una nueva actividad";
    private static final String NEW_EXPERIENCE_OPENING_HOURS_EN = "Monday to Friday 10:00 - 17:30";
    private static final String NEW_EXPERIENCE_OPENING_HOURS_ES = "Lunes a Viernes 10:00 - 17:30";
    private static final float NEW_EXPERIENCE_LATITUDE = 40.42f;
    private static final float NEW_EXPERIENCE_LONGITUDE = -3.69f;


    public void testCanCreateAnExperience() {

        Experience sut = new Experience(0,NEW_EXPERIENCE_NAME);

        assertNotNull(sut);
    }

    public void testANewExperienceStoresDataCorrectly() {

        Experience sut = new Experience(14,NEW_EXPERIENCE_NAME);

        assertEquals(14, sut.getId() );
        assertEquals(NEW_EXPERIENCE_NAME, sut.getName() );
    }

    public void testANewExperienceStoresDataInPropertiesCorrectly() {

        Experience sut = new Experience(15, NEW_EXPERIENCE_NAME)
                .setImageUrl(NEW_EXPERIENCE_IMAGE_URL)
                .setLogoImgUrl(NEW_EXPERIENCE_LOGO_IMAGE_URL)
                .setAddress(NEW_EXPERIENCE_ADDRESS)
                .setUrl(NEW_EXPERIENCE_URL)
                .setDescription(LANG_ENGLISH, NEW_EXPERIENCE_DESCRIPTION_EN)
                .setDescription(LANG_SPANISH, NEW_EXPERIENCE_DESCRIPTION_ES)
                .setOpeningHours(LANG_ENGLISH, NEW_EXPERIENCE_OPENING_HOURS_EN)
                .setOpeningHours(LANG_SPANISH, NEW_EXPERIENCE_OPENING_HOURS_ES)
                .setLatitude(NEW_EXPERIENCE_LATITUDE)
                .setLongitude(NEW_EXPERIENCE_LONGITUDE);

        assertEquals(sut.getImageUrl(), NEW_EXPERIENCE_IMAGE_URL);
        assertEquals(sut.getLogoImgUrl(), NEW_EXPERIENCE_LOGO_IMAGE_URL);
        assertEquals(sut.getAddress(), NEW_EXPERIENCE_ADDRESS);
        assertEquals(sut.getUrl(), NEW_EXPERIENCE_URL);
        assertEquals(sut.getDescription(LANG_ENGLISH), NEW_EXPERIENCE_DESCRIPTION_EN);
        assertEquals(sut.getDescription(LANG_SPANISH), NEW_EXPERIENCE_DESCRIPTION_ES);
        assertEquals(sut.getOpeningHours(LANG_ENGLISH), NEW_EXPERIENCE_OPENING_HOURS_EN);
        assertEquals(sut.getOpeningHours(LANG_SPANISH), NEW_EXPERIENCE_OPENING_HOURS_ES);
        assertEquals(sut.getLatitude(), NEW_EXPERIENCE_LATITUDE);
        assertEquals(sut.getLongitude(), NEW_EXPERIENCE_LONGITUDE);
    }

    public void testExperienceReturnsLocalizedDataCorrectly() {

        String localizedDescription, localizedOpeningHours;

        Experience sut = new Experience(16, NEW_EXPERIENCE_NAME)
                .setDescription(LANG_ENGLISH, NEW_EXPERIENCE_DESCRIPTION_EN)
                .setDescription(LANG_SPANISH, NEW_EXPERIENCE_DESCRIPTION_ES)
                .setOpeningHours(LANG_ENGLISH, NEW_EXPERIENCE_OPENING_HOURS_EN)
                .setOpeningHours(LANG_SPANISH, NEW_EXPERIENCE_OPENING_HOURS_ES);

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
