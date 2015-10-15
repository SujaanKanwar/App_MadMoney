package com.example.sujan.madmoney.AppData;

import com.example.sujan.madmoney.Connectors.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sujan on 6/10/15.
 */
public class Money implements Comparable<Money> {
    private String dated;
    private String id;
    private String ownerId;
    private String signature;
    private int value;

    public Money(int value, String dated, String id, String ownerId, String signature) {
        this.value = value;
        this.dated = dated;
        this.id = id;
        this.ownerId = ownerId;
        this.signature = signature;

    }

    public String getDated() {
        return dated;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getSignature() {
        return signature;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Money another) {
        return this.getValue() - another.getValue();
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.Money.DATED, getDated());
            jsonObject.put(Constants.Money.ID, getId());
            jsonObject.put(Constants.Money.OWNERID, getOwnerId());
            jsonObject.put(Constants.Money.SIGNATURE, getSignature());
            jsonObject.put(Constants.Money.VALUE, getValue());
        } catch (JSONException e) {
            return null;
        }

        return jsonObject;
    }
}
