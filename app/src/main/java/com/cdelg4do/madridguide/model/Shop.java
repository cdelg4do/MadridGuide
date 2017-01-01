package com.cdelg4do.madridguide.model;

import java.io.Serializable;

/**
 * This class represents a shop in the application.
 * It implements the Serializable interface so that it can be passed in an Intent.
 */
public class Shop implements Serializable {

    private long id;
    private String name;
    private String imageUrl;
    private String logoImgUrl;
    private String address;
    private String url;
    private String description;
    private float latitude;
    private float longitude;

    // The default constructor will not be public
    private Shop() {
    }

    public Shop(long id, String name) {
        this.id = id;
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }


    // Setters:

    public Shop setId(long id) {
        this.id = id;
        return this;
    }

    public Shop setName(String name) {
        this.name = name;
        return this;
    }

    public Shop setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Shop setLogoImgUrl(String logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
        return this;
    }

    public Shop setAddress(String address) {
        this.address = address;
        return this;
    }

    public Shop setUrl(String url) {
        this.url = url;
        return this;
    }

    public Shop setDescription(String description) {
        this.description = description;
        return this;
    }

    public Shop setLatitude(float latitude) {
        this.latitude = latitude;
        return this;
    }

    public Shop setLongitude(float longitude) {
        this.longitude = longitude;
        return this;
    }
}
