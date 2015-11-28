package com.payment.sujan.madmoney.AppData;

/**
 * Created by sujan on 5/11/15.
 */
public class UserAddress {
    private String id;
    private String userName;
    private String userAddressId;
    private String phoneNo;
    public UserAddress(String id, String userName, String userAddressId, String phoneNo) {
        this.id = id;
        this.userName = userName;
        this.userAddressId = userAddressId;
        this.phoneNo = phoneNo;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAddressId() {
        return userAddressId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
}
