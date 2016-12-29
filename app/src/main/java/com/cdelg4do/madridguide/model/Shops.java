package com.cdelg4do.madridguide.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an aggregate of Shop objects.
 */
public class Shops implements ShopsIterable, ShopsUpdatable {

    List<Shop> shops;

    // All constructors are private, use the newInstance() static methods to create an object
    private Shops() {
    }

    private Shops(List<Shop> shops) {
        this.shops = shops;
    }

    public static @NonNull Shops newInstance(@NonNull final List<Shop> shopList) {

        Shops newShops = new Shops(shopList);

        if (shopList == null)
            newShops.shops = new ArrayList<Shop>();

        return newShops;
    }

    public static @NonNull Shops newInstance() {
        return newInstance( new ArrayList<Shop>() );
    }

    @Override
    public long size() {
        return shops.size();
    }

    @Override
    public Shop get(long index) {
        return shops.get((int)index);
    }

    @Override
    public List<Shop> allShops() {
        return shops;
    }

    @Override
    public void add(Shop shop) {
        shops.add(shop);
    }

    @Override
    public void delete(Shop shop) {
        shops.remove(shop);
    }

    @Override
    public void edit(Shop newShop, long index) {
        shops.set((int)index, newShop);
    }
}
