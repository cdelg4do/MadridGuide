package com.cdelg4do.madridguide.model;

import java.util.List;

/**
 * This interface describes the behavior available to access and iterate through an aggregate of Shop.
 */
public interface ShopsIterable {

    long size();

    Shop get(long index);

    List<Shop> allShops();
}
