package com.cdelg4do.madridguide;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.manager.db.ExperienceDAO;
import com.cdelg4do.madridguide.model.Experience;

import java.util.List;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.manager.db.ExperienceDAO class.
 */
public class ExperienceDAOTests extends AndroidTestCase {

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

    public void testCanInsertANewExperience() {

        ExperienceDAO sut = new ExperienceDAO( getContext() );

        final int count = getQueryCount(sut);
        final long id = insertTestExperience(sut);

        assertTrue(id > 0);
        assertTrue( count+1 == sut.queryCursor().getCount() );
    }

    public void testCanDeleteAnExperience() {

        final ExperienceDAO sut = new ExperienceDAO(getContext());

        final long id = insertTestExperience(sut);
        final int count = getQueryCount(sut);

        int affectedRows = sut.delete(id);

        assertEquals(1, affectedRows);
        assertTrue( count-1 == sut.queryCursor().getCount() );
    }

    public void testDeleteAllExperiences() {

        final ExperienceDAO sut = new ExperienceDAO( getContext() );

        sut.deleteAll();

        final int count = getQueryCount(sut);
        assertEquals(0, count);
    }

    public void testQueryOneExperience() {

        final ExperienceDAO dao = new ExperienceDAO( getContext() );

        final long id = insertTestExperience(dao);

        Experience sut = dao.query(id);

        assertNotNull(sut);
        assertEquals(id, sut.getId() );
        assertEquals(NEW_EXPERIENCE_NAME, sut.getName() );
        assertEquals(NEW_EXPERIENCE_ADDRESS, sut.getAddress() );
    }

    public void testQueryAllExperiences() {

        final ExperienceDAO dao = new ExperienceDAO( getContext() );

        final int count = getQueryCount(dao);

        insertTestExperience(dao);
        insertTestExperience(dao);
        insertTestExperience(dao);

        List<Experience> sut = dao.query();

        assertNotNull(sut);
        assertTrue(sut.size() > 0);
        assertTrue(sut.size() == count+3);
    }


    // Auxiliary methods:

    private int getQueryCount(ExperienceDAO experienceDAO) {

        final Cursor c = experienceDAO.queryCursor();
        return c.getCount();
    }

    private long insertTestExperience(ExperienceDAO experienceDAO) {

        final Experience experience = new Experience(1,NEW_EXPERIENCE_NAME).setAddress(NEW_EXPERIENCE_ADDRESS);
        return experienceDAO.insert(experience);
    }

}
