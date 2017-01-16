package com.cdelg4do.madridguide;

import android.test.AndroidTestCase;

import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Experiences;

import java.util.ArrayList;
import java.util.List;


/**
 * This class gathers all tests for the com.cdelg4do.madridguide.model.Experiences class.
 */
public class ExperiencesTests extends AndroidTestCase {

    public void testCreatingAnExperiencesWithNullListReturnsNonNullExperiences() {

        Experiences sut = Experiences.buildExperiencesFromList(null);

        assertNotNull(sut);
        assertNotNull( sut.allElements() );
    }

    public void testCreatingAnExperiencesWithAListReturnsNonNullExperiences() {

        List<Experience> testData = createTestExperienceList();

        Experiences sut = Experiences.buildExperiencesFromList(testData);

        assertNotNull(sut);
        assertNotNull(sut.allElements() );
        assertEquals(sut.allElements(), testData);
        assertEquals(sut.allElements().size(), testData.size() );
    }


    // Auxiliary methods:

    private List<Experience> createTestExperienceList() {

        List<Experience> data = new ArrayList<>();
        data.add( new Experience(1, "Experience 1").setAddress("Addr 1") );
        data.add( new Experience(2, "Experience 2").setAddress("Addr 2") );

        return data;
    }
}
