package com.example.sujan.madmoney.Utility;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

/**
 * Created by sujan on 30/9/15.
 */
public class KeyPairGeneratorStore {

    private static final String keyAlias = "MadMoneyKeyPair";
    private static final String keyAlgorithm = "RSA";
    private static final String androidKeyStore = "AndroidKeyStore";

    /***
     * API: 19
     * References //http://nelenkov.blogspot.in/2013/08/credential-storage-enhancements-android-43.html
     * @param ctx : getActivity().getApplication().getApplicationContext();
     */
    public static void generateKeyPairAndStoreInKeyStore(Context ctx) {
        try {

            KeyStore keyStore = KeyStore.getInstance(androidKeyStore);
            keyStore.load(null);
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
          // if (keyEntry == null) {
                Calendar notBefore = Calendar.getInstance();
                Calendar notAfter = Calendar.getInstance();

                notAfter.add(Calendar.YEAR, 10);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(ctx)
                        .setAlias(keyAlias)
                        .setSubject(
                                new X500Principal(String.format("CN=%s, OU=%s", "alais",
                                        ctx.getPackageName())))
                        .setSerialNumber(BigInteger.ONE).setStartDate(notBefore.getTime())
                        .setKeySize(1024)
                        .setEndDate(notAfter.getTime()).build();

                KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(keyAlgorithm, androidKeyStore);
                kpGenerator.initialize(spec);
                KeyPair kp = kpGenerator.generateKeyPair();
            //}
        } catch (Exception e) {
            //// TODO: 30/9/15 Add exception logging
            String exce = e.toString();
        }
    }

    public static String getPublicKey(){
        try {
            KeyStore keyStore = KeyStore.getInstance(androidKeyStore);
            keyStore.load(null);
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
            return bytesToString(keyEntry.getCertificate().getPublicKey().getEncoded());

        } catch (Exception e) {
            String exce = e.toString();
            return  null;
        }
    }

    public static PrivateKey getPrivateKey() {
        try {

            KeyStore keyStore = KeyStore.getInstance(androidKeyStore);
            keyStore.load(null);
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
            return keyEntry.getPrivateKey();
        } catch (Exception e) {
            String exce = e.toString();
            return  null;
        }
    }

    public static String bytesToString(byte[] b) {

        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static byte[] stringToByte(String s) {
        return Base64.decode(s, Base64.NO_WRAP);
    }
}
