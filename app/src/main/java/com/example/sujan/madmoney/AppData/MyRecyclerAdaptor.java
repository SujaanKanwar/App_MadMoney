package com.example.sujan.madmoney.AppData;

import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sujan.madmoney.R;

import java.util.List;

/**
 * Created by sujan on 10/10/15.
 */
public class MyRecyclerAdaptor extends RecyclerView.Adapter<MyRecyclerAdaptor.ViewHolder> {

    private List<BluetoothDevice> bluetoothDeviceList = GlobalStatic.getBluetoothDeviceList();
    private View.OnDragListener onDragListener;

    public MyRecyclerAdaptor(View.OnDragListener onDragListener)
    {
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

        deviceNameTextView.setText(bluetoothDeviceList.get(position).getName());

        frameLayout.setTag(bluetoothDeviceList.get(position).getAddress());

        frameLayout.setOnDragListener(onDragListener);
    }

    @Override
    public int getItemCount() {

        return bluetoothDeviceList.size();
    }

}

