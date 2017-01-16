package com.cdelg4do.madridguide.model;

import java.util.List;

/**
 * This interface describes the behavior available to access and iterate through an aggregate of T objects.
 */
public interface IterableAggregate<T> {

    long size();

    T get(long index);

    List<T> allElements();
}
