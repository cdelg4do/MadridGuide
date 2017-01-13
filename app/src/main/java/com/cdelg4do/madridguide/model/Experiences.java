package com.cdelg4do.madridguide.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents an aggregate of Experience objects.
 */
public class Experiences implements IterableAggregate<Experience>, UpdatableAggregate<Experience> {

    List<Experience> experienceList;


    // All constructors are private
    // (to create an object use the buildExperiences...() static methods)
    private Experiences() {
    }

    private Experiences(List<Experience> experienceList) {
        this.experienceList = experienceList;
    }


    // Methods from interface IterableAggregate:

    @Override
    public long size() {
        return experienceList.size();
    }

    @Override
    public Experience get(long index) {
        return experienceList.get((int)index);
    }

    @Override
    public List<Experience> allElements() {
        return experienceList;
    }


    // Methods from interface UpdatableAggregate:

    @Override
    public void add(Experience element) {
        experienceList.add(element);
    }

    @Override
    public void delete(Experience element) {
        experienceList.remove(element);
    }

    @Override
    public void update(Experience newElement, long index) {
        experienceList.set((int)index, newElement);
    }


    // Auxiliary methods:

    // Creates a new Experiences object from a List of Experience objects
    public static @NonNull Experiences buildExperiencesFromList(@NonNull final List<Experience> experienceList) {

        Experiences newExperiences = new Experiences(experienceList);

        if (experienceList == null)
            newExperiences.experienceList = new ArrayList<Experience>();

        return newExperiences;
    }

    // Creates a new Experiences object that contains no Experience objects
    public static @NonNull Experiences buildExperiencesEmpty() {
        return buildExperiencesFromList( new ArrayList<Experience>() );
    }

    // Creates a new Experiences object from the data in a Cursor
    public static @NonNull Experiences buildExperiencesFromCursor(@NonNull final Cursor cursor) {

        final List<Experience> experienceList = new LinkedList<>();

        if ( cursor.moveToFirst() ) {
            do{
                experienceList.add( Experience.buildExperienceFromCursor(cursor) );
            }
            while(cursor.moveToNext());
        }

        return Experiences.buildExperiencesFromList(experienceList);
    }
}
