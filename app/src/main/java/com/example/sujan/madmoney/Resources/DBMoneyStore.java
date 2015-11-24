package com.example.sujan.madmoney.Resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sujan.madmoney.AppData.Money;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sujan on 6/10/15.
 */
public class DBMoneyStore extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "moneyStore";

    public DBMoneyStore(Context context) {
        super(context, "MoneyStore.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( Id TEXT PRIMARY KEY, Dated TEXT, " +
                "OwnerId TEXT, Signature TEXT, Value INTEGER)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean insert(List<Money> moneyList) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            for (int i = 0; i < moneyList.size(); i++) {
                values.put("Id", moneyList.get(i).getId());
                values.put("Dated", moneyList.get(i).getDated());
                values.put("OwnerId", moneyList.get(i).getOwnerId());
                values.put("Signature", moneyList.get(i).getSignature());
                values.put("Value", moneyList.get(i).getValue());
                database.insert(TABLE_NAME, null, values);
            }
            database.close();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
            return false;
        }
        return true;
    }

    public void deleteAll() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from " + TABLE_NAME);
        database.close();
    }

    public HashMap<Integer, List<Money>> retrieveAllMoney() {
        HashMap<Integer, List<Money>> moneyCollection = new HashMap<Integer, List<Money>>();

        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String dated = cursor.getString(1);
                String ownerId = cursor.getString(2);
                String signature = cursor.getString(3);
                int value = cursor.getInt(4);
                Money money = new Money(value, dated, id, ownerId, signature);

                if (!moneyCollection.containsKey(value)) {
                    List<Money> list = new ArrayList<Money>();
                    list.add(money);
                    moneyCollection.put(value, list);
                } else {
                    moneyCollection.get(value).add(money);
                }

            } while (cursor.moveToNext());
        }
        database.close();
        return moneyCollection;
    }
}
