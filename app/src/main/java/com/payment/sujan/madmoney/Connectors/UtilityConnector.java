package com.payment.sujan.madmoney.Connectors;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sujan on 11/9/2015.
 */
public class UtilityConnector {
    private String BASE_URL = Constants.BASE_URL;
    private String GET_APK_FILE_URL = "/APKFile";
    private String GET_TEL_LOCATION_URL = "/gettlocations";
    private String SEND_DISCOVERED_LOCATION_URL = "/discoveredLocations";
    private String TRANS_MONEY = "/DepositMoneyToAccount";

    public String getAPKFileFromServer() {

        String url = BASE_URL + GET_APK_FILE_URL;

        return post(url, null);
    }

    public String getTeleportingPositions(String data) {
        String url = BASE_URL + GET_TEL_LOCATION_URL;

        return post(url, data);
    }

    public String sendDiscoveredLocations(String data) {
        String url = BASE_URL + SEND_DISCOVERED_LOCATION_URL;
        return post(url,data);
    }

    private String post(String url, String postParameters) {
        HttpURLConnection urlConnection = null;
        String outputStr = "";
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
        return outputStr;
    }

    public boolean transferMoney(JSONObject data) {
        String url = BASE_URL + TRANS_MONEY;

        String response = post(url, data.toString());

        return response.equals("true");
    }

}
