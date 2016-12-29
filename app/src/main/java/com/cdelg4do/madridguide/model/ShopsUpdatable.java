package com.cdelg4do.madridguide.model;

/**
 * This interface describes the behavior available to update an aggregate of Shop.
 */
public interface ShopsUpdatable {

    void add(Shop shop);

    void delete(Shop shop);

    void edit(Shop newShop, long index);
}
