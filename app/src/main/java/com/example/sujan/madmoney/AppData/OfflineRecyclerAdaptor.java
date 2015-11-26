package com.example.sujan.madmoney.AppData;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.sujan.madmoney.R;

import java.util.List;

/**
 * Created by sujan on 10/10/15.
 */
public class OfflineRecyclerAdaptor extends RecyclerView.Adapter<OfflineRecyclerAdaptor.ViewHolder> {

    private List<BluetoothDevice> bluetoothDeviceList = GlobalStatic.getBluetoothDeviceList();
    private View.OnDragListener onDragListener;

    public OfflineRecyclerAdaptor(View.OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
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

        //  Get the device list from the db
        //1. Device Tell Me your address
        //2. if returns save into the database
        //3. Show to the UI

        deviceNameTextView.setText(bluetoothDeviceList.get(position).getName());

        frameLayout.setTag(bluetoothDeviceList.get(position).getAddress());

        frameLayout.setOnDragListener(onDragListener);
    }

    @Override
    public int getItemCount() {

        return bluetoothDeviceList.size();
    }

}




