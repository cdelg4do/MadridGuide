package com.cdelg4do.madridguide.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents an aggregate of Shop objects.
 */
public class Shops implements IterableAggregate<Shop>, UpdatableAggregate<Shop> {

    List<Shop> shopList;


    // All constructors are private
    // (to create an object use the buildShops...() static methods)
    private Shops() {
    }

    private Shops(List<Shop> shopList) {
        this.shopList = shopList;
    }


    // Methods from interface IterableAggregate:

    @Override
    public long size() {
        return shopList.size();
    }

    @Override
    public Shop get(long index) {
        return shopList.get((int)index);
    }

    @Override
    public List<Shop> allElements() {
        return shopList;
    }


    // Methods from interface UpdatableAggregate:

    @Override
    public void add(Shop element) {
        shopList.add(element);
    }

    @Override
    public void delete(Shop element) {
        shopList.remove(element);
    }

    @Override
    public void update(Shop newElement, long index) {
        shopList.set((int)index, newElement);
    }


    // Auxiliary methods:

    // Creates a new Shops object from a List of Shop objects
    public static @NonNull Shops buildShopsFromList(@NonNull final List<Shop> shopList) {

        Shops newShops = new Shops(shopList);

        if (shopList == null)
            newShops.shopList = new ArrayList<Shop>();

        return newShops;
    }

    // Creates a new Shops object that contains no Shops
    public static @NonNull Shops buildShopsEmpty() {
        return buildShopsFromList( new ArrayList<Shop>() );
    }

    // Creates a new Shops object from the data in a Cursor
    public static @NonNull Shops buildShopsFromCursor(@NonNull final Cursor cursor) {

        final List<Shop> shopList = new LinkedList<>();

        if ( cursor.moveToFirst() ) {
            do{
                shopList.add( Shop.buildShopFromCursor(cursor) );
            }
            while(cursor.moveToNext());
        }

        return Shops.buildShopsFromList(shopList);
    }
}
