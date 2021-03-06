package com.payment.sujan.madmoney.AppData;

import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.Connectors.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sujan on 6/10/15.
 */
public class Money implements Comparable<com.payment.sujan.madmoney.AppData.Money> {
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
    public int compareTo(com.payment.sujan.madmoney.AppData.Money another) {
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

    public static JSONArray getJSONMoneyToTransferFromBucket() {
        JSONArray jsonArray = new JSONArray();
        HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> bucketMoney = GlobalStatic.getTransCollection();
        if (bucketMoney == null)
            return null;
        for (Integer key : bucketMoney.keySet()) {
            for (com.payment.sujan.madmoney.AppData.Money money : bucketMoney.get(key)) {
                jsonArray.put(money.toJSON());
            }
        }
        return jsonArray;
    }
}
