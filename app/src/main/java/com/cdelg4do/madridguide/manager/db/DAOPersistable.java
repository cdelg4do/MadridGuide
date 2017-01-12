package com.cdelg4do.madridguide.manager.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * This interface describes the behavior of the DAO objects for each model entity.
 */
public interface DAOPersistable<T> {

    long insert(@NonNull T data);
    void update(final long id, final @NonNull T data);
    int delete(final long id);
    int deleteAll();
    @Nullable Cursor queryCursor();
    @Nullable Cursor queryCursor(long id);
    @Nullable Cursor queryCursor(String selection, String[] selectionArgs);
    T query(final long id);
    @Nullable
    List<T> query();
}
