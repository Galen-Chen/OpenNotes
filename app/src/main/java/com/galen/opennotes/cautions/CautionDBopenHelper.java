package com.galen.opennotes.cautions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Galen on 5/14/17.
 */

public class CautionDBopenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "caution.db";


    public static final String TABLE_NAME = "cautions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CAUTIONS = "the_caution";

    public static final String CREATE_THE_TABLE="CREATE TABLE "+
            TABLE_NAME+ " ("+ COLUMN_ID +
            " INTEGER PRIMARY KEY, "+
            COLUMN_CAUTIONS+ " TEXT)";

    // create a singleton

    private static CautionDBopenHelper sInstance;

    private CautionDBopenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static CautionDBopenHelper getInstance(Context context) {
        if (sInstance == null)
            sInstance = new CautionDBopenHelper(context.getApplicationContext());
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_THE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    //take user's input into the database
    public void insertCautions(int id, String caution){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_CAUTIONS, caution);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // let the user to delete the caution
    public boolean deleteCaution(int id) {

        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID +  "=" + id, null) >0;
    }

    //get the number of the rows in order to give the id for the inputs

    public int getRowsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    //GET all the cautions from the database
    public List<Cautions> getCautions() {
        List<Cautions> cautionses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                cautionses.add(new Cautions(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CAUTIONS))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return cautionses;
    }

    // get the id by text, the id for delete the column.
    public int getID(String caution){
       int mID =-1;
//
         SQLiteDatabase db = getReadableDatabase();
//
        String[] projection =new String[]{COLUMN_ID,COLUMN_CAUTIONS};

        String selection="the_caution = ?";

        String[] selectionArgs = new String[]{ caution};

        Cursor cursor = db.query(TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            mID= cursor.getInt(cursor.getColumnIndex(COLUMN_CAUTIONS));
        }


        cursor.close();
        return mID;


//        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CAUTIONS + " = " + caution;
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst())
//        {
//            mID = cursor.getInt(cursor.getColumnIndex(COLUMN_CAUTIONS));
//        }
//
//        cursor.close();
//        return mID;





    }








}













