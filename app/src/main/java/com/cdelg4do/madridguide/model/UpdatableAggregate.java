package com.cdelg4do.madridguide.model;

/**
 * This interface describes the behavior available to update an aggregate of T objects.
 */
public interface UpdatableAggregate<T> {

    void add(T element);

    void delete(T element);

    void update(T newElement, long index);
}
