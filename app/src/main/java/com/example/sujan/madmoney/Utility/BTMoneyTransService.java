package com.example.sujan.madmoney.Utility;

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

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.Money;
import com.example.sujan.madmoney.Connectors.BluetoothConnector;
import com.example.sujan.madmoney.Connectors.Constants;
import com.example.sujan.madmoney.Cryptography.AESEncryptionDecryption;
import com.example.sujan.madmoney.Cryptography.RSAEncryptionDecryption;
import com.example.sujan.madmoney.Fragments.BucketFragment;
import com.example.sujan.madmoney.MainActivity;
import com.example.sujan.madmoney.SharedConstants.SharedBrodConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

public class BTMoneyTransService extends IntentService {

    private BluetoothConnector bluetoothConnector = null;
    Handler mHandler;
    private Context context;
    private BluetoothDevice device;

    private static final String ACTION_SEND_MONEY = "com.example.sujan.madmoney.Utility.action.SEND_MONEY";

    private static final String EXTRA_PARAM1 = "com.example.sujan.madmoney.Utility.extra.PARAM1";

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

        if (GlobalStatic.getBucketCollection() == null)
            return;

        GlobalStatic.setTransCollection(GlobalStatic.getBucketCollection());

        GlobalStatic.setBucketCollection(null);

        send("");

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SharedBrodConstants.ACTION_ADDRESS_RECIEVED)) {
                String deviceAddress = intent.getStringExtra(SharedBrodConstants.DEVICE_ADDRESS);
                if (deviceAddress.equals(device.getAddress())) {
                    String responseData = intent.getStringExtra(SharedBrodConstants.RESPONSE_DATA);
                    String[] splitMessage = responseData.split(":");
                    send(splitMessage[1]);
                }
                context.unregisterReceiver(broadcastReceiver);
            }
        }
    };

    IntentFilter intentFilter = new IntentFilter(SharedBrodConstants.ACTION_ADDRESS_RECIEVED);

    private void send(String madMoneyAddress) {
        bluetoothConnector = new BluetoothConnector(thisHandler);

        if (madMoneyAddress == "") {
            bluetoothConnector.transferData(device, Constants.TELL_ME_YOUR_ADDRESS.getBytes(), Constants.TRANS_TYPE_MSG);
        } else {
            String encryptedSynchronousKey;

            APKHandler apkHandler = new APKHandler(context);

            RSAPublicKey rsaPublicKey = apkHandler.getPublicKey(madMoneyAddress);

            JSONArray money = Money.getJSONMoneyToTransferFromBucket();
            try {

                SecretKeySpec synchronousKey = AESEncryptionDecryption.generateKey();

                String encryptMoney = AESEncryptionDecryption.Encrypt(synchronousKey, money.toString());

                encryptedSynchronousKey = RSAEncryptionDecryption.Encrypt(synchronousKey.getEncoded(), rsaPublicKey);

                JSONObject data = new JSONObject();
                data.put("ENCRYPTED_KEY", encryptedSynchronousKey);
                data.put("MONEY", encryptMoney);

                bluetoothConnector.transferData(device, data.toString().getBytes("UTF8"), Constants.TRANS_TYPE_MONEY);

            } catch (Exception e) {
                Log.e("Encryption Failure", e.getMessage());
            }
        }
    }


    private final Handler thisHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.MESSAGE_MONEY_SENT:
                    MoneyStore.deleteMoneyList(context, GlobalStatic.getTransCollection());

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BTMoneyTransService.this, "Money have been transferred", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case Constants.CONNECTION_FAILED:
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
