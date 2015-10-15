package com.example.sujan.madmoney.Requesters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sujan on 5/10/15.
 */
public class FetchMoneyRequest {

    private String decryptedOTP;
    private String userAddressId;

    public FetchMoneyRequest(String decryptedOTP, String userAddressId) {
        this.decryptedOTP = decryptedOTP;
        this.userAddressId = userAddressId;
    }

    public String toJSON() {

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject.put("DecryptedOTP", this.decryptedOTP);
            jsonObject.put("UserAddressId", this.userAddressId);
            jsonObject1.put("data", jsonObject);
            return jsonObject1.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}
