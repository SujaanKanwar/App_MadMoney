package com.example.sujan.madmoney.Utility;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.Money;
import com.example.sujan.madmoney.Cryptography.AESEncryptionDecryption;
import com.example.sujan.madmoney.Cryptography.RSAEncryptionDecryption;
import com.example.sujan.madmoney.Resources.DBMoneyStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sujan on 6/10/15.
 */
public class MoneyStore {
    private static String TAG = "MoneyStore";

    public static boolean store(Context applicationContext, JSONArray jsonArray) {

        String dated, id, ownerId, signature;

        int value;

        List<Money> moneyList = new ArrayList<Money>();

        try {
            if (jsonArray == null || jsonArray.length() == 0)
                return false;
            for (int i = 0; i < jsonArray.length(); i++) {
                value = jsonArray.getJSONObject(i).getInt("value");
                id = jsonArray.getJSONObject(i).getString("id");
                dated = jsonArray.getJSONObject(i).getString("dated");
                ownerId = jsonArray.getJSONObject(i).getString("ownerId");
                signature = jsonArray.getJSONObject(i).getString("signature");
                Money money = new Money(value, dated, id, ownerId, signature);
                moneyList.add(money);
            }
        } catch (JSONException e) {
            return false;
        }
        if (moneyList.size() > 0) {

            DBMoneyStore dbMoneyStore = new DBMoneyStore(applicationContext);

            dbMoneyStore.insert(moneyList);
            return true;
        }
        return false;
    }

    public static boolean storeBluetoothTransferMoney(Context applicationContext, String moneyResponse) {

        try {
            JSONObject jsonObject = new JSONObject(moneyResponse);

            String encryptedKey = jsonObject.getString("ENCRYPTED_KEY");

            String encryptedMoneyJSONArray = jsonObject.getString("MONEY");

            SecretKeySpec synchronousKey = decryptAESKey(encryptedKey);

            String decryptedMoney = AESEncryptionDecryption.Decrypt(synchronousKey, encryptedMoneyJSONArray);

            JSONArray jsonMoneyArray = new JSONArray(decryptedMoney);

            store(applicationContext, jsonMoneyArray);

        } catch (JSONException e) {

            Log.e(TAG, e.getMessage());

            return false;
        }
        return true;
    }

    public static boolean restoreMoney(Context applicationContext) {
        DBMoneyStore dbMoneyStore = new DBMoneyStore(applicationContext);

        dbMoneyStore.deleteAll();

        HashMap<Integer, List<Money>> hashMapMoneyList = GlobalStatic.getMoneyCollection();

        List<Money> moneyList = new ArrayList<>();

        for (Integer key : hashMapMoneyList.keySet())
            for (Money money : hashMapMoneyList.get(key))
                moneyList.add(money);

        return dbMoneyStore.insert(moneyList);
    }

    public static boolean deleteMoneyList(Context applicationContext, HashMap<Integer, List<Money>> collection)
    {
        DBMoneyStore dbMoneyStore = new DBMoneyStore(applicationContext);
        dbMoneyStore.deleteMoneyList(collection);
        return true;
    }

    public static SecretKeySpec decryptAESKey(String encryptedKey) {

        SecretKeySpec secretKeySpec = null;

        PrivateKey myPrivateKey = KeyPairGeneratorStore.getPrivateKey();

        try {
            byte[] secreteKeyInByte = RSAEncryptionDecryption.Decrypt(encryptedKey, myPrivateKey);

            secretKeySpec = new SecretKeySpec(secreteKeyInByte, 0, secreteKeyInByte.length, "AES");

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }
        return secretKeySpec;
    }
}
