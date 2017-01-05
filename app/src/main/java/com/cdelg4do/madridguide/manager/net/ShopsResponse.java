package com.cdelg4do.madridguide.manager.net;


import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * This class represents a JSON list of shops given by a Shops Request.
 */
public class ShopsResponse {

    @SerializedName("result") private List<ShopEntity> result;

    /**
     * This class represents a JSON shop given by a Shops Request.
     */
    public class ShopEntity {

        @SerializedName("name")             private String name;
        @SerializedName("img")              private String imgUrl;
        @SerializedName("logo_img")         private String logoImgUrl;
        @SerializedName("address")          private String address;
        @SerializedName("url")              private String url;
        @SerializedName("description_es")   private String description;
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

        public String getDescription() {
            return description;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }
    }

    public List<ShopEntity> getResult() {
        return result;
    }
}
