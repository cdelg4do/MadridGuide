package com.cdelg4do.madridguide.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.cdelg4do.madridguide.util.Utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_ADDRESS;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_DESCRIPTION_EN;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_DESCRIPTION_ES;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_ID;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_IMAGE_URL;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_LATITUDE;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_LOGO_IMAGE_URL;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_LONGITUDE;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_NAME;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_OPENING_HOURS_EN;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_OPENING_HOURS_ES;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_URL;
import static com.cdelg4do.madridguide.util.Constants.GOOGLE_MAPS_API_KEY;
import static com.cdelg4do.madridguide.util.Constants.LANG_DEFAULT;
import static com.cdelg4do.madridguide.util.Constants.DETAIL_MAP_HEIGHT;
import static com.cdelg4do.madridguide.util.Constants.DETAIL_MAP_MARKER_COLOR;
import static com.cdelg4do.madridguide.util.Constants.DETAIL_MAP_SCALE;
import static com.cdelg4do.madridguide.util.Constants.DETAIL_MAP_TYPE;
import static com.cdelg4do.madridguide.util.Constants.DETAIL_MAP_WIDTH;
import static com.cdelg4do.madridguide.util.Constants.DETAIL_MAP_ZOOM;
import static com.cdelg4do.madridguide.util.Constants.LANG_ENGLISH;
import static com.cdelg4do.madridguide.util.Constants.LANG_SPANISH;


/**
 * This class represents an Experience (a.k.a. Activity) in the application.
 * It implements the Serializable interface so that it can be passed inside an Intent.
 */
public class Experience implements Serializable {

    private long id;
    private String name;
    private String imageUrl;
    private String logoImgUrl;
    private String address;
    private String url;
    private Map<String,String> descriptions;
    private Map<String,String> openingHours;
    private float latitude;
    private float longitude;

    // The default constructor will not be public
    private Experience() {
    }

    public Experience(long id, String name) {
        this.id = id;
        this.name = name;

        this.descriptions = new HashMap<>();
        this.openingHours = new HashMap<>();
    }


