package com.example.sujan.madmoney.AppData;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.sujan.madmoney.Fragments.OfflineRFragment;
import com.example.sujan.madmoney.R;
import com.example.sujan.madmoney.Resources.DBAddressBook;

import java.util.List;

/**
 * Created by sujan on 10/10/15.
 */
public class OfflineRecyclerAdaptor extends RecyclerView.Adapter<OfflineRecyclerAdaptor.ViewHolder> {

    private List<BluetoothDevice> bluetoothDeviceList = GlobalStatic.getBluetoothDeviceList();
    private View.OnDragListener onDragListener;
    private Context context;
    private List<BTAddress> dbBTAddressBookList;
    private DBAddressBook dbAddressBookStore;
    private View.OnClickListener onClickListener;

    public OfflineRecyclerAdaptor(Context context, View.OnDragListener onDragListener, View.OnClickListener onClickListener) {
        this.onDragListener = onDragListener;
        this.context = context;
        this.dbAddressBookStore = new DBAddressBook(context);
        this.dbBTAddressBookList = dbAddressBookStore.selectBTAddress();
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ViewHolder(View itemView) {

            super(itemView);

            this.view = itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FrameLayout frameLayout = (FrameLayout) holder.view.findViewById(R.id.device_frame);

        TextView deviceNameTextView = (TextView) frameLayout.findViewById(R.id.deviceName);

        int i;
        String deviceName = null, deviceAddress = null;
        for (i = 0; i < dbBTAddressBookList.size(); i++) {
            if (dbBTAddressBookList.get(i).getDeviceAddress().equals(bluetoothDeviceList.get(position).getAddress())) {
                deviceName = dbBTAddressBookList.get(i).getDeviceName();
                deviceAddress = dbBTAddressBookList.get(i).getDeviceAddress();
                break;
            }
        }
        if (deviceName == null) {
            dbAddressBookStore.insertBTAddress(bluetoothDeviceList.get(position).getName(), bluetoothDeviceList.get(position).getAddress());
            deviceName = bluetoothDeviceList.get(position).getName();
            deviceAddress = bluetoothDeviceList.get(position).getAddress();
        }

        //  Get the device list from the db
        //1. Device Tell Me your address
        //2. if returns save into the database
        //3. Show to the UI

        deviceNameTextView.setText(deviceName);

        frameLayout.setTag(deviceAddress);

        frameLayout.setOnDragListener(onDragListener);

        frameLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {

        return bluetoothDeviceList.size();
    }

}




