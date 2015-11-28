package com.payment.sujan.madmoney.AppData;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.payment.sujan.madmoney.AppData.*;
import com.payment.sujan.madmoney.AppData.UserAddress;
import com.payment.sujan.madmoney.R;

import java.util.List;

/**
 * Created by Sujan on 11/26/2015.
 */
public class OnlineRecyclerAdaptor extends RecyclerView.Adapter<OnlineRecyclerAdaptor.ViewHolder> {


    private List<com.payment.sujan.madmoney.AppData.UserAddress> onlineAddressList = GlobalStatic.getOnlineUserAddressList();
    private View.OnDragListener onDragListener;

    private View.OnClickListener onClickListener;

    public OnlineRecyclerAdaptor(View.OnDragListener onDragListener, View.OnClickListener onClickListener) {
        this.onDragListener = onDragListener;
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

        deviceNameTextView.setText(onlineAddressList.get(position).getUserName());

        frameLayout.setTag(onlineAddressList.get(position).getId());

        frameLayout.setOnDragListener(onDragListener);

        frameLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return onlineAddressList.size();
    }
}
