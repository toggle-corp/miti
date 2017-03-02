package com.togglecorp.miti.dateutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MitiDb extends SQLiteOpenHelper{

    public static class DateItem {
        public String tithi;
        public String extra;
        public Boolean holiday;
    };

    private static final String DATABASE_NAME = "Miti.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "miti";


    public MitiDb(Context context)
    {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                        "date INTEGER PRIMARY KEY, " +  // yyyymmdd
                        "tithi TEXT, " +
                        "extra TEXT," +
                        "holiday INTEGER" +
                        ")"
        );
    }

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(String date, String tithi, String extra, boolean holiday) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", Integer.parseInt(stripDate(date)));
        values.put("tithi", tithi);
        values.put("extra", (extra==null || extra.equals("null")) ? "" : extra);
        values.put("holiday", holiday?"1":"0");
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void update(String date, String tithi, String extra, boolean holiday) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tithi", tithi);
        values.put("extra", (extra==null || extra.equals("null")) ? "" : extra);
        values.put("holiday", holiday?"1":"0");
        db.update(TABLE_NAME, values, "date=?", new String[]{stripDate(date)});
        db.close();
    }

    public DateItem get(String date) {
        SQLiteDatabase db = getReadableDatabase();
        DateItem item = getTithi(db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE date=?",
                new String[]{stripDate(date)}), true);
        db.close();
        return item;
    }

    public DateItem get(Integer date) {
        SQLiteDatabase db = getReadableDatabase();
        DateItem item = getTithi(db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE date=?",
                new String[]{date+""}), true);
        db.close();
        return item;
    }

//    public List<DateItem> get(List<Integer> dates) {
//        String joinedDate = dates.get(0).toString();
//        for (int i=1; i<dates.size(); i++) {
//            joinedDate += ", " + dates.get(i).toString();
//        }
//
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM tithi WHERE date IN (" + joinedDate + ")", null);
//        List<DateItem> r = new ArrayList<>();
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
        db.delete(TABLE_NAME, "date", new String[]{stripDate(date)});
        db.close();
    }

    private static DateItem getTithi(Cursor cursor, boolean closeCursor) {
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        DateItem item = new DateItem();
        item.tithi = cursor.getString(cursor.getColumnIndex("tithi"));
        item.extra = cursor.getString(cursor.getColumnIndex("extra"));
        item.holiday = cursor.getInt(cursor.getColumnIndex("holiday")) == 1;
        if (closeCursor) {
            cursor.close();
        }
        return item;
    }

    private static String stripDate(String date){
        return date.replaceAll("[^0-9]", "");
    }
}
