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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
        String query = "CREATE TABLE " + TEL_POSITION_TABLE_NAME + " ( Id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", Latitude TEXT not null unique" +
                ", Longitude TEXT not null unique" +
                ", Radius TEXT not null" +
                ", LocationName TEXT not null" +
                ", City TEXT not null" +
                ", Description TEXT not null" +
                ", RequestId TEXT not null" +
                ", LoiteringTime INTEGER not null " +
                ", GeoTransactionType TEXT not null" +
                ", ExpirationTime INTEGER not null" +
                ", OperationStatus INTEGER not null" +
                ", TimeOfDiscovery TEXT)"; // 0 for new from server, 1 for geofenced, 2 for detected locations

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
                values.put("LocationName", positions.get(i).getLocationName());
                values.put("City", positions.get(i).getCity());
                values.put("Description", positions.get(i).getDescription());
                values.put("RequestId", positions.get(i).getRequestId());
                values.put("LoiteringTime", positions.get(i).getLoiteringTime());
                values.put("GeoTransactionType", positions.get(i).getGeoTransactionType());
                values.put("ExpirationTime", positions.get(i).getExpirationTime());
                values.put("OperationStatus", 0);

                database.insert(TEL_POSITION_TABLE_NAME, null, values);
            }

            database.close();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
            return false;
        }
        return true;
    }

    public Position selectTelPosition(String requestId) {
        Position position = null;
        String MY_QUERY = "SELECT * FROM " + TEL_POSITION_TABLE_NAME + " WHERE RequestId = '" + requestId + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(MY_QUERY, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String latitude = cursor.getString(1);
            String longitude = cursor.getString(2);
            String radius = cursor.getString(3);
            String locationName = cursor.getString(4);
            String city = cursor.getString(5);
            String description = cursor.getString(6);
            requestId = cursor.getString(7);
            int loiteringTime = cursor.getInt(8);
            String geoTransactionType = cursor.getString(9);
            int expirationTime = cursor.getInt(10);
            String dateAndTimeOfDiscovery = cursor.getString(11);
            position = new Position(id, locationName, latitude, longitude, radius,
                    city, description, requestId, loiteringTime, geoTransactionType, expirationTime, dateAndTimeOfDiscovery);
        }
        database.close();
        return position;
    }

    public List<Position> selectTelPositions(int operationStatus) {
        List<Position> list = new ArrayList<>();

        String MY_QUERY = "SELECT * FROM " + TEL_POSITION_TABLE_NAME + " WHERE OperationStatus = " + operationStatus;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(MY_QUERY, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String latitude = cursor.getString(1);
                String longitude = cursor.getString(2);
                String radius = cursor.getString(3);

                String locationName = cursor.getString(4);
                String city = cursor.getString(5);
                String description = cursor.getString(6);
                String requestId = cursor.getString(7);
                int loiteringTime = cursor.getInt(8);
                String geoTransactionType = cursor.getString(9);
                int expirationTime = cursor.getInt(10);
                String dateAndTimeOfDiscovery = cursor.getString(12);

                Position btAddress = new Position(id, locationName, latitude, longitude, radius,
                        city, description, requestId, loiteringTime, geoTransactionType, expirationTime, dateAndTimeOfDiscovery);
                list.add(btAddress);

            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public void updateOperationStatus(int id, int operationStatus) {
        Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();
        ContentValues values = new ContentValues();
        String whereClause = "Id" + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        values.put("OperationStatus", operationStatus);
        values.put("TimeOfDiscovery", currentLocalTime.toString());
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(TEL_POSITION_TABLE_NAME, values, whereClause, whereArgs);
        database.close();
    }

    public void updateOperationStatus(String requestId, int operationStatus) {
        Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();
        ContentValues values = new ContentValues();
        String whereClause = "RequestId" + "=?";
        String[] whereArgs = new String[]{String.valueOf(requestId)};
        values.put("OperationStatus", operationStatus);
        values.put("TimeOfDiscovery", currentLocalTime.toString());
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(TEL_POSITION_TABLE_NAME, values, whereClause, whereArgs);
        database.close();
    }

    public void deleteAllPositions() {
        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("delete from " + TEL_POSITION_TABLE_NAME);

        database.close();
    }
}
