package com.example.sujan.madmoney.AppData;

/**
 * Created by sujan on 5/11/15.
 */
public class BTAddress {
    private String id;
    private String deviceName;
    private String deviceAddress;
    private String userName;
    private String userAddressId;
    private String phoneNo;
    public BTAddress(String id, String deviceName, String deviceAddress, String name, String userAddressId, String phoneNo) {
        this.id = id;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.userName = name;
        this.userAddressId = userAddressId;
        this.phoneNo = phoneNo;
    }
}
