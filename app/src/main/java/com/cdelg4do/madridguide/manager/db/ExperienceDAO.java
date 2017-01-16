package com.cdelg4do.madridguide.manager.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cdelg4do.madridguide.model.Experience;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import static com.cdelg4do.madridguide.manager.db.DBConstants.ALL_COLUMNS_EXPERIENCE;
import static com.cdelg4do.madridguide.manager.db.DBConstants.KEY_EXPERIENCE_ID;
import static com.cdelg4do.madridguide.manager.db.DBConstants.TABLE_EXPERIENCE;


/**
 * This class manages the operations on the Experience table of the database.
 */
public class ExperienceDAO implements DAOPersistable<Experience> {

    public static final long INVALID_ID = -1;

    private WeakReference<Context> context;
    private DBManager dbManager;
    private SQLiteDatabase db;


    /**
     * Public constructor.
     *
     * @param context   a context for the manager operations.
     * @param dbManager a DBManager previously initialized (useful for testing).
     */
    public ExperienceDAO(Context context, DBManager dbManager) {
        this.context = new WeakReference<Context>(context);
        this.dbManager = dbManager;
        this.db = dbManager.getDB();
    }


    /**
     * Public constructor.
     *
     * @param context   a context for the manager operations.
     */
    public ExperienceDAO(Context context) {
        this(context, DBManager.getInstance(context) );
    }


    /**
     * Insert an experience in the DB.
     *
     * @param experience should not be null.
     * @return 0 if experience is null, INVALID_ID if insert fails, id if insert succeeds.
     */
    @Override
    public long insert(@NonNull Experience experience) {

        if (experience == null)
            return 0;

        db.beginTransaction();

        long id = INVALID_ID; // If the insert succeeds, will be replaced with the id of the new record

        try {
            id = db.insert(TABLE_EXPERIENCE, null, experience.toContentValues() );
            experience.setId(id);
            db.setTransactionSuccessful();  // Commit the transaction
        }
        finally {
            db.endTransaction();    // This is always called, and in case the transaction was unsuccessful
        }                           // (because an exception happened) then performs a rollback

        return id;
    }


    /**
     * Updates an experience in the database (NOT IMPLEMENTED)
     *
     * @param id the id of the row we want to update.
     * @param experience an Experience containing the data we want to update in the row.
     */
    @Override
    public void update(final long id, final @NonNull Experience experience) {

    }


    /**
     * Deletes an experience from the database.
     *
     * @param id the id of the row we want to delete.
     * @return the number of rows affected by the operation (should be 0 or 1)
     */
    @Override
    public int delete(final long id) {
        return db.delete(TABLE_EXPERIENCE, KEY_EXPERIENCE_ID + " = ?", new String[]{"" + id});
    }


    /**
     * Deletes all experiences from the database.
     *
     * @return the number of rows affected by the operation
     */
    @Override
    public int deleteAll() {
        return db.delete(TABLE_EXPERIENCE, "1", null);
    }


    /**
     * Gets a cursor to all rows from the Experience table, ordered by id.
     */
    @Nullable
    @Override
    public Cursor queryCursor() {

        Cursor c = db.query(TABLE_EXPERIENCE, ALL_COLUMNS_EXPERIENCE, null, null, null, null, KEY_EXPERIENCE_ID);

        if (c != null && c.getCount() > 0)
            c.moveToFirst();

        return c;
    }


    /**
     * Gets a cursor to all rows from the Experience table with an specific id.
     */
    @Nullable
    @Override
    public Cursor queryCursor(long id) {

        Cursor c = db.query(TABLE_EXPERIENCE, ALL_COLUMNS_EXPERIENCE, KEY_EXPERIENCE_ID + " = ?", new String[]{"" + id}, null, null, null);

        if (c != null && c.getCount() > 0)
            c.moveToFirst();

        return c;
    }


    /**
     * Gets a cursor to all rows from the Experience table matching the given selection.
     */
    @Nullable
    @Override
    public Cursor queryCursor(String selection, String[] selectionArgs) {

        Cursor c = db.query(TABLE_EXPERIENCE, ALL_COLUMNS_EXPERIENCE, selection, selectionArgs, null, null, null);

        if (c != null && c.getCount() > 0)
            c.moveToFirst();

        return c;
    }


    /**
     * Gets from the database the experience row with the given id.
     *
     * @param id the id of the row we are searching for.
     * @return null if operation fails or there is no row with that id, otherwise an Experience with the row data.
     */
    @Override
    public @Nullable Experience query(final long id) {

        Cursor c = db.query(TABLE_EXPERIENCE, ALL_COLUMNS_EXPERIENCE, KEY_EXPERIENCE_ID + " = " + id, null, null, null, KEY_EXPERIENCE_ID);

        if (c != null && c.getCount() == 1)
            c.moveToFirst();
        else
            return null;

        Experience experience = Experience.buildExperienceFromCursor(c);
        return experience;
    }


    /**
     * Gets a List with all the experiences of the database.
     *
     * @return null if the query operation failed, otherwise the List of Experience objects.
     */
    @Nullable
    @Override
    public List<Experience> query() {

        Cursor c = queryCursor();
        if (c == null || !c.moveToFirst() )
            return null;

        List<Experience> experienceList = new LinkedList<>();

        c.moveToFirst();
        do {
            Experience newExperience = Experience.buildExperienceFromCursor(c);
            experienceList.add(newExperience);
        }
        while ( c.moveToNext() );

        return experienceList;
    }
}
