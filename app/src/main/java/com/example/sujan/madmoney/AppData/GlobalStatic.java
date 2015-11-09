package com.example.sujan.madmoney.AppData;

import android.bluetooth.BluetoothDevice;

import com.example.sujan.madmoney.Resources.FileOperations;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by sujan on 9/10/15.
 */
public class GlobalStatic {
    private GlobalStatic(){}

    private static HashMap<Integer, List<Money>> moneyCollection;
    private static HashMap<Integer, List<Money>> bucketCollection;
    private static List<BluetoothDevice> bluetoothDeviceList;
    private static String userAddressId;

    public static String getUserAddressId() {return userAddressId;}

    public static void setUserAddressId(String userAddressId) {GlobalStatic.userAddressId = userAddressId;}

    public static List<BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }

    public static void setBluetoothDeviceList(List<BluetoothDevice> bluetoothDeviceList) {GlobalStatic.bluetoothDeviceList = bluetoothDeviceList;}

    public static HashMap<Integer, List<Money>> getMoneyCollection() {
        return moneyCollection;
    }

    public static void setMoneyCollection(HashMap<Integer, List<Money>> moneyCollection) {GlobalStatic.moneyCollection = moneyCollection;}

    public static HashMap<Integer, List<Money>> getBucketCollection() {
        return bucketCollection;
    }

    public static void setBucketCollection(HashMap<Integer, List<Money>> bucketCollection) {GlobalStatic.bucketCollection = bucketCollection;}

    public static int getTotalBalance(){
        int totalBalance =0;
        if(bucketCollection != null)
        {
            Set<Integer> keySet = bucketCollection.keySet();
            for (int key : keySet)
            {
                totalBalance += key * bucketCollection.get(key).size();
            }
        }
        if(moneyCollection != null)
        {
            Set<Integer> keySet = moneyCollection.keySet();
            for (int key : keySet)
            {
                totalBalance += key * moneyCollection.get(key).size();
            }
        }
        return totalBalance;
    }
}
