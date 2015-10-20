package com.example.sujan.madmoney.Connectors;

import android.os.AsyncTask;
import android.util.Log;

import com.example.sujan.madmoney.Fragments.WalletFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by sujan on 5/10/15.
 */
public class FetchMoneyConnector {
    public FetchMoneyConnector(WalletFragment walletFragment) {
        onTaskCompleteListner = walletFragment;
    }

    private static final String LOGGER_TAG = "CreateUserServiceCall";

    private static final String createUserUrl = "http://192.168.0.104/madmoneyservice.svc/fetchmoney";

    private static final boolean isDummy = false;

    private OnTaskComplete onTaskCompleteListner = null;

    public void service(String data) {
        new HttpPostAsyncTask().execute(createUserUrl, data);
    }

    private String post(String url, String postParameters) {
        HttpURLConnection urlConnection = null;
        String outputStr = "";
        if (!isDummy) {
            try {
                URL urlToRequest = new URL(url);
                urlConnection = (HttpURLConnection) urlToRequest.openConnection();
                if (postParameters != null) {
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(postParameters);
                    out.close();
                }
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Service is response is not ok");
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String strTemp = "";
                while (null != (strTemp = br.readLine())) {
                    outputStr += strTemp;
                }
            } catch (Exception e) {
                Log.e("Connector", e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        } else {
            return createDummyResponse(postParameters);
        }
        return outputStr;
    }

    private String createDummyResponse(String data) {
        JSONObject jsonObject = null;
        String decryptedMessage = "";
        StringBuilder builder = new StringBuilder();
        try {
            jsonObject = new JSONObject(data);
            decryptedMessage = jsonObject.getJSONObject("data").getString("DecryptedOTP");
        } catch (JSONException e) {
        }
        if (decryptedMessage.compareTo("") == 0) {
            builder.append("{" +
                    "  \"IsSuccess\": false," +
                    "  \"encryptedOTP\": \"bC8wK4rPNeJ8DUD8zkRrZTZQkz77QGz7u5+P1jsLS/EGiGWQ/fBn5Pm1/8R+OIjFsAYOzTBPRoSb5kGLYrTGGeKDaDrvmW3fj0jOEsxxkA410IuylSbBMxVI/FhKomtKyU4g05Nk8ScjglLJ8C6SAQcP1nNMVrGbYGW2GODZgVg=GPAFWinvpcROyBeTYqCPP6sY0eBWn/sEk96+RAPgRO+TyGZz3vYpkQxlE2h6RPkBT7IP/UnHF80sFHnJ26uc5UvshlgeQ9Ez6jblS+lFXTQGYnLk5N6oizLcfoAYsrb/rrZAnTgvWD6TdOP30oujotXV06AvUqLwj8Y6yxNRbGA=\"," +
                    "  \"moneyList\": []," +
                    "  \"status\": \"OTP_SENT\"" +
                    "}");
        } else {
            builder.append("{" +
                    "  \"IsSuccess\": true," +
                    "  \"encryptedOTP\": \"\"," +
                    "  \"moneyList\": [" +
                    "    {" +
                    "      \"dated\": \"9/26/2015 3:33:55 PM\"," +
                    "      \"hash\": \"20a6b13cd19e1a7631d6f400987fd6f56edcd38241e5c400035160617439af91\"," +
                    "      \"id\": \"02b929cb64b74d76a966178f239e1fcf1\"," +
                    "      \"ownerId\": \"in/MH/Pune/kharadi/2e179ab097924cf2b4ae572b257f6cc2-1\"," +
                    "      \"signature\": \"G/wlSghzGdgLmLPKA8ZsIzUBqK11qo/u32ikGmp/JtXUeAtkGet7Eebq8RLw5gu3lBR0+w2NVjXJckMJCwmeuSy2DBaZp7oKVQ/o42fQaARONZvCfBn5kVNB286URb/yVlEH/5Je3S+BzzpVv1PVOZyh2tKN4/ZsVYHc4sfmw+o=\"," +
                    "      \"value\": 1" +
                    "    }," +
                    "    {" +
                    "      \"dated\": \"9/26/2015 3:33:55 PM\"," +
                    "      \"hash\": \"a989805ee771734315fd57d6a918feb4b9b41508fbbf0db94ce1f1c0ef42c445\"," +
                    "      \"id\": \"000220a284664702938cc8715e64689b2\"," +
                    "      \"ownerId\": \"in/MH/Pune/kharadi/2e179ab097924cf2b4ae572b257f6cc2-1\"," +
                    "      \"signature\": \"Okar+hnjU/EbJi/3GfzYKINsHThxp74ofLFkKI8ttCNjVcfsBl40MxNnFp+bEJ90c6oZMgYWNC+j3UP7jM9jtmkRb592CHhPlpW4D9pStdSkuDHEoVdnKSPS9sZg6TLuf4PS2QPSd2U3oDO18ApYujG0nX8yP1dzxgRy7JWEMk4=\"," +
                    "      \"value\": 2" +
                    "    }," +
                    "    {" +
                    "      \"dated\": \"9/26/2015 3:33:55 PM\"," +
                    "      \"hash\": \"a840fde8ba59acb4fddf0812bdf65fa488febb4c5df1a3ba84288a0f2dd8042a\"," +
                    "      \"id\": \"0b5b095b803846a2ade3c3713cdac4ce5\"," +
                    "      \"ownerId\": \"in/MH/Pune/kharadi/2e179ab097924cf2b4ae572b257f6cc2-1\"," +
                    "      \"signature\": \"Ymh1rH9/FOhc7ZZU3zoXZFWh5B2ZB6oERUnp1Kz9kNmHlnFxTC31IY4pU2E2kPfkjg27TquNG1a3bEgg8QfiA+8ApiXEH0Rmsrl9R7o3fqpLsH46ADb/2B1np3ybDA/G0OXfPhRlqXEnfsyRQher7Lth9ad1b2pR09HF4q1Baps=\"," +
                    "      \"value\": 5" +
                    "    }" +
                    "  ]," +
                    "  \"status\": \"MONEY_SENT\"" +
                    "}");
        }

        return builder.toString();
    }

    private class HttpPostAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return post(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            onTaskCompleteListner.onFetchServiceResponse(result);
        }
    }

    public interface OnTaskComplete {
        void onFetchServiceResponse(String s);
    }
}
