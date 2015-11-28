package com.payment.sujan.madmoney.Fragments;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.AppData.OnlineRecyclerAdaptor;
import com.payment.sujan.madmoney.AppData.UserAddress;
import com.payment.sujan.madmoney.Connectors.Constants;
import com.payment.sujan.madmoney.Fragments.*;
import com.payment.sujan.madmoney.Fragments.BucketFragment;
import com.payment.sujan.madmoney.Fragments.onAddNewAddressListener;
import com.payment.sujan.madmoney.R;
import com.payment.sujan.madmoney.Resources.DBAddressBook;
import com.payment.sujan.madmoney.Services.UtilityService;

import java.util.List;

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

        ImageButton addNewAddressButton = (ImageButton) getActivity().findViewById(R.id.online_add_address_btn);

        addNewAddressButton.setOnClickListener(new onAddNewAddressListener(getActivity().getApplicationContext(), getFragmentManager()));
    }

    @Override
    public void onStart() {
        super.onStart();
        initialise();
    }

    private void initialise() {
        getAndSetAddressOnlineAddressList();

        OnlineRecyclerAdaptor adaptor = new OnlineRecyclerAdaptor(new OnDragListener(), new OnClickListener(getActivity().getApplicationContext(), getFragmentManager()));

        recyclerView.setAdapter(adaptor);
    }

    private void getAndSetAddressOnlineAddressList() {
        DBAddressBook dbAddressBook = new DBAddressBook(getActivity().getApplicationContext());
        List<UserAddress> addressList = dbAddressBook.selectUserAddress();
        GlobalStatic.setOnlineUserAddressList(addressList);
    }

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
                    String id = (String) v.getTag();
                    TextView totalAmountTextView = (TextView) getActivity().findViewById(R.id.totalMoney);
                    totalAmountTextView.setText(" ");
                    String userAddressId = getUserAddressFromAddressBook(id);

                    if (userAddressId != null)
                        UtilityService.sendMoney(getActivity().getApplicationContext(), userAddressId);

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
            int addressId = Integer.parseInt((String) v.getTag());
            EditOnlineAddressDialog editOnlineAddressDialog = new EditOnlineAddressDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("addressId", addressId);
            editOnlineAddressDialog.setArguments(bundle);
            editOnlineAddressDialog.show(fragmentManager, "EDIT_ADDRESS");
        }
    }

    private String getUserAddressFromAddressBook(String id) {
        List<UserAddress> addressList = GlobalStatic.getOnlineUserAddressList();
        for (UserAddress temp : addressList) {
            if (temp.getId().equals(id))
                return temp.getUserAddressId();
        }
        return null;
    }
}
