package com.cdelg4do.madridguide.model.mapper;


import com.cdelg4do.madridguide.manager.net.ShopsResponse;
import com.cdelg4do.madridguide.model.Shop;

import java.util.LinkedList;
import java.util.List;

import static com.cdelg4do.madridguide.util.Constants.LANG_ENGLISH;
import static com.cdelg4do.madridguide.util.Constants.LANG_SPANISH;


/**
 * This class is used to map a list of ShopEntity JSON objects to a list of Shop model objects.
 */
public class ShopEntityShopMapper {

    public List<Shop> map(List<ShopsResponse.ShopEntity> shopEntities) {

        List<Shop> shopList = new LinkedList<>();

        for (ShopsResponse.ShopEntity entity: shopEntities) {

            Shop shop = new Shop(0, entity.getName() );

            shop.setImageUrl( entity.getImgUrl() );
            shop.setLogoImgUrl( entity.getLogoImgUrl() );
            shop.setAddress( entity.getAddress() );
            shop.setUrl( entity.getUrl() );
            shop.setDescription(LANG_ENGLISH, entity.getDescriptionEn() );
            shop.setDescription(LANG_SPANISH, entity.getDescriptionEs() );
            shop.setOpeningHours(LANG_ENGLISH, entity.getOpeningHoursEn() );
            shop.setOpeningHours(LANG_SPANISH, entity.getOpeningHoursEs() );
            shop.setLatitude( entity.getLatitude() );
            shop.setLongitude( entity.getLongitude() );

            shopList.add(shop);
        }

        return shopList;
    }
}
