package com.example.sujan.madmoney.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sujan.madmoney.R;

public class OnlineRFragment extends Fragment {

    private RecyclerView recyclerView;

    public OnlineRFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.online_devicesListView);

        recyclerView.setLayoutManager(layoutManager);
    }
    //1. Recycler view will show all the address book from DB
    //2. Add button will add new device with UserAddressId(Without BT device address)
    //3. Offline recycler view will show all the address having BT device address
    //4. Unique primary Id should be based upon user address id.
}
