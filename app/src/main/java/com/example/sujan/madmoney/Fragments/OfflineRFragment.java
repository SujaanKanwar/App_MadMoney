package com.example.sujan.madmoney.Fragments;


import android.app.Activity;
import android.app.FragmentManager;
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
import android.os.ParcelUuid;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.OfflineRecyclerAdaptor;
import com.example.sujan.madmoney.Connectors.Constants;
import com.example.sujan.madmoney.MainActivity;
import com.example.sujan.madmoney.R;
import com.example.sujan.madmoney.Utility.BTMoneyTransService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OfflineRFragment extends Fragment {

    private BluetoothAdapter btAdapter;
    private List<BluetoothDevice> bluetoothDeviceList;
    private OfflineRecyclerAdaptor offlineRecyclerAdaptor;
    private RecyclerView recyclerView;

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

        return inflater.inflate(R.layout.fragment_offline, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.offline_devices_list_view);

        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {

        super.onStart();

        if (btAdapter == null)
            onDestroy();
        else {

            if (!btAdapter.isEnabled()) {

                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else
                initialise();
        }
    }

    @Override
    public void onDestroy() {
        if (btAdapter != null && btAdapter.isEnabled()) {
            if (btAdapter.isDiscovering())
                btAdapter.cancelDiscovery();
//            btAdapter.disable();
        }

        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK)
                    initialise();

                else {
                    Toast.makeText(getActivity(), "Not able to enable the bluetooth", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void initialise() {

        getAndDisplayPairedOrNewBTDevices();

        offlineRecyclerAdaptor = new OfflineRecyclerAdaptor(getActivity().getApplicationContext(), new OnDragListener(), new OnClickListener(getActivity().getApplicationContext(), getFragmentManager()));

        recyclerView.setAdapter(offlineRecyclerAdaptor);

        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void getAndDisplayPairedOrNewBTDevices() {

        bluetoothDeviceList = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();


        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                //get BT address list from DB
                ParcelUuid[] uuids = device.getUuids();
                int i;
                for (i = 0; i < uuids.length; i++) {
                    if (uuids[i].getUuid().compareTo(UUID.fromString("00001105-0000-1000-8000-00805f9b34fb")) == 0)
                        break;
                }
                if (i != uuids.length)
                    bluetoothDeviceList.add(device);
            }
        }
        GlobalStatic.setBluetoothDeviceList(bluetoothDeviceList);

        doDiscovery();
    }

    private void doDiscovery() {

        if (btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();

        btAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                bluetoothDeviceList.add(device);

                GlobalStatic.setBluetoothDeviceList(bluetoothDeviceList);

                offlineRecyclerAdaptor.notifyItemInserted(bluetoothDeviceList.size() - 1);

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
//                    MoneyStore.restoreMoney(activity.getApplicationContext());
//                    GlobalStatic.setBucketCollection(null);
                    ((MainActivity) getActivity()).refreshMoney();
                    Toast.makeText(getActivity(), "Money have been transferred", Toast.LENGTH_LONG).show();
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
                    }
                    String deviceAddress = (String) v.getTag();

                    BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                    if (GlobalStatic.getBucketCollection() != null) {
                        BTMoneyTransService.sendMoney(getActivity().getApplicationContext(), device);
                        TextView totalAmountTextView = (TextView) getActivity().findViewById(R.id.totalMoney);
                        totalAmountTextView.setText(" ");
                    }
//                        bluetoothUtility.sendMoney(device);

//                    ((MainActivity) getActivity()).refreshMoney();
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

    private class OnClickListener implements View.OnClickListener {
        Context context;
        FragmentManager fragmentManager;

        public OnClickListener(Context applicationContext, FragmentManager fragmentManager) {
            context = applicationContext;
            this.fragmentManager = fragmentManager;
        }

        @Override
        public void onClick(View v) {
            View deviceView = v.findViewById(R.id.deviceName);
            String deviceName = ((TextView) deviceView).getText().toString();
            EditOfflineAddressDialog editOfflineAddressDialog = new EditOfflineAddressDialog();
            Bundle bundle = new Bundle();
            bundle.putString("DEVICE_NAME", deviceName);
            editOfflineAddressDialog.setArguments(bundle);
            editOfflineAddressDialog.show(fragmentManager, "EDIT_ADDRESS");
        }
    }
}