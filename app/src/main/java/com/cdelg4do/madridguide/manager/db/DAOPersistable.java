package com.cdelg4do.madridguide.manager.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This interface describes the behavior of the DAO objects for each model entity.
 */
public interface DAOPersistable<T> {

    long insert(@NonNull T data);
    void update(final long id, final @NonNull T data);
    void delete(final long id);
    void deleteAll();
    @Nullable Cursor queryCursor();
    T query(final long id);
}
