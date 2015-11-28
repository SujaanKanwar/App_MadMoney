package com.payment.sujan.madmoney.Requesters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sujan on 5/10/15.
 */
public class FetchMoneyRequest {

    private String decryptedOTP;
    private String userAddressId;
    private String requestType;

    public FetchMoneyRequest(String decryptedOTP, String userAddressId, String requestType) {
        this.decryptedOTP = decryptedOTP;
        this.userAddressId = userAddressId;
        this.requestType = requestType;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject.put("DecryptedOTP", this.decryptedOTP);
            jsonObject.put("UserAddressId", this.userAddressId);
            jsonObject.put("RequestType", this.requestType);
            jsonObject1.put("data", jsonObject);
            return jsonObject1.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
