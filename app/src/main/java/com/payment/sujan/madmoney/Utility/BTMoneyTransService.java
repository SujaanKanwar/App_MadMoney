package com.payment.sujan.madmoney.Utility;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.AppData.Money;
import com.payment.sujan.madmoney.Connectors.BluetoothConnector;
import com.payment.sujan.madmoney.Connectors.Constants;
import com.payment.sujan.madmoney.Cryptography.AESEncryptionDecryption;
import com.payment.sujan.madmoney.Cryptography.RSAEncryptionDecryption;
import com.payment.sujan.madmoney.Fragments.BucketFragment;
import com.payment.sujan.madmoney.MainActivity;
import com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants;
import com.payment.sujan.madmoney.Utility.*;
import com.payment.sujan.madmoney.Utility.APKHandler;
import com.payment.sujan.madmoney.Utility.MoneyStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

public class BTMoneyTransService extends IntentService {

    private com.payment.sujan.madmoney.Connectors.BluetoothConnector bluetoothConnector = null;
    Handler mHandler;
    private Context context;
    private BluetoothDevice device;

    private static final String ACTION_SEND_MONEY = "com.payment.sujan.madmoney.Utility.action.SEND_MONEY";

    private static final String EXTRA_PARAM1 = "com.payment.sujan.madmoney.Utility.extra.PARAM1";

    public static void sendMoney(Context context, BluetoothDevice device) {
        Intent intent = new Intent(context, BTMoneyTransService.class);
        intent.setAction(ACTION_SEND_MONEY);
        intent.putExtra(EXTRA_PARAM1, device);
        context.startService(intent);
    }

    public BTMoneyTransService()
    {
        super("BTMoneyTransService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_MONEY.equals(action)) {
                final BluetoothDevice param1 = intent.getParcelableExtra(EXTRA_PARAM1);
                sendMoney(param1);
            }
        }
    }


    private void sendMoney(BluetoothDevice device) {

        this.device = device;

        this.context = getApplicationContext();

        if (com.payment.sujan.madmoney.AppData.GlobalStatic.getBucketCollection() == null)
            return;

        com.payment.sujan.madmoney.AppData.GlobalStatic.setTransCollection(com.payment.sujan.madmoney.AppData.GlobalStatic.getBucketCollection());

        com.payment.sujan.madmoney.AppData.GlobalStatic.setBucketCollection(null);

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
        bluetoothConnector = new com.payment.sujan.madmoney.Connectors.BluetoothConnector(thisHandler);

        if (madMoneyAddress == "") {
            bluetoothConnector.transferData(device, com.payment.sujan.madmoney.Connectors.Constants.TELL_ME_YOUR_ADDRESS.getBytes(), com.payment.sujan.madmoney.Connectors.Constants.TRANS_TYPE_MSG);
        } else {
            String encryptedSynchronousKey;

            com.payment.sujan.madmoney.Utility.APKHandler apkHandler = new com.payment.sujan.madmoney.Utility.APKHandler(context);

            RSAPublicKey rsaPublicKey = apkHandler.getPublicKey(madMoneyAddress);

            JSONArray money = com.payment.sujan.madmoney.AppData.Money.getJSONMoneyToTransferFromBucket();
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

            switch (msg.what) {

                case com.payment.sujan.madmoney.Connectors.Constants.MESSAGE_MONEY_SENT:
                    com.payment.sujan.madmoney.Utility.MoneyStore.deleteMoneyList(context, com.payment.sujan.madmoney.AppData.GlobalStatic.getTransCollection());

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BTMoneyTransService.this, "Money have been transferred", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case com.payment.sujan.madmoney.Connectors.Constants.CONNECTION_FAILED:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BTMoneyTransService.this, "Failure while transaction", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
            }

        }
    };
}