    // Getters:

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLogoImgUrl() {
        return logoImgUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription(String language) {
        return descriptions.get(language);
    }

    public String getLocalizedDescription() {

        String desc = descriptions.get(Utils.systemLanguage());

        if (desc == null)
            desc = descriptions.get(LANG_DEFAULT);

        return desc;
    }

    public String getOpeningHours(String language) {
        return openingHours.get(language);
    }

    public String getLocalizedOpeningHours() {

        String hours = openingHours.get(Utils.systemLanguage());

        if (hours == null)
            hours = openingHours.get(LANG_DEFAULT);

        return hours;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getMapUrl() {

        String mapUrl = "http://maps.googleapis.com/maps/api/staticmap"
                + "?center="+ latitude +","+ longitude
                + "&zoom="+ DETAIL_MAP_ZOOM
                + "&size="+ DETAIL_MAP_WIDTH +"x"+ DETAIL_MAP_HEIGHT
                + "&scale="+ DETAIL_MAP_SCALE
                + "&maptype="+ DETAIL_MAP_TYPE
                + "&markers=%7Ccolor:"+ DETAIL_MAP_MARKER_COLOR +"%7C"+ latitude +","+ longitude
                + "&key="+ GOOGLE_MAPS_API_KEY
                ;

        return mapUrl;
    }


    // Setters:

    public Experience setId(long id) {
        this.id = id;
        return this;
    }

    public Experience setName(String name) {
        this.name = name;
        return this;
    }

    public Experience setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Experience setLogoImgUrl(String logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
        return this;
    }

    public Experience setAddress(String address) {
        this.address = address;
        return this;
    }

    public Experience setUrl(String url) {
        this.url = url;
        return this;
    }

    public Experience setDescription(String language, String description) {
        this.descriptions.put(language,description);
        return this;
    }

    public Experience setOpeningHours(String language, String hours) {
        this.openingHours.put(language,hours);
        return this;
    }

    public Experience setLatitude(float latitude) {
        this.latitude = latitude;
        return this;
    }

    public Experience setLongitude(float longitude) {
        this.longitude = longitude;
        return this;
    }


    // Auxiliary methods:

    // Maps all data from a given Experience object to a new ContentValues object
    // (to use with SQL insert)
    @NonNull
    public ContentValues toContentValues() {

        final ContentValues cv = new ContentValues();

        // The id of the new row will be generated by the DB --> ignore this.getId()
        //cv.put(KEY_EXPERIENCE_ID, this.getId());

        cv.put(KEY_EXPERIENCE_NAME, this.getName());
        cv.put(KEY_EXPERIENCE_ADDRESS, this.getAddress());
        cv.put(KEY_EXPERIENCE_DESCRIPTION_EN, this.getDescription(LANG_ENGLISH));
        cv.put(KEY_EXPERIENCE_DESCRIPTION_ES, this.getDescription(LANG_SPANISH));
        cv.put(KEY_EXPERIENCE_OPENING_HOURS_EN, this.getOpeningHours(LANG_ENGLISH));
        cv.put(KEY_EXPERIENCE_OPENING_HOURS_ES, this.getOpeningHours(LANG_SPANISH));
        cv.put(KEY_EXPERIENCE_IMAGE_URL, this.getImageUrl());
        cv.put(KEY_EXPERIENCE_LOGO_IMAGE_URL, this.getLogoImgUrl());
        cv.put(KEY_EXPERIENCE_LATITUDE, this.getLatitude());
        cv.put(KEY_EXPERIENCE_LONGITUDE, this.getLongitude());
        cv.put(KEY_EXPERIENCE_URL, this.getUrl());

        return cv;
    }


    // Maps all data from a given ContentValues to a new Experience object
    // (to use with DAO insert)
    @NonNull
    public static Experience buildExperienceFromContentValues(final @NonNull ContentValues cv) {

        final Experience experience = new Experience(1, "");

        // Usually, the ContentValues will not include an id
        // (because we want to insert the experience as a new row in the DB).
        // So our Experience object here will have id "1" by default,
        // and it will be replaced only if the ContentValues has an id.
        if (cv.getAsString(KEY_EXPERIENCE_ID) != null)
            experience.setId(cv.getAsInteger(KEY_EXPERIENCE_ID));

        experience.setName(cv.getAsString(KEY_EXPERIENCE_NAME));
        experience.setAddress(cv.getAsString(KEY_EXPERIENCE_ADDRESS));
        experience.setDescription(LANG_ENGLISH,cv.getAsString(KEY_EXPERIENCE_DESCRIPTION_EN));
        experience.setDescription(LANG_SPANISH,cv.getAsString(KEY_EXPERIENCE_DESCRIPTION_ES));
        experience.setDescription(LANG_ENGLISH,cv.getAsString(KEY_EXPERIENCE_OPENING_HOURS_EN));
        experience.setDescription(LANG_SPANISH,cv.getAsString(KEY_EXPERIENCE_OPENING_HOURS_ES));
        experience.setImageUrl(cv.getAsString(KEY_EXPERIENCE_IMAGE_URL));
        experience.setLogoImgUrl(cv.getAsString(KEY_EXPERIENCE_LOGO_IMAGE_URL));
        experience.setLatitude(cv.getAsFloat(KEY_EXPERIENCE_LATITUDE));
        experience.setLongitude(cv.getAsFloat(KEY_EXPERIENCE_LONGITUDE));
        experience.setUrl(cv.getAsString(KEY_EXPERIENCE_URL));

        return experience;
    }


    // Maps all data from the current position of a given Cursor to a new Experience object
    // (the cursor should be non-null, and already positioned)
    @NonNull
    public static Experience buildExperienceFromCursor(@NonNull Cursor c) {

        long id = c.getLong( c.getColumnIndex(KEY_EXPERIENCE_ID) );
        String name = c.getString( c.getColumnIndex(KEY_EXPERIENCE_NAME) );
        String imageUrl = c.getString( c.getColumnIndex(KEY_EXPERIENCE_IMAGE_URL) );
        String logoImageUrl = c.getString( c.getColumnIndex(KEY_EXPERIENCE_LOGO_IMAGE_URL) );
        String address = c.getString( c.getColumnIndex(KEY_EXPERIENCE_ADDRESS) );
        String url = c.getString( c.getColumnIndex(KEY_EXPERIENCE_URL) );
        String description_en = c.getString( c.getColumnIndex(KEY_EXPERIENCE_DESCRIPTION_EN) );
        String description_es = c.getString( c.getColumnIndex(KEY_EXPERIENCE_DESCRIPTION_ES) );
        String openingHours_en = c.getString( c.getColumnIndex(KEY_EXPERIENCE_OPENING_HOURS_EN) );
        String openingHours_es = c.getString( c.getColumnIndex(KEY_EXPERIENCE_OPENING_HOURS_ES) );
        float latitude = c.getFloat( c.getColumnIndex(KEY_EXPERIENCE_LATITUDE) );
        float longitude = c.getFloat( c.getColumnIndex(KEY_EXPERIENCE_LONGITUDE) );

        Experience experience = new Experience(id,name);

        experience.setName(name)
                .setImageUrl(imageUrl)
                .setLogoImgUrl(logoImageUrl)
                .setAddress(address)
                .setUrl(url)
                .setDescription(LANG_ENGLISH, description_en)
                .setDescription(LANG_SPANISH, description_es)
                .setOpeningHours(LANG_ENGLISH, openingHours_en)
                .setOpeningHours(LANG_SPANISH, openingHours_es)
                .setLatitude(latitude)
                .setLongitude(longitude);

        return experience;
    }
}
