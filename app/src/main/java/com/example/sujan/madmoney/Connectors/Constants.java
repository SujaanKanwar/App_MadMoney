package com.example.sujan.madmoney.Connectors;

import java.net.PortUnreachableException;

/**
 * Created by sujan on 12/10/15.
 */
public class Constants {
    public static final int MESSAGE_MESSAGE_SENT = 1;
    public static final int MESSAGE_MONEY_SENT = 2;
    public static final int MESSAGE_RECEIVED = 3;
    public static final int CONNECTION_FAILED = 4;

    public static final int TRANS_TYPE_MSG = 1;
    public static final int TRANS_TYPE_MONEY = 2;

    public static final String DEVICE_NAME = "device_name";
    public static final String DATA = "DATA";
    public static final String CONN_FAIL_WHILE = "conn_fail_while";

    public static final String SENDING = "sending";
    public static final String RECEIVING = "receiving";
    public static final String BUCKET_TRANSFER = "bucket_transfer";
    public static final String TELL_ME_YOUR_ADDRESS = "TELL_ME_YOUR_ADDRESS";
    public static final String MY_ADDRESS = "MY_ADDRESS";

    //Money SharedPrefConstants
    public static class Money {
        public static final String DATED = "dated";
        public static final String ID = "id";
        public static final String OWNERID = "ownerId";
        public static final String SIGNATURE = "signature";
        public static final String VALUE = "value";
    }

    public static class FetchMoneyRequest {
        public static final String INIT = "INIT";
        public static final String DECRYPTED_OTP = "DECRYPTED_OTP";
        public static final String RECEIVED_OK = "RECEIVED_OK";
    }

    public static class FetchMoneyResponse {
        public static final String EMPTY_AC = "EMPTY_AC";
        public static final String OTP_SENT = "OTP_SENT";
        public static final String MONEY_SENT = "MONEY_SENT";
        public static final String OTP_MISMATCHED = "OTP_MISMATCHED";

    }

}
