package com.example.sujan.madmoney.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sujan.madmoney.Connectors.Constants;
import com.example.sujan.madmoney.MainActivity;
import com.example.sujan.madmoney.R;
import com.example.sujan.madmoney.Utility.MoneyStore;

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

    private String userAddressId;

    private static boolean RUNNING = true;
    private static final long BLUETOOTH_CHECK_INTERVAL = 10 * 1000;

    //0. If bluetooth enable
    //1. start listening indefinitely
    //2. When received the request read the data.
    //3. if tell me your address send address. => 1.
    //4. if transferred money save in the db

    private BluetoothServerSocket mmServerSocket;

    public BluetoothBackgroundService() {

        super("MadMoneyService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (validateRequest(intent)) return;

        run();
    }

    @Override
    public boolean stopService(Intent name) {
        RUNNING = false;
        return super.stopService(name);
    }

    private void run() {
        BluetoothSocket socket = null;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        while (RUNNING) {

            while (!btAdapter.isEnabled()) {

                SystemClock.sleep(BLUETOOTH_CHECK_INTERVAL);
            }

            BluetoothServerSocket tmp = null;

            try {
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(CONNECTION_NAME, MAD_MONEY_UUID);

            } catch (Exception e) {
                Log.e(TAG, "Socket Type: listen() failed", e);
                continue;
            }
            mmServerSocket = tmp;
            while (btAdapter.isEnabled()) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type:  accept() failed", e);
                    break;
                }
                if (socket != null) {
                    afterReceiveSocketConnection(socket, socket.getRemoteDevice());
                }
            }
        }
    }

    private boolean validateRequest(Intent intent) {
//        Bundle bundle = intent.getExtras();
//
//        userAddressId = bundle.getString("USER_ADDRESS_ID", null);
//
//        if (userAddressId == null) {
//            Log.e(TAG, "USER_ADDRESS_ID is missing in the request");
//            return true;
//        }
//        return false;
        return false;
    }

    private void afterReceiveSocketConnection(BluetoothSocket socket, BluetoothDevice remoteDevice) {

        InputStream inStream = null;

        try {
            inStream = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        try {
            int bytesRead = -1;
            String message = "";

            while (true) {

                bytesRead = inStream.read(buffer);

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
        if (receivedData.contains(Constants.TELL_ME_YOUR_ADDRESS)) {

            sendMyAddress(remoteDevice);

            return;
        } else if(receivedData.contains(Constants.MY_ADDRESS))
        {

        }
        else {
            MoneyStore.storeBluetoothTransferMoney(getApplicationContext(), receivedData);

            showMoneyTransferNotification();
        }
    }

    private void showMoneyTransferNotification() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("MadMoney Transaction")
                        .setContentText("Congratulation! Money has been transferred.");

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);
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

        String data = Constants.MY_ADDRESS + ":" + userAddressId;

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

            outputStream.write(dataToTransfer);

            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception during write: transferData has been failed", e);
        }

    }
}
