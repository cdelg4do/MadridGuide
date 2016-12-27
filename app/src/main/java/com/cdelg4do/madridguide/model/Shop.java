package com.cdelg4do.madridguide.model;

/**
 * This class represents a shop in the application.
 */
public class Shop {

    private long id;
    private String name;
    private String imageUrl;
    private String logoImgUrl;
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
}
