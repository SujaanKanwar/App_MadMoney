package com.example.sujan.madmoney.Resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sujan.madmoney.AppData.BTAddress;
import com.example.sujan.madmoney.AppData.UserAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sujan on 5/11/15.
 */
public class DBAddressBook extends SQLiteOpenHelper {

    private static final String BT_ADDRESS_TABLE_NAME = "BT_ADDRESS_TABLE";
    private static final String USER_ADDRESS_TABLE_NAME = "USER_ADDRESS_TABLE";

    public DBAddressBook(Context context) {
        super(context, "addressBook.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + BT_ADDRESS_TABLE_NAME + " ( Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DeviceName TEXT, DeviceAddress TEXT not null unique, FK_UserAddressTable INTEGER)";
        db.execSQL(query);
        query = "CREATE TABLE " + USER_ADDRESS_TABLE_NAME + " ( Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, UserAddressId TEXT not null unique, PhoneNo Text)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean insertBTAddress(String deviceName, String deviceAddress) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("DeviceName", deviceName);

            values.put("DeviceAddress", deviceAddress);

            database.insert(BT_ADDRESS_TABLE_NAME, null, values);

            database.close();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
            return false;
        }
        return true;
    }

    public void updateFKBTTable(int fk_id, String deviceAddress) {
        ContentValues values = new ContentValues();
        values.put("FK_UserAddressTable", fk_id);
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(BT_ADDRESS_TABLE_NAME, values, "DeviceAddress " + "=" + deviceAddress, null);
        database.close();
    }

    public List<BTAddress> selectBTAddress() {
        List<BTAddress> list = new ArrayList<>();

        String MY_QUERY = "SELECT * FROM " + BT_ADDRESS_TABLE_NAME + " a LEFT JOIN " + USER_ADDRESS_TABLE_NAME +
                " b ON a.FK_UserAddressTable = b.Id";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(MY_QUERY, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String deviceName = cursor.getString(1);
                String deviceAddress = cursor.getString(2);
                String name = cursor.getString(3);
                String userAddressId = cursor.getString(4);
                String phoneNo = cursor.getString(5);
                BTAddress btAddress = new BTAddress(id, deviceName, deviceAddress, name, userAddressId, phoneNo);
                list.add(btAddress);

            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public void deleteBTAddress(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String table = BT_ADDRESS_TABLE_NAME;
        String whereClause = "Id" + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        database.delete(table, whereClause, whereArgs);
    }

    public boolean insertUserAddress(String name, String userAddressId, String phoneNo) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("Name", name);

            values.put("UserAddressId", userAddressId);

            values.put("PhoneNo", phoneNo);

            database.insert(USER_ADDRESS_TABLE_NAME, null, values);

            database.close();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
            return false;
        }
        return true;
    }

    public void updatePhoneUserTable(String phoneNo, String userAddressId) {
        ContentValues values = new ContentValues();
        values.put("PhoneNo", phoneNo);
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(USER_ADDRESS_TABLE_NAME, values, "UserAddressId " + "=" + userAddressId, null);
        database.close();
    }

    public void updateNameUserTable(String name, String userAddressId) {
        ContentValues values = new ContentValues();
        values.put("Name", name);
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(USER_ADDRESS_TABLE_NAME, values, "UserAddressId " + "=" + userAddressId, null);
        database.close();
    }

    public List<UserAddress> selectUserAddress() {
        List<UserAddress> list = new ArrayList<>();

        String MY_QUERY = "SELECT * FROM " + USER_ADDRESS_TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(MY_QUERY, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String userName = cursor.getString(1);
                String userAddressId = cursor.getString(2);
                String phoneNo = cursor.getString(3);

                UserAddress userAddress = new UserAddress(id, userName, userAddressId, phoneNo);
                list.add(userAddress);

            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public void deleteUserAddress(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String table = USER_ADDRESS_TABLE_NAME;
        String whereClause = "Id" + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        database.delete(table, whereClause, whereArgs);
    }

    public void deleteAll() {

        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("delete from " + BT_ADDRESS_TABLE_NAME);

        database.execSQL("delete from " + USER_ADDRESS_TABLE_NAME);

        database.close();
    }
}
