package com.example.sujan.madmoney.Connectors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by sujan on 11/10/15.
 */
public class BluetoothConnector {
    private static final UUID MAD_MONEY_UUID = UUID.fromString("61217015-8107-4ed0-9373-b1cfa4ce6494");
    private static final String TAG = "BluetoothConnector";

    public static final int STATE_NONE = 0;

    private final BluetoothAdapter btAdapter;
    private ConnectThread deviceConnectThread;

    private int state;
    private Handler handler;
    private byte[] dataToTransfer;

    public int getState() {
        return state;
    }

    public BluetoothConnector(Handler handler, BluetoothAdapter btAdapter) {
        setState(STATE_NONE);

        this.btAdapter = btAdapter;

        this.handler = handler;
    }

    public synchronized void transferData(BluetoothDevice device, byte[] data, int transType) {
        if (deviceConnectThread != null) {
            deviceConnectThread.cancel();
            deviceConnectThread = null;
        }

        this.dataToTransfer = data;

        deviceConnectThread = new ConnectThread(device, transType);

        deviceConnectThread.start();
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private final int transType;

        public ConnectThread(BluetoothDevice device, int transType) {
            this.device = device;
            this.transType = transType;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MAD_MONEY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + "create() failed", e);
            }
            socket = tmp;
        }

        public void run() {
            setName("ConnectThread");

            btAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + " socket during connection failure", e2);
                }
                connectionFailed(Constants.SENDING);
                return;
            }

            synchronized (BluetoothConnector.this) {
                deviceConnectThread = null;
            }
            afterTransSocketConnection(socket, device, transType);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + " socket failed", e);
            }
        }
    }

    private synchronized void afterTransSocketConnection(BluetoothSocket socket, BluetoothDevice device, int transType) {

        if (deviceConnectThread != null) {
            deviceConnectThread.cancel();
            deviceConnectThread = null;
        }
        transferData(socket, device, transType);
    }

    private void transferData(BluetoothSocket socket, BluetoothDevice device, int transType) {
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }
        try {
            outputStream.flush();
            Log.e("DataToTransfer", dataToTransfer.length + "");

//            BTMessage packet = new BTMessage(dataToTransfer.length, dataToTransfer);


            outputStream.write(dataToTransfer, 0, dataToTransfer.length);
//            outputStream.close();
//            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception during write: transferData has been failed", e);
        }
        transNotification(device, transType);
    }

    private void transNotification(BluetoothDevice device, int transType) {
        Message msg;
        if (transType == Constants.TRANS_TYPE_MONEY) {
            msg = handler.obtainMessage(Constants.MESSAGE_MONEY_SENT);
        } else {
            msg = handler.obtainMessage(Constants.MESSAGE_MESSAGE_SENT);
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void connectionFailed(String type) {

        Message msg = handler.obtainMessage(Constants.CONNECTION_FAILED);

        Bundle bundle = new Bundle();

        bundle.putString(Constants.CONN_FAIL_WHILE, type);

        msg.setData(bundle);

        handler.sendMessage(msg);
    }

    private void setState(int state) {
        this.state = state;
    }
}
