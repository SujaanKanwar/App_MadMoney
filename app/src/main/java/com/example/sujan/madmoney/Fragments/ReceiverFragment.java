package com.example.sujan.madmoney.Fragments;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.MyRecyclerAdaptor;
import com.example.sujan.madmoney.Connectors.Constants;
import com.example.sujan.madmoney.MainActivity;
import com.example.sujan.madmoney.R;
import com.example.sujan.madmoney.Utility.BluetoothUtility;
import com.example.sujan.madmoney.Utility.MoneyStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReceiverFragment extends Fragment {

    BluetoothAdapter btAdapter;

    List<BluetoothDevice> bluetoothDeviceList;

    MyRecyclerAdaptor myRecyclerAdaptor;

    RecyclerView recyclerView;

    BluetoothUtility bluetoothUtility = null;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {

            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();

            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reciever, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.devicesListView);

        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {

        super.onStart();

        if (!btAdapter.isEnabled()) {

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else
            initialise();
    }

    @Override
    public void onStop() {
        if (btAdapter != null && btAdapter.isEnabled()) {
//            if(btAdapter.isDiscovering())
//                btAdapter.cancelDiscovery();
            btAdapter.disable();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        if (btAdapter != null && btAdapter.isEnabled()) {
//            if(btAdapter.isDiscovering())
//                btAdapter.cancelDiscovery();
            btAdapter.disable();
        }
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (btAdapter != null && btAdapter.isEnabled()) {
//            if(btAdapter.isDiscovering())
//                btAdapter.cancelDiscovery();
            btAdapter.disable();
        }

        getActivity().unregisterReceiver(mReceiver);

        super.onDestroy();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK)
                    initialise();

                else {
                    Toast.makeText(getActivity(), "Not able to enable the bluetooth", Toast.LENGTH_SHORT).show();

//                    getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                }
        }
    }


    private void initialise() {

        bluetoothUtility = new BluetoothUtility(getActivity().getApplicationContext(), btMessageHandler, btAdapter);

        getAndDisplayPairedOrNewBTDevices();

        myRecyclerAdaptor = new MyRecyclerAdaptor(new OnDragListener());

        recyclerView.setAdapter(myRecyclerAdaptor);

        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void getAndDisplayPairedOrNewBTDevices() {

        bluetoothDeviceList = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {

                bluetoothDeviceList.add(device);
            }
        }
        GlobalStatic.setBluetoothDeviceList(bluetoothDeviceList);

        doDiscovery();
    }

    private void doDiscovery() {

        if (btAdapter.isDiscovering()) {

            btAdapter.cancelDiscovery();
        }
        btAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                bluetoothDeviceList.add(device);

                GlobalStatic.setBluetoothDeviceList(bluetoothDeviceList);

                myRecyclerAdaptor.notifyItemInserted(bluetoothDeviceList.size() - 1);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
            }
        }
    };

    private final Handler btMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_MONEY_SENT:
                    MoneyStore.restoreMoney(getContext());
                    GlobalStatic.setBucketCollection(null);
//                    ((MainActivity) getActivity()).refreshMoney();
                    Toast.makeText(getActivity(), "Money have been transferred", Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_RECEIVED:
                    Toast.makeText(getActivity(), "Money have been received", Toast.LENGTH_LONG).show();
                    break;
                case Constants.CONNECTION_FAILED:
                    Toast.makeText(getActivity(), "Failure while transaction", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    private class OnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        v.setBackgroundColor(Color.BLUE);

                        v.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setColorFilter(Color.GREEN);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setColorFilter(Color.BLUE);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    v.setBackgroundColor(Color.RED);
                    if (item.getText().toString().compareTo(Constants.BUCKET_TRANSFER) != 0) {
                        Integer amount = Integer.parseInt(item.getText().toString());
                        BucketFragment.addAmountToBucket(amount);
                        ((MainActivity) getActivity()).refreshMoney();
                    }
                    String deviceAddress = (String) v.getTag();

                    BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                    if (GlobalStatic.getBucketCollection() != null)
                        bluetoothUtility.sendMoney(device);

                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    //v.clearColorFilter();
                    v.invalidate();
                    return true;

                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }
            return false;
        }
    }
}
