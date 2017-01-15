package com.cdelg4do.madridguide.manager.net;


import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * This class represents a JSON list of activities given by an Activities Request.
 */
public class ExperiencesResponse {

    @SerializedName("result") private List<ExperienceEntity> result;

    /**
     * This class represents a JSON shop given by a Shops Request.
     */
    public class ExperienceEntity {

        @SerializedName("name")             private String name;
        @SerializedName("img")              private String imgUrl;
        @SerializedName("logo_img")         private String logoImgUrl;
        @SerializedName("address")          private String address;
        @SerializedName("url")              private String url;
        @SerializedName("description_en")   private String description_en;
        @SerializedName("description_es")   private String description_es;
        @SerializedName("opening_hours_en") private String openingHours_en;
        @SerializedName("opening_hours_es") private String openingHours_es;
        @SerializedName("gps_lat")          private float latitude;
        @SerializedName("gps_lon")          private float longitude;

        public String getName() {
            return name;
        }

        public String getImgUrl() {
            return imgUrl;
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

        public String getDescriptionEn() {
            return description_en;
        }

        public String getDescriptionEs() {
            return description_es;
        }

        public String getOpeningHoursEn() {
            return openingHours_en;
        }

        public String getOpeningHoursEs() {
            return openingHours_es;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }
    }

    public List<ExperienceEntity> getResult() {
        return result;
    }
}
