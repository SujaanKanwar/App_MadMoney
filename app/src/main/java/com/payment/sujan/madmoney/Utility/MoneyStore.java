package com.payment.sujan.madmoney.Utility;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.AppData.Money;
import com.payment.sujan.madmoney.Cryptography.AESEncryptionDecryption;
import com.payment.sujan.madmoney.Cryptography.RSAEncryptionDecryption;
import com.payment.sujan.madmoney.Resources.DBMoneyStore;
import com.payment.sujan.madmoney.Utility.KeyPairGeneratorStore;

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

        List<com.payment.sujan.madmoney.AppData.Money> moneyList = new ArrayList<com.payment.sujan.madmoney.AppData.Money>();

        try {
            if (jsonArray == null || jsonArray.length() == 0)
                return false;
            for (int i = 0; i < jsonArray.length(); i++) {
                value = jsonArray.getJSONObject(i).getInt("value");
                id = jsonArray.getJSONObject(i).getString("id");
                dated = jsonArray.getJSONObject(i).getString("dated");
                ownerId = jsonArray.getJSONObject(i).getString("ownerId");
                signature = jsonArray.getJSONObject(i).getString("signature");
                com.payment.sujan.madmoney.AppData.Money money = new com.payment.sujan.madmoney.AppData.Money(value, dated, id, ownerId, signature);
                moneyList.add(money);
            }
        } catch (JSONException e) {
            return false;
        }
        if (moneyList.size() > 0) {

            com.payment.sujan.madmoney.Resources.DBMoneyStore dbMoneyStore = new com.payment.sujan.madmoney.Resources.DBMoneyStore(applicationContext);

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

            String decryptedMoney = com.payment.sujan.madmoney.Cryptography.AESEncryptionDecryption.Decrypt(synchronousKey, encryptedMoneyJSONArray);

            JSONArray jsonMoneyArray = new JSONArray(decryptedMoney);

            store(applicationContext, jsonMoneyArray);

        } catch (JSONException e) {

            Log.e(TAG, e.getMessage());

            return false;
        }
        return true;
    }

    public static boolean restoreMoney(Context applicationContext) {
        com.payment.sujan.madmoney.Resources.DBMoneyStore dbMoneyStore = new com.payment.sujan.madmoney.Resources.DBMoneyStore(applicationContext);

        dbMoneyStore.deleteAll();

        HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> hashMapMoneyList = com.payment.sujan.madmoney.AppData.GlobalStatic.getMoneyCollection();

        List<com.payment.sujan.madmoney.AppData.Money> moneyList = new ArrayList<>();

        for (Integer key : hashMapMoneyList.keySet())
            for (com.payment.sujan.madmoney.AppData.Money money : hashMapMoneyList.get(key))
                moneyList.add(money);

        return dbMoneyStore.insert(moneyList);
    }

    public static boolean deleteMoneyList(Context applicationContext, HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> collection)
    {
        com.payment.sujan.madmoney.Resources.DBMoneyStore dbMoneyStore = new com.payment.sujan.madmoney.Resources.DBMoneyStore(applicationContext);
        dbMoneyStore.deleteMoneyList(collection);
        return true;
    }

    public static SecretKeySpec decryptAESKey(String encryptedKey) {

        SecretKeySpec secretKeySpec = null;

        PrivateKey myPrivateKey = com.payment.sujan.madmoney.Utility.KeyPairGeneratorStore.getPrivateKey();

        try {
            byte[] secreteKeyInByte = com.payment.sujan.madmoney.Cryptography.RSAEncryptionDecryption.Decrypt(encryptedKey, myPrivateKey);

            secretKeySpec = new SecretKeySpec(secreteKeyInByte, 0, secreteKeyInByte.length, "AES");

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }
        return secretKeySpec;
    }
}
