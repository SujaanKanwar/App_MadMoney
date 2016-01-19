package com.payment.sujan.madmoney.Connectors;

import android.os.AsyncTask;
import android.util.Log;

import com.payment.sujan.madmoney.Fragments.UserCreateFragment;

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
 * Created by sujan on 30/9/15.
 */
public class CreateUserConnector {

    public CreateUserConnector(OnTaskComplete onTaskComplete) {
        onTaskCompleteListner = onTaskComplete;
    }

    private static final String LOGGER_TAG = "CreateUserServiceCall";

    private static final String BASE_URL = Constants.BASE_URL;

    private static final String createUserUrl = BASE_URL + "/CreateUser";

    private static final boolean isDummy = false;

    private OnTaskComplete onTaskCompleteListner = null;

    public void createUser(String data) {
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
                String strTemp ="";
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
            return createDummyResponse();
        }
        return outputStr;
    }

    private String createDummyResponse() {
        StringBuilder builder = new StringBuilder();
        builder.append("{" +
                "  \"APKTreeArray\": [" +
                "    {" +
                "      \"PublicKey\": \"<RSAKeyValue><Modulus>peHYmS0SEOKvpZuXO5Fxe3nxwn7wzfa84pv0u8ePM8I6kYp9BfItM1CtqeF65OaB1WAL3S13uYKNhPqqFYhQ3lU+SfmZ5E+uoe70V7rmJsoNeCCdLrFwSBwGJjbt5GxjJHdtLt4Y9+1YV52E5MASZ/DY4pxPmrRRcGPQ4IyKW9E=</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>\"," +
                "      \"SiblingIndex\": -1," +
                "      \"Value\": \"WORLD\"" +
                "    }," +
                "    {" +
                "      \"PublicKey\": null," +
                "      \"SiblingIndex\": -1," +
                "      \"Value\": \"in\"" +
                "    }," +
                "    {" +
                "      \"PublicKey\": null," +
                "      \"SiblingIndex\": -1," +
                "      \"Value\": \"MH\"" +
                "    }," +
                "    {" +
                "      \"PublicKey\": null," +
                "      \"SiblingIndex\": -1," +
                "      \"Value\": \"Pune\"" +
                "    }," +
                "    {" +
                "      \"PublicKey\": null," +
                "      \"SiblingIndex\": -1," +
                "      \"Value\": \"kharadi\"" +
                "    }," +
                "    {" +
                "      \"PublicKey\": \"<RSAKeyValue><Modulus>sOBbxKGwzDvOXijfzk9sAt5jUPPspiKKrbJZw4mVSaref69TcgxFbFsFfw8yBO1dTfH/hux6HaCCiDIex5epxpUl9refieShjf35stDtfJwf8SejZrLtodq6tgBbW20pUm8YlVwZgaru4yQjlynZmHASY1/zUKdozgE2xWOlsUk=</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>\"," +
                "      \"SiblingIndex\": 6," +
                "      \"Value\": \"f14065a15dc04891bc6e3d6a87e89e7d-5\"" +
                "    }," +
                "    {" +
                "      \"PublicKey\": \"<RSAKeyValue><Modulus>sOBbxKGwzDvOXijfzk9sAt5jUPPspiKKrbJZw4mVSaref69TcgxFbFsFfw8yBO1dTfH/hux6HaCCiDIex5epxpUl9refieShjf35stDtfJwf8SejZrLtodq6tgBbW20pUm8YlVwZgaru4yQjlynZmHASY1/zUKdozgE2xWOlsUk=</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>\"," +
                "      \"SiblingIndex\": 7," +
                "      \"Value\": \"d789e8ff7c1a4d4bb18b3f2765209a1d-6\"" +
                "    }," +
                "    {" +
                "      \"PublicKey\": \"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDHx9cGkjEcHjThFU2vZIBbQA4REO5GM1C/5Q/nJirwlxe1BraY2QNDlzw7wEBpsMiR5cmHsrUiKTWp+zIn1h5EfJxB5QwfH4Aicxi8amGDSqpeGjO3ULEPHpkN9qzd0sVLJhOjWBa8SRxQnEzCebHrXw2g9lW/xv1ZD2K8wwLHOQIDAQAB\"," +
                "      \"SiblingIndex\": -1," +
                "      \"Value\": \"d053b568730444d0b67d4145b9d243c9-7\"" +
                "    }" +
                "  ]," +
                "  \"FailureDesc\": \"\"," +
                "  \"IsSuccess\": true," +
                "  \"UserAddressId\": \"in/MH/Pune/kharadi/d053b568730444d0b67d4145b9d243c9-7\"" +
                "}");
        return builder.toString();
    }

    private class HttpPostAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return post(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            onTaskCompleteListner.onCreateUserServiceResponse(result);
        }
    }

    public interface OnTaskComplete {
        void onCreateUserServiceResponse(String s);
    }
}
