package com.payment.sujan.madmoney.RegisterUser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.interfaces.RSAPublicKey;

import com.payment.sujan.madmoney.Connectors.CreateUserConnector;
import com.payment.sujan.madmoney.MainActivity;
import com.payment.sujan.madmoney.R;
import com.payment.sujan.madmoney.Requesters.CreateUserRequest;
import com.payment.sujan.madmoney.Resources.FileOperations;
import com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants;
import com.payment.sujan.madmoney.Cryptography.KeyPairGeneratorStore;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterUserActivity extends AppCompatActivity implements CreateUserConnector.OnTaskComplete {

    private EditText nameView, localityView, passwordView, confirmPasswordView;

    private View progressView;

    private View loginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initViews();

        initRegisterButton();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onCreateUserServiceResponse(String serviceResponse) {
        Context context = this.getApplicationContext();
        boolean status;
        String apkTreeArray, userAddressId;

        try {
            JSONObject jsonObject = new JSONObject(serviceResponse);

            status = jsonObject.getBoolean("IsSuccess");

            apkTreeArray = jsonObject.getJSONArray("APKTreeArray").toString();

            userAddressId = jsonObject.getString("UserAddressId");

        } catch (JSONException e) {
            Toast.makeText(context, "Error while user creation. Try after some time.", Toast.LENGTH_SHORT).show();
            showProgress(false);
            return;
        }
        if (status) {

            storeUserData(apkTreeArray, userAddressId);

            finish();

            Toast.makeText(context, "Congratulations! You have successfully registered.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else
            Toast.makeText(context, "Error while user creation. Try after some time.", Toast.LENGTH_SHORT).show();

        showProgress(false);
    }

    private void storeUserData(String apkTreeArray, String userAddressId) {

        Context context = this.getApplicationContext();

        FileOperations fileOperations = new FileOperations(context, SharedPrefConstants.APK_FILE_NAME);
        fileOperations.write(apkTreeArray);

        SharedPreferences setting = this.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(SharedPrefConstants.USER_NAME, nameView.getText().toString());
        editor.putString(SharedPrefConstants.USER_PASSWORD, nameView.getText().toString());
        editor.putString(SharedPrefConstants.USER_ADDRESS_ID, userAddressId);
        editor.putBoolean(SharedPrefConstants.IS_USER_CREATED, true);
        editor.commit();
    }

    private void attemptLogin() {

        resetErrors();

        String userName = nameView.getText().toString();
        String locality = localityView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        String fName = "", lName = "";
        String[] tempName = userName.split(" ");
        if (tempName.length > 1) {
            fName = tempName[0];
            lName = tempName[tempName.length - 1];
        } else
            fName = userName;

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userName)) {
            nameView.setError(getString(R.string.error_emplty_name));
            focusView = nameView;
            cancel = true;
        } else if (TextUtils.isEmpty(locality)) {
            localityView.setError(getString(R.string.error_emplty_locality));
            focusView = localityView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_empty_password));
            focusView = passwordView;
            cancel = true;
        } else if (password.length() < 3) {
            passwordView.setError(getString(R.string.invalid_password_length));
            focusView = passwordView;
            cancel = true;
        } else if (password.compareTo(confirmPassword) != 0) {
            passwordView.setError(getString(R.string.erro_password_mismatch));
            focusView = passwordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            createUser(locality, fName, lName);
        }
    }

    private void createUser(String locality, String fName, String lName) {

        KeyPairGeneratorStore.generateKeyPairAndStoreInKeyStore(this);

        String publicKey = getJSONFormattedPublicKey();

        CreateUserRequest.Address address = new CreateUserRequest.Address("INDIA", "MH", "PUNE", locality);

        CreateUserRequest request = new CreateUserRequest(fName, lName, publicKey, address);

        CreateUserConnector createUserConnector = new CreateUserConnector(this);

        createUserConnector.createUser(request.toJSON());
    }

    private void resetErrors() {
        nameView.setError(null);
        localityView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);
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

    private void initRegisterButton() {
        Button registerUserButton = (Button) findViewById(R.id.create_account);

        registerUserButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void initViews() {
        nameView = (EditText) findViewById(R.id.userName);
        localityView = (EditText) findViewById(R.id.locality);
        passwordView = (EditText) findViewById(R.id.password);
        confirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }
}

