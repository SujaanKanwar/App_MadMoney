package com.payment.sujan.madmoney.Resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.payment.sujan.madmoney.AppData.BTAddress;
import com.payment.sujan.madmoney.AppData.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sujan on 12/19/2015.
 */
public class DBTeleLocation extends SQLiteOpenHelper {

    private String TEL_POSITION_TABLE_NAME = "TEL_POSITION_TABLE_NAME";

    public DBTeleLocation(Context context) {
        super(context, "telLocationDb.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TEL_POSITION_TABLE_NAME + " ( Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Latitude TEXT not null unique, Longitude TEXT not null unique, Radius TEXT not null)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean insertTelLocations(List<Position> positions) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values;

            for (int i = 0; i < positions.size(); i++) {
                values = new ContentValues();

                values.put("Latitude", positions.get(i).getLatitude());

                values.put("Longitude", positions.get(i).getLongitude());

                values.put("Radius", positions.get(i).getRadius());

                database.insert(TEL_POSITION_TABLE_NAME, null, values);
            }

            database.close();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
            return false;
        }
        return true;
    }

    public List<Position> selectTelPositions() {
        List<Position> list = new ArrayList<>();

        String MY_QUERY = "SELECT * FROM " + TEL_POSITION_TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(MY_QUERY, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String latitude = cursor.getString(1);
                String longitude = cursor.getString(2);
                String radius = cursor.getString(3);
                Position btAddress = new Position(id, latitude, longitude, radius);
                list.add(btAddress);

            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public void deleteAllPositions() {
        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("delete from " + TEL_POSITION_TABLE_NAME);

        database.close();
    }
}
