package com.payment.sujan.madmoney.Requesters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sujan on 30/9/15.
 */
public class CreateUserRequest {
    private String fName;
    private String lName;
    private Address address;
    private String publicKey;

    public CreateUserRequest(String fName, String lName, String publicKey, Address address) {
        this.fName = fName;
        this.lName = lName;
        this.publicKey = publicKey;
        this.address = address;
    }

    public static class Address {
        String countryCode;
        String state;
        String city;
        String local;

        public Address(String countryCode, String state, String city, String local) {
            this.countryCode = countryCode;
            this.state = state;
            this.city = city;
            this.local = local;
        }

        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("CountryCode", this.countryCode);
                jsonObject.put("State", this.state);
                jsonObject.put("City", this.city);
                jsonObject.put("Local", this.local);
                return jsonObject;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }

    public String toJSON() {

        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject1.put("FName", this.fName);
            jsonObject1.put("LName", this.lName);
            jsonObject1.put("PublicKey", this.publicKey);
            jsonObject1.put("Address", this.address.toJSON());
            jsonObject.put("data", jsonObject1);
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}

