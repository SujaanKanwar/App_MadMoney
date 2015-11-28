package com.payment.sujan.madmoney.Services;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.Connectors.Constants;
import com.payment.sujan.madmoney.MainActivity;
import com.payment.sujan.madmoney.R;
import com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants;
import com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants;
import com.payment.sujan.madmoney.Utility.MoneyStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by sujan on 4/11/15.
 */
public class BluetoothBackgroundService extends IntentService {

    private static final UUID MAD_MONEY_UUID = UUID.fromString("61217015-8107-4ed0-9373-b1cfa4ce6494");
    private static final String CONNECTION_NAME = "MadMoneyConnection";
    private static final String TAG = "BluetoothConnector";
    private static final String DATA_USER_ADDRESS = "DATA_USER_ADDRESS";


    private static boolean RUNNING = true;
    private static final long BLUETOOTH_CHECK_INTERVAL = 1 * 1000;

    //0. If bluetooth enable
    //1. start listening indefinitely
    //2. When received the request read the data.
    //3. if tell me your address send address. => 1.
    //4. if transferred money save in the db

    private BluetoothServerSocket mmServerSocket;

    public BluetoothBackgroundService() {
        super("MadMoneyService");
    }

    public void startBTBackgroundService(Context context, String userAddressId) {
        Intent intent = new Intent(context, BluetoothBackgroundService.class);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BluetoothSocket socket = null;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        while (RUNNING) {
            while (!btAdapter.isEnabled()) {
                // TODO: 11/10/2015 change to message handler
                SystemClock.sleep(BLUETOOTH_CHECK_INTERVAL);
            }
            BluetoothServerSocket tmp = null;
            try {
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(CONNECTION_NAME, MAD_MONEY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Socket Type: listen() failed", e);
                break;
            }
            mmServerSocket = tmp;
            while (btAdapter.isEnabled()) {
                try {
                    socket = mmServerSocket.accept();
                    if (socket != null) {
                        mmServerSocket.close();
                        afterReceiveSocketConnection(socket, socket.getRemoteDevice());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type:  accept() failed", e);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean stopService(Intent name) {
        RUNNING = false;
        return super.stopService(name);
    }

    private void afterReceiveSocketConnection(BluetoothSocket socket, BluetoothDevice remoteDevice) {

        InputStream inStream = null;

        try {
            inStream = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
            return;
        }
        int bufferSize = 990;
        byte[] buffer = new byte[bufferSize];

        try {
            int bytesRead = -1;
            String message = "";

            while (true) {

                bytesRead = inStream.read(buffer, 0, bufferSize);

                if (bytesRead != -1) {
                    while ((bytesRead == bufferSize) && (buffer[bufferSize - 1] != 0)) {

                        message = message + new String(buffer, 0, bytesRead);

                        bytesRead = inStream.read(buffer);
                    }
                    message = message + new String(buffer, 0, bytesRead);

                    inStream.close();

                    socket.close();

                    receiveNotification(message, remoteDevice);

                    break;
                }
            }
        } catch (IOException e) {
            Log.d("Error while reading ", e.getMessage());
        }
    }

    private void receiveNotification(String receivedData, BluetoothDevice remoteDevice) {
        if (receivedData.contains(com.payment.sujan.madmoney.Connectors.Constants.TELL_ME_YOUR_ADDRESS)) {

            sendMyAddress(remoteDevice);

            return;
        } else if (receivedData.contains(com.payment.sujan.madmoney.Connectors.Constants.MY_ADDRESS)) {
            Intent intent = new Intent();
            intent.setAction(com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants.ACTION_ADDRESS_RECIEVED);
            intent.putExtra(com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants.DEVICE_ADDRESS, remoteDevice.getAddress());
            intent.putExtra(com.payment.sujan.madmoney.SharedConstants.SharedBrodConstants.RESPONSE_DATA, receivedData);
            sendBroadcast(intent);
        } else {
            com.payment.sujan.madmoney.Utility.MoneyStore.storeBluetoothTransferMoney(getApplicationContext(), receivedData);

            showMoneyTransferNotification();
        }
    }

    private void showMoneyTransferNotification() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("MadMoney Transaction")
                        .setContentText("Congratulation! Money has been transferred.");

        Intent resultIntent = new Intent(this, com.payment.sujan.madmoney.MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(com.payment.sujan.madmoney.MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(12, mBuilder.build());
    }

    private void sendMyAddress(BluetoothDevice remoteDevice) {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String userAddress = prefs.getString(com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants.USER_ADDRESS_ID, null);

        String data = com.payment.sujan.madmoney.Connectors.Constants.MY_ADDRESS + ":" + userAddress;

        transferData(remoteDevice, data.getBytes());
    }

    private void transferData(BluetoothDevice remoteDevice, byte[] dataToTransfer) {
        final BluetoothSocket socket;

        BluetoothSocket tmp = null;
        try {

            tmp = remoteDevice.createRfcommSocketToServiceRecord(MAD_MONEY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket Type: " + "create() failed", e);
        }
        socket = tmp;
        OutputStream outputStream = null;
        try {
            socket.connect();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }
        try {
            outputStream.flush();

            outputStream.write(dataToTransfer);

//            outputStream.close();

//            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception during write: transferData has been failed", e);
        }

    }
}
