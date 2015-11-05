package com.example.sujan.madmoney.Utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.sujan.madmoney.Connectors.BluetoothConnector;
import com.example.sujan.madmoney.Connectors.Constants;
import com.example.sujan.madmoney.Cryptography.AESEncryptionDecryption;
import com.example.sujan.madmoney.Cryptography.RSAEncryptionDecryption;
import com.example.sujan.madmoney.Fragments.BucketFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.interfaces.RSAPublicKey;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sujan on 13/10/15.
 */
public class BluetoothUtility {
    private BluetoothConnector bluetoothConnector = null;
    private Handler receiverHandler;
    private Context context;
    private BluetoothDevice device;

    public BluetoothUtility(Context context, Handler btMessageHandler, BluetoothAdapter btAdapter) {

        this.context = context;

        this.receiverHandler = btMessageHandler;

        bluetoothConnector = new BluetoothConnector(thisHandler, btAdapter);
    }

    public void sendMoney(BluetoothDevice device) {

        this.device = device;

        send("");
    }

    private void send(String madMoneyAddress) {
        if (madMoneyAddress == "") {
            bluetoothConnector.transferData(device, Constants.TELL_ME_YOUR_ADDRESS.getBytes(), Constants.TRANS_TYPE_MSG);
        } else {
            String encryptedSynchronousKey;

            APKHandler apkHandler = new APKHandler(context);

            RSAPublicKey rsaPublicKey = apkHandler.getPublicKey(madMoneyAddress);

            JSONArray money = BucketFragment.getJSONMoneyToTransferFromBucket();
            try {

                SecretKeySpec synchronousKey = AESEncryptionDecryption.generateKey();

                String encryptMoney = AESEncryptionDecryption.Encrypt(synchronousKey, money.toString());

                encryptedSynchronousKey = RSAEncryptionDecryption.Encrypt(synchronousKey.getEncoded(), rsaPublicKey);

                JSONObject data = new JSONObject();
                data.put("ENCRYPTED_KEY", encryptedSynchronousKey);
                data.put("MONEY", encryptMoney);


                bluetoothConnector.transferData(device, data.toString().getBytes(), Constants.TRANS_TYPE_MONEY);

            } catch (Exception e) {
                Log.e("Encryption Failure", e.getMessage());
            }
        }
    }

    private final Handler thisHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Message newMsg;

            switch (msg.what) {

                case Constants.MESSAGE_RECEIVED:

                    newMsg = receiverHandler.obtainMessage(Constants.MESSAGE_RECEIVED);

                    String responseData = msg.getData().getString(Constants.DATA);

                    if (responseData.contains(Constants.MY_ADDRESS)) {

                        String[] splitMessage = responseData.split(":");

                        send(splitMessage[1]);

                        break;
                    } else {
                        MoneyStore.storeBluetoothTransferMoney(context, responseData);

                        receiverHandler.sendMessage(newMsg);
                    }
                    break;
                case Constants.MESSAGE_MONEY_SENT:
                    newMsg = receiverHandler.obtainMessage(Constants.MESSAGE_MONEY_SENT);

                    receiverHandler.sendMessage(newMsg);
                    break;
                case Constants.MESSAGE_MESSAGE_SENT:
                    break;
                case Constants.CONNECTION_FAILED:

                    newMsg = receiverHandler.obtainMessage(Constants.CONNECTION_FAILED);

                    receiverHandler.sendMessage(newMsg);
                    break;
            }
        }
    };
}