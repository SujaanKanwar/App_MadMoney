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
    private static final String TAG = "BluetoothConnector";

    private static final String CONNECTION_NAME = "MadMoneyConnection";

    private static final UUID MAD_MONEY_UUID = UUID.fromString("61217015-8107-4ed0-9373-b1cfa4ce6494");

    private Handler handler;

    private final BluetoothAdapter btAdapter;

    private ConnectThread deviceConnectThread;

    private ReceiveThread deviceReceiveThread;

    private ListenThread listenThread;

    private byte[] dataToTransfer;

    private int state;

    public int getState() {
        return state;
    }

    private void setState(int state) {
        this.state = state;
    }


    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    public BluetoothConnector(Handler handler, BluetoothAdapter btAdapter) {
        setState(STATE_NONE);

        this.btAdapter = btAdapter;

        this.handler = handler;

        startListeningService();
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

                tmp = device.createInsecureRfcommSocketToServiceRecord(MAD_MONEY_UUID);
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
            outputStream.write(dataToTransfer);
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


    public void startListeningService() {
        int state = getState();

        if (state == STATE_NONE || state == STATE_LISTEN) {
            if (listenThread != null) {
                listenThread.cancel();
                listenThread = null;
            }
            if (deviceReceiveThread != null) {
                deviceReceiveThread.cancel();
                deviceReceiveThread = null;
            }
            setState(STATE_LISTEN);

            listenThread = new ListenThread();

            listenThread.start();
        }
    }


    private class ListenThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public ListenThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(CONNECTION_NAME, MAD_MONEY_UUID);

            } catch (IOException e) {
                Log.e(TAG, "Socket Type: listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;

            while (state != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type:  accept() failed", e);
                    break;
                }
                if (socket != null) {
                    synchronized (BluetoothConnector.this) {
                        switch (state) {
                            case STATE_NONE:
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                afterReceiveSocketConnection(socket, socket.getRemoteDevice());
                                break;
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: ");
        }

        public void cancel() {
            Log.d(TAG, "Socket Type cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type close() of server failed", e);
            }
        }
    }

    private void afterReceiveSocketConnection(BluetoothSocket socket, BluetoothDevice remoteDevice) {

        setState(STATE_CONNECTED);

        deviceReceiveThread = new ReceiveThread(socket, remoteDevice);

        deviceReceiveThread.start();
    }

    private class ReceiveThread extends Thread {

        private BluetoothSocket socket;
        private InputStream inStream;
        private BluetoothDevice device;

        public ReceiveThread(BluetoothSocket socket, BluetoothDevice remoteDevice) {

            this.socket = socket;
            this.device = remoteDevice;
            try {
                inStream = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
        }

        public void run() {

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

                        receiveNotification(message, device);
                        setState(STATE_LISTEN);

                        startListeningService();

                        break;
                    }
                }
            } catch (IOException e) {

                connectionFailed(Constants.RECEIVING);

                Log.d("Error while reading ", e.getMessage());
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private void receiveNotification(String receivedData, BluetoothDevice device) {

        if (receivedData.contains(Constants.TELL_ME_YOUR_ADDRESS)) {
            sendMyAddress(device);
            return;
        }

        Message msg = handler.obtainMessage(Constants.MESSAGE_RECEIVED);

        Bundle bundle = new Bundle();

        bundle.putString(Constants.DATA, receivedData);

        msg.setData(bundle);

        handler.sendMessage(msg);
    }

    private void sendMyAddress(BluetoothDevice device) {

        //GlobalStatic.getUserAddressId()
        String data = Constants.MY_ADDRESS + ":" + "in/MH/Pune/kharadi/d053b568730444d0b67d4145b9d243c9-7";

        this.transferData(device, data.getBytes(), Constants.TRANS_TYPE_MSG);
    }
}
