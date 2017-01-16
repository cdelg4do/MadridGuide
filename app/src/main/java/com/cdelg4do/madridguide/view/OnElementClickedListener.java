package com.cdelg4do.madridguide.view;


/**
 * This generic interface must be implemented by any object
 * that listens for a ListView/GridView/RecyclerView cell to be clicked.
 *
 * @param <T> the type of the object represented by the element clicked.
 */
public interface OnElementClickedListener<T> {

    void onElementClicked(T item, int position);
}
