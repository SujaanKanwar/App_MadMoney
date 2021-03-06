package com.payment.sujan.madmoney.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.payment.sujan.madmoney.R;
import com.payment.sujan.madmoney.Cryptography.KeyPairGeneratorStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.interfaces.RSAPublicKey;


public class UserCreateFragment extends Fragment implements com.payment.sujan.madmoney.Connectors.CreateUserConnector.OnTaskComplete {

    private EditText fName;
    private EditText lName;
    private EditText countryCode;
    private EditText state;
    private EditText city;
    private EditText local;
    private TextView erroMessage;

    private static String APK_FILE_NAME = "apkfile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_create, container, false);
    }

    @Override
    public void onCreateUserServiceResponse(String serviceResponse) {
        Context context = this.getActivity().getApplicationContext();
        try {
            JSONObject jsonObject = new JSONObject(serviceResponse);
            if (jsonObject.getBoolean("IsSuccess")) {
                com.payment.sujan.madmoney.Resources.FileOperations fileOperations = new com.payment.sujan.madmoney.Resources.FileOperations(context, APK_FILE_NAME);
                fileOperations.write(jsonObject.getJSONArray("APKTreeArray").toString());
                SharedPreferences setting = this.getActivity().getSharedPreferences("MadMoney", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();

                String userAddressId = jsonObject.getString("UserAddressId");
                editor.putString("UserAddressId", userAddressId);
                editor.putBoolean("IsUserCreated", true);
                editor.commit();
                com.payment.sujan.madmoney.AppData.GlobalStatic.setUserAddressId(userAddressId);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                com.payment.sujan.madmoney.Fragments.WalletFragment walletFragment = new com.payment.sujan.madmoney.Fragments.WalletFragment();
//                fragmentTransaction.replace(R.id.fragment_container, walletFragment);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error while user creation", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, "User has been created", Toast.LENGTH_SHORT).show();
    }

    public interface XMLClickables {
        void createUser(View view);
    }

    public void createUser(View view) {

        if (fName == null)
            initializeViews();

        if (isEnteredFieldsValid()) {

            KeyPairGeneratorStore.generateKeyPairAndStoreInKeyStore(getActivity().getApplication().getApplicationContext());

            String publicKey = getJSONFormattedPublicKey();

            com.payment.sujan.madmoney.Requesters.CreateUserRequest.Address address = new com.payment.sujan.madmoney.Requesters.CreateUserRequest.Address(countryCode.getText().toString(), state.getText().toString(), city.getText().toString(), local.getText().toString());

            com.payment.sujan.madmoney.Requesters.CreateUserRequest request = new com.payment.sujan.madmoney.Requesters.CreateUserRequest(fName.getText().toString(), lName.getText().toString(), publicKey, address);

            com.payment.sujan.madmoney.Connectors.CreateUserConnector createUserConnector = new com.payment.sujan.madmoney.Connectors.CreateUserConnector(this);

            createUserConnector.createUser(request.toJSON());
        }
    }

    private String getJSONFormattedPublicKey() {
        JSONObject jsonObject = null;
        RSAPublicKey publicKey = KeyPairGeneratorStore.getPublicKey();
        try {
            jsonObject = new JSONObject();
            jsonObject.put("MOD", Base64.encodeToString(stripLeadingZeros(publicKey.getModulus().toByteArray()), Base64.NO_WRAP));
            jsonObject.put("EXP", Base64.encodeToString(publicKey.getPublicExponent().toByteArray(), Base64.NO_WRAP));
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        return jsonObject.toString();
    }
    private byte[] stripLeadingZeros(byte[] a) {
        int lastZero = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                lastZero = i;
            } else {
                break;
            }
        }
        lastZero++;
        byte[] result = new byte[a.length - lastZero];
        System.arraycopy(a, lastZero, result, 0, result.length);
        return result;
    }

    private boolean isEnteredFieldsValid() {
        if (fName.getText().toString().trim().equals("")) {
            erroMessage.setText("");
            erroMessage.setText("Enter First Name");
            return false;
        }
        if (lName.getText().toString().trim().equals("")) {
            erroMessage.setText("");
            erroMessage.setText("Enter Last Name");
            return false;
        }
        if (countryCode.getText().toString().trim().equals("")) {
            erroMessage.setText("");
            erroMessage.setText("Enter Country Name");
            return false;
        }
        if (state.getText().toString().trim().equals("")) {
            erroMessage.setText("");
            erroMessage.setText("Enter State");
            return false;
        }
        if (city.getText().toString().trim().equals("")) {
            erroMessage.setText("");
            erroMessage.setText("Enter City Name");
            return false;
        }
        if (local.getText().toString().trim().equals("")) {
            erroMessage.setText("");
            erroMessage.setText("Enter Area Name");
            return false;
        }
        return true;
    }

    private void initializeViews() {
        fName = (EditText) getActivity().findViewById(R.id.firstName);
        lName = (EditText) getActivity().findViewById(R.id.lastName);
        countryCode = (EditText) getActivity().findViewById(R.id.country);
        state = (EditText) getActivity().findViewById(R.id.state);
        city = (EditText) getActivity().findViewById(R.id.city);
        local = (EditText) getActivity().findViewById(R.id.locale);
        erroMessage = (TextView) getActivity().findViewById(R.id.erro_message);
    }

}
