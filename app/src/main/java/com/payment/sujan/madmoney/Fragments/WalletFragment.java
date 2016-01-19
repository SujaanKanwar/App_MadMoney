package com.payment.sujan.madmoney.Fragments;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.payment.sujan.madmoney.Connectors.Constants;
import com.payment.sujan.madmoney.Cryptography.RSAEncryptionDecryption;
import com.payment.sujan.madmoney.R;
import com.payment.sujan.madmoney.Requesters.FetchMoneyRequest;
import com.payment.sujan.madmoney.Connectors.FetchMoneyConnector;
import com.payment.sujan.madmoney.Services.UtilityService;
import com.payment.sujan.madmoney.Cryptography.KeyPairGeneratorStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static android.view.ViewGroup.*;

public class WalletFragment extends Fragment implements com.payment.sujan.madmoney.Connectors.FetchMoneyConnector.OnTaskComplete {

    private com.payment.sujan.madmoney.Connectors.FetchMoneyConnector fetchMoneyConnector;
    private String userAddressId;
    private SharedPreferences setting;
    private String TAG = "FETCHMONEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setting = this.getActivity().getSharedPreferences(com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userAddressId = setting.getString(com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants.USER_ADDRESS_ID, null);

        String lastUpdatedDate = setting.getString(com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants.LAST_UPDATED, null);

        Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();

