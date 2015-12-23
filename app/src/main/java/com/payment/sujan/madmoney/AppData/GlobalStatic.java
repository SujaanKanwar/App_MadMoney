package com.payment.sujan.madmoney.AppData;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by sujan on 9/10/15.
 */
public class GlobalStatic {

    private static HashMap<Integer, List<Money>> moneyCollection;
    private static HashMap<Integer, List<Money>> bucketCollection;
    private static HashMap<Integer, List<Money>> transCollection;
    private static List<BluetoothDevice> bluetoothDeviceList;
    private static List<UserAddress> onlineUserAddressList;
    private static GoogleApiClient googleApiClient;
    private static String userAddressId;

    public static PendingIntent getGeofenceTransitionService() {
        return geofenceTransitionService;
    }

    public static void setGeofenceTransitionService(PendingIntent geofenceTransitionService) {
        GlobalStatic.geofenceTransitionService = geofenceTransitionService;
    }

    private static PendingIntent geofenceTransitionService;

    private GlobalStatic() {
    }

    public static GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public static void setGoogleApiClient(GoogleApiClient googleApiClient) {
        GlobalStatic.googleApiClient = googleApiClient;
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
        GlobalStatic.bluetoothDeviceList = bluetoothDeviceList;
    }


    public static HashMap<Integer, List<Money>> getMoneyCollection() {
        return moneyCollection;
    }

    public static void setMoneyCollection(HashMap<Integer, List<Money>> moneyCollection) {
        GlobalStatic.moneyCollection = moneyCollection;
    }


    public static HashMap<Integer, List<Money>> getBucketCollection() {
        return bucketCollection;
    }

    public static void setBucketCollection(HashMap<Integer, List<Money>> bucketCollection) {
        GlobalStatic.bucketCollection = bucketCollection;
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

    public static HashMap<Integer, List<Money>> getTransCollection() {
        return transCollection;
    }

    public static void setTransCollection(HashMap<Integer, List<Money>> transCollection) {
        GlobalStatic.transCollection = transCollection;
    }

    public static List<UserAddress> getOnlineUserAddressList() {
        return onlineUserAddressList;
    }

    public static void setOnlineUserAddressList(List<UserAddress> onlineUserAddressList) {
        GlobalStatic.onlineUserAddressList = onlineUserAddressList;
    }
}
