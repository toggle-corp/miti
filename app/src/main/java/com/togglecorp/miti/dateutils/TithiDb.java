package com.togglecorp.miti.dateutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Nepali Tithi Database.
 */
public class TithiDb extends SQLiteOpenHelper{

    /// Database name.
    public static final String DATABASE_NAME = "Miti.Tithi.db";
    /// Database version.
    public static final int DATABASE_VERSION = 3;

    /**
     * Create Tithi Database Helper.
     * @param context The context working on the database.
     */
    public TithiDb(Context context)
    {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    /**
     * Create tithi table for given database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tithi (" +
                        "date INTEGER PRIMARY KEY, " +  // yyyymmdd
                        "tithi TEXT, " +
                        "extra TEXT" +
                        ")"
        );
    }

    /**
     * Upgrade the database to new version. Basically delete the tithi table and create new one.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteAll(db);
    }

    /**
     * Delete all data in the database.
     */
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        deleteAll(db);
        db.close();
    }
    private void deleteAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS tithi");
        onCreate(db);
    }

    /**
     * Insert new tithi data.
     * @param date Date for which tithi is being inserted.
     * @param tithi Tithi for the date.
     * @param extra Extra information on the date.
     */
    public void insert(String date, String tithi, String extra) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", Integer.parseInt(stripDate(date)));
        values.put("tithi", tithi);
        values.put("extra", (extra==null || extra.equals("null")) ? "" : extra);
        db.insert("tithi", null, values);
        db.close();
    }

    /**
     * Update tithi data for a date.
     * @param date Date for which tithi is to be updated.
     * @param tithi New tithi for the date.
     * @param extra Extra information on the date.
     */
    public void update(String date, String tithi, String extra) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tithi", tithi);
        values.put("extra", (extra==null || extra.equals("null")) ? "" : extra);
        db.update("tithi", values, "date=?", new String[]{stripDate(date)});
        db.close();
    }

    /**
     * Get tithi data for a date.
     * @param date Date for which tithi is to be obtained.
     * @return A pair of strings (tithi, extra) for given date.
     */
    public Pair<String, String> get(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Pair<String, String> c = getTithi(db.rawQuery("SELECT * FROM tithi WHERE date=?",
                new String[]{stripDate(date)}), true);
        db.close();
        return c;
    }
    public Pair<String, String> get(Integer date) {
        SQLiteDatabase db = getReadableDatabase();
        Pair<String, String> c = getTithi(db.rawQuery("SELECT * FROM tithi WHERE date=?",
                new String[]{date+""}), true);
        db.close();
        return c;
    }

//    public List<Pair<String, String>> get(List<Integer> dates) {
//        String joinedDate = dates.get(0).toString();
//        for (int i=1; i<dates.size(); i++) {
//            joinedDate += ", " + dates.get(i).toString();
//        }
//
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM tithi WHERE date IN (" + joinedDate + ")", null);
//        List<Pair<String, String>> r = new ArrayList<>();
//
//        while (cursor.moveToNext()) {
//            r.add(getTithi(cursor, false));
//        }
//
//        cursor.close();
//        db.close();
//        return r;
//    }

    /**
     * Delete tithi for a date.
     * @param date Date for which tithi data is to be deleted.
     */
    public void delete(String date) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("tithi", "date", new String[]{stripDate(date)});
        db.close();
    }

    private static Pair<String, String> getTithi(Cursor cursor, boolean closeCursor) {
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        Pair<String, String> r = new Pair<>(
                cursor.getString(cursor.getColumnIndex("tithi")),
                cursor.getString(cursor.getColumnIndex("extra"))
        );
        if (closeCursor) {
            cursor.close();
        }
        return r;
    }

    private static String stripDate(String date){
        return date.replaceAll("[^0-9]", "");
    }
}
