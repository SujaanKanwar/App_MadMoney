package com.example.sujan.madmoney.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sujan.madmoney.R;

public class OnlineRFragment extends Fragment {


    public OnlineRFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Online fragment", Toast.LENGTH_LONG).show();
        return inflater.inflate(R.layout.fragment_online, container, false);
    }


}
