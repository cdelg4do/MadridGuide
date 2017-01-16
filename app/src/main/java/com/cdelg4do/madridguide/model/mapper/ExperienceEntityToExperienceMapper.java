package com.cdelg4do.madridguide.model.mapper;


import com.cdelg4do.madridguide.manager.net.ExperiencesResponse.ExperienceEntity;
import com.cdelg4do.madridguide.model.Experience;

import java.util.LinkedList;
import java.util.List;

import static com.cdelg4do.madridguide.util.Constants.LANG_ENGLISH;
import static com.cdelg4do.madridguide.util.Constants.LANG_SPANISH;


/**
 * This class is used to map a list of ExperienceEntity JSON objects to a list of Experience model objects.
 */
public class ExperienceEntityToExperienceMapper {

    public List<Experience> map(List<ExperienceEntity> experienceEntities) {

        List<Experience> experienceList = new LinkedList<>();

        for (ExperienceEntity entity: experienceEntities) {

            Experience experience = new Experience(0, entity.getName() );

            experience.setImageUrl( entity.getImgUrl() );
            experience.setLogoImgUrl( entity.getLogoImgUrl() );
            experience.setAddress( entity.getAddress() );
            experience.setUrl( entity.getUrl() );
            experience.setDescription(LANG_ENGLISH, entity.getDescriptionEn() );
            experience.setDescription(LANG_SPANISH, entity.getDescriptionEs() );
            experience.setOpeningHours(LANG_ENGLISH, entity.getOpeningHoursEn() );
            experience.setOpeningHours(LANG_SPANISH, entity.getOpeningHoursEs() );
            experience.setLatitude( entity.getLatitude() );
            experience.setLongitude( entity.getLongitude() );

            experienceList.add(experience);
        }

        return experienceList;
    }
}
