package com.payment.sujan.madmoney.Utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.payment.sujan.madmoney.Connectors.BluetoothConnector;
import com.payment.sujan.madmoney.Connectors.Constants;
import com.payment.sujan.madmoney.Cryptography.AESEncryptionDecryption;
import com.payment.sujan.madmoney.Cryptography.RSAEncryptionDecryption;
import com.payment.sujan.madmoney.Fragments.BucketFragment;
import com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants;
import com.payment.sujan.madmoney.Utility.APKHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.interfaces.RSAPublicKey;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sujan on 13/10/15.
 */
public class BluetoothUtility {
    private com.payment.sujan.madmoney.Connectors.BluetoothConnector bluetoothConnector = null;
    private Handler receiverHandler;
    private Context context;
    private BluetoothDevice device;

    public BluetoothUtility(Context context, Handler btMessageHandler, BluetoothAdapter btAdapter) {

        this.context = context;

        this.receiverHandler = btMessageHandler;

        bluetoothConnector = new com.payment.sujan.madmoney.Connectors.BluetoothConnector(thisHandler);
    }

    public void sendMoney(BluetoothDevice device) {

        this.device = device;

        send("");

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants.ACTION_ADDRESS_RECIEVED)) {
                String deviceAddress = intent.getStringExtra(com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants.DEVICE_ADDRESS);
                if (deviceAddress.equals(device.getAddress())) {
                    String responseData = intent.getStringExtra(com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants.RESPONSE_DATA);
                    String[] splitMessage = responseData.split(":");
                    send(splitMessage[1]);
                }
                context.unregisterReceiver(broadcastReceiver);
            }
        }
    };

    IntentFilter intentFilter = new IntentFilter(com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants.ACTION_ADDRESS_RECIEVED);

    private void send(String madMoneyAddress) {
        if (madMoneyAddress == "") {
            bluetoothConnector.transferData(device, com.payment.sujan.madmoney.Connectors.Constants.TELL_ME_YOUR_ADDRESS.getBytes(), com.payment.sujan.madmoney.Connectors.Constants.TRANS_TYPE_MSG);
        } else {
            String encryptedSynchronousKey;

            com.payment.sujan.madmoney.Utility.APKHandler apkHandler = new com.payment.sujan.madmoney.Utility.APKHandler(context);

            RSAPublicKey rsaPublicKey = apkHandler.getPublicKey(madMoneyAddress);

            JSONArray money = com.payment.sujan.madmoney.Fragments.BucketFragment.getJSONMoneyToTransferFromBucket();
            try {

                SecretKeySpec synchronousKey = com.payment.sujan.madmoney.Cryptography.AESEncryptionDecryption.generateKey();

                String encryptMoney = com.payment.sujan.madmoney.Cryptography.AESEncryptionDecryption.Encrypt(synchronousKey, money.toString());

                encryptedSynchronousKey = com.payment.sujan.madmoney.Cryptography.RSAEncryptionDecryption.Encrypt(synchronousKey.getEncoded(), rsaPublicKey);

                JSONObject data = new JSONObject();
                data.put("ENCRYPTED_KEY", encryptedSynchronousKey);
                data.put("MONEY", encryptMoney);

                bluetoothConnector.transferData(device, data.toString().getBytes("UTF8"), com.payment.sujan.madmoney.Connectors.Constants.TRANS_TYPE_MONEY);

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

                case com.payment.sujan.madmoney.Connectors.Constants.MESSAGE_MONEY_SENT:
                    newMsg = receiverHandler.obtainMessage(com.payment.sujan.madmoney.Connectors.Constants.MESSAGE_MONEY_SENT);

                    receiverHandler.sendMessage(newMsg);
                    break;

                case com.payment.sujan.madmoney.Connectors.Constants.CONNECTION_FAILED:

                    newMsg = receiverHandler.obtainMessage(com.payment.sujan.madmoney.Connectors.Constants.CONNECTION_FAILED);

                    receiverHandler.sendMessage(newMsg);
                    break;
            }
        }
    };
}