        if (lastUpdatedDate == null || Math.abs(Date.parse(lastUpdatedDate) - currentLocalTime.getTime()) > 2 * 60 * 1000) {

            fetchMoneyFromServer();
        }
        getAPKFileFromServer(getActivity().getApplicationContext());
    }

    private void getAPKFileFromServer(Context context) {
        UtilityService.getAPKFileFromServer(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        refreshMoney();

        bindRefreshEvent();
    }

    public void fetchMoneyFromServer() {

        FetchMoneyRequest request = new FetchMoneyRequest("", userAddressId, Constants.FetchMoneyRequest.INIT);

        fetchMoneyConnector = new FetchMoneyConnector(this);

        fetchMoneyConnector.service(request.toJSONString());
    }

    @Override
    public void onFetchServiceResponse(String responseString) {
        JSONObject jsonObject;
        JSONArray jsonMoneyArray;
        Boolean isSuccess;
        String encryptedOTP;
        String status;
        try {
            jsonObject = new JSONObject(responseString);
            isSuccess = jsonObject.getBoolean("IsSuccess");
            encryptedOTP = jsonObject.getString("encryptedOTP");
            status = jsonObject.getString("status");
            jsonMoneyArray = jsonObject.getJSONArray("moneyList");
        } catch (JSONException e) {
            return;
        }
        if (!isSuccess) {
            Log.e(TAG, status);
        } else {
            switch (status) {
                case com.payment.sujan.madmoney.Connectors.Constants.FetchMoneyResponse.EMPTY_AC:
                    break;
                case com.payment.sujan.madmoney.Connectors.Constants.FetchMoneyResponse.OTP_SENT:

                    String decryptedOTP = decryptOTP(encryptedOTP);

                    if (decryptedOTP != null) {

                        com.payment.sujan.madmoney.Requesters.FetchMoneyRequest request = new com.payment.sujan.madmoney.Requesters.FetchMoneyRequest(decryptedOTP, userAddressId, com.payment.sujan.madmoney.Connectors.Constants.FetchMoneyRequest.DECRYPTED_OTP);

                        fetchMoneyConnector.service(request.toJSONString());
                    }
                    break;

                case com.payment.sujan.madmoney.Connectors.Constants.FetchMoneyResponse.MONEY_SENT:

                    Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();

                    setting.edit().putString(com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants.LAST_UPDATED, currentLocalTime.toString()).commit();

                    if (com.payment.sujan.madmoney.Utility.MoneyStore.store(this.getActivity().getApplicationContext(), jsonMoneyArray)) {

                        this.refreshMoney();

                        com.payment.sujan.madmoney.Requesters.FetchMoneyRequest request = new com.payment.sujan.madmoney.Requesters.FetchMoneyRequest("", userAddressId, com.payment.sujan.madmoney.Connectors.Constants.FetchMoneyRequest.RECEIVED_OK);

                        fetchMoneyConnector.service(request.toJSONString());
                    }
                    break;

                case com.payment.sujan.madmoney.Connectors.Constants.FetchMoneyResponse.OTP_MISMATCHED:
                    break;
            }
        }
    }

    public void refreshMoney() {

        com.payment.sujan.madmoney.Resources.DBMoneyStore dbMoneyStore = new com.payment.sujan.madmoney.Resources.DBMoneyStore(this.getActivity().getApplicationContext());

        com.payment.sujan.madmoney.AppData.GlobalStatic.setMoneyCollection(dbMoneyStore.retrieveAllMoney());

        com.payment.sujan.madmoney.AppData.GlobalStatic.setBucketCollection(null);

        refreshMoneyView();

        refreshBucketView();

        setTotalBalanceView();
    }


    private void setTotalBalanceView() {
        TextView totalBalance = (TextView) getActivity().findViewById(R.id.main_total_balance);
        totalBalance.setText(com.payment.sujan.madmoney.AppData.GlobalStatic.getTotalBalance() + "");
    }

    private void refreshBucketView() {
        final TextView totalAmountTextView = (TextView) getActivity().findViewById(R.id.totalMoney);
        if (totalAmountTextView != null)
            totalAmountTextView.setText("");
    }

    private String decryptOTP(String encryptedOTP) {

        String decryptedOtp = null;
        try {
            decryptedOtp = new String(RSAEncryptionDecryption.Decrypt(encryptedOTP,
                    KeyPairGeneratorStore.getPrivateKey()), Charset.forName("UTF8"));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return decryptedOtp;
    }

    private void setDragListener(ImageButton moneyObject, final int value) {

        moneyObject.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipData.Item item = new ClipData.Item(new String("" + value));

                String[] mineType = new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData("MONEY_TRANSFER", mineType, item);

                v.startDrag(dragData,
                        new DragShadowBuilder(v),
                        null,
                        0
                );

                return true;
            }
        });
    }

    public void refreshMoneyView() {

        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.horizontalScrollView);
        LinearLayout linearLayout = (LinearLayout) horizontalScrollView.findViewById(R.id.scrollViewLinearLayout);
        linearLayout.removeAllViews();

        HashMap<Integer, List<com.payment.sujan.madmoney.AppData.Money>> moneyCollection = com.payment.sujan.madmoney.AppData.GlobalStatic.getMoneyCollection();

        for (Integer key : moneyCollection.keySet()) {

            linearLayout.addView(generateViewForValue(key, moneyCollection.get(key).size()));
        }
    }

    private View generateViewForValue(int value, int totalNumber) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(160, 160);

        lp.setMargins(20, 0, 20, 10);

        ImageButton moneyObject = new ImageButton(this.getActivity().getApplicationContext());
        moneyObject.setLayoutParams(lp);

        switch (value) {
            case 1:
                moneyObject.setId(R.id.one);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.one));
                break;
            case 2:
                moneyObject.setId(R.id.two);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.two));
                break;
            case 5:
                moneyObject.setId(R.id.five);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.five));
                break;
            case 10:
                moneyObject.setId(R.id.ten);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.ten));
                break;
            case 20:
                moneyObject.setId(R.id.twenty);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.twenty));
                break;
            case 50:
                moneyObject.setId(R.id.fifty);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.fifty));
                break;
            case 100:
                moneyObject.setId(R.id.hundred);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.hundred));
                break;
            case 500:
                moneyObject.setId(R.id.fivehundred);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.fivehundred));
                break;
            case 1000:
                moneyObject.setId(R.id.thousand);
                moneyObject.setBackground(getResources().getDrawable(R.drawable.thousand));
                break;
        }

        setDragListener(moneyObject, value);

        return moneyObject;
    }

    private void bindRefreshEvent() {

        ImageButton refreshWallet = (ImageButton) getActivity().findViewById(R.id.btn_refresh_wallet);

        refreshWallet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMoney();
            }
        });
    }

}
