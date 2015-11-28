package com.payment.sujan.madmoney.AppData;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.preference.PreferenceManager;

import com.payment.sujan.madmoney.AppData.*;
import com.payment.sujan.madmoney.AppData.Money;
import com.payment.sujan.madmoney.AppData.UserAddress;
import com.payment.sujan.madmoney.Resources.FileOperations;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by sujan on 9/10/15.
 */
public class GlobalStatic {

    private static HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> moneyCollection;
    private static HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> bucketCollection;
    private static HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> transCollection;
    private static List<BluetoothDevice> bluetoothDeviceList;
    private static List<com.payment.sujan.madmoney.AppData.UserAddress> onlineUserAddressList;
    private static String userAddressId;

    private GlobalStatic() {
    }


    public static String getUserAddressId() {

        return userAddressId;
    }

    public static void setUserAddressId(String userAddressId) {
        GlobalStatic.userAddressId = userAddressId;
    }


    public static List<BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }

    public static void setBluetoothDeviceList(List<BluetoothDevice> bluetoothDeviceList) {
        com.payment.sujan.madmoney.AppData.GlobalStatic.bluetoothDeviceList = bluetoothDeviceList;
    }


    public static HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> getMoneyCollection() {
        return moneyCollection;
    }

    public static void setMoneyCollection(HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> moneyCollection) {
        com.payment.sujan.madmoney.AppData.GlobalStatic.moneyCollection = moneyCollection;
    }


    public static HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> getBucketCollection() {
        return bucketCollection;
    }

    public static void setBucketCollection(HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> bucketCollection) {
        com.payment.sujan.madmoney.AppData.GlobalStatic.bucketCollection = bucketCollection;
    }


    public static int getTotalBalance() {
        int totalBalance = 0;
        if (bucketCollection != null) {
            Set<Integer> keySet = bucketCollection.keySet();
            for (int key : keySet) {
                totalBalance += key * bucketCollection.get(key).size();
            }
        }
        if (moneyCollection != null) {
            Set<Integer> keySet = moneyCollection.keySet();
            for (int key : keySet) {
                totalBalance += key * moneyCollection.get(key).size();
            }
        }
        return totalBalance;
    }

    public static HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> getTransCollection() {
        return transCollection;
    }

    public static void setTransCollection(HashMap<Integer, List<Money>> transCollection) {
        com.payment.sujan.madmoney.AppData.GlobalStatic.transCollection = transCollection;
    }

    public static List<UserAddress> getOnlineUserAddressList() {
        return onlineUserAddressList;
    }

    public static void setOnlineUserAddressList(List<UserAddress> onlineUserAddressList) {
        com.payment.sujan.madmoney.AppData.GlobalStatic.onlineUserAddressList = onlineUserAddressList;
    }
}
