package com.example.rloftus88.wavewatcher.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(ForecastDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*

    Students: Uncomment this test once you've written the code to create the Location
    table.  Note that you will have to have chosen the same column names that I did in
    my solution for this test to compile, so if you haven't yet done that, this is
    a good time to change your column names to match mine.

    Note that this only tests that the Location table has the correct columns, since we
    give you the code for the weather table.  This test does not look at the

    */

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(ForecastContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(ForecastContract.ForecastEntry.TABLE_NAME);

        mContext.deleteDatabase(ForecastDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ForecastDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + ForecastContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(ForecastContract.LocationEntry._ID);
        locationColumnHashSet.add(ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        c.close();
        db.close();
    }


    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createLahinchLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */

    public void testLocationTable() {
        insertLocation();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.
        long locRowID = insertLocation();
        assertFalse("Error: Location entries not properly inserted.", locRowID == -1L);

        // First step: Get reference to writable database
        SQLiteDatabase db = new ForecastDbHelper(mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)
        ContentValues weatherTestVals = TestUtilities.createWeatherValues(locRowID);

        // Insert ContentValues into database and get a row ID back
        long weatherRowID = db.insert(ForecastContract.ForecastEntry.TABLE_NAME, null, weatherTestVals);
        assertFalse("Error: problem inserting into weatherDB.", weatherRowID == -1L);
        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                ForecastContract.ForecastEntry.TABLE_NAME,
                null, //columns to return
                null, //cols for the where clause
                null, //values for the where clause
                null, //cols to group by
                null, //cols to filter by row groups
                null //sort order
        );

        // Move the cursor to a valid database row
        assertTrue("Error: problem querying weatherDB", cursor.moveToFirst() );
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Problem validating from weatherDB",
                cursor, weatherTestVals);
        // Make sure we only recieved one record back
        assertFalse("Error: More than one record returned", cursor.moveToNext() );


        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new ForecastDbHelper(mContext).getWritableDatabase();
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Create ContentValues of what you want to insert
        // (you can use the createLahinchLocationValues if you wish)
        ContentValues testValues = TestUtilities.createLahinchLocationValues();

        // Insert ContentValues into database and get a row ID back
        long locRowID = db.insert(ForecastContract.LocationEntry.TABLE_NAME, null, testValues);
        // row ID is -1 on error
        assertTrue(locRowID != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                ForecastContract.LocationEntry.TABLE_NAME, //table to query from
                null, //columns to return
                null, //cols for the where clause
                null, //values for the where clause
                null, //cols to group by
                null, //cols to filter by row groups
                null //sort order
        );

        // Move the cursor to first database row and make sure it's valid
        assertTrue("Error: Nothing returned from locationDB query.", cursor.moveToFirst() );
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Query validation failed", cursor, testValues);

        // Make sure we only recieved one record back
        assertFalse("Error: More than one record returned", cursor.moveToNext() );

        // Finally, close the cursor and database
        cursor.close();
        db.close();

        return locRowID;
    }

}
