package com.payment.sujan.madmoney.Cryptography;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Xml;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
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

    public static void generateKeyPairAndStoreInKeyStore(Context ctx) {
        try {

            KeyStore keyStore = KeyStore.getInstance(androidKeyStore);
            keyStore.load(null);
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
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
        } catch (Exception e) {
            //// TODO: 30/9/15 Add exception logging
            String exce = e.toString();
        }
    }

    public static RSAPublicKey getPublicKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(androidKeyStore);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(keyAlias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                return null;
            }
            return (RSAPublicKey) ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();

        } catch (Exception e) {
            String exce = e.toString();
            return null;
        }
    }

    public static PrivateKey getPrivateKey() {
        try {

            KeyStore keyStore = KeyStore.getInstance(androidKeyStore);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(keyAlias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                return null;
            }
            return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        } catch (Exception e) {
            String exce = e.toString();
            return null;
        }
    }

    public static String SignData(String data) {
        try {
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initSign(KeyPairGeneratorStore.getPrivateKey());
            s.update(Charset.forName("UTF-8").encode(data));

            return bytesToString(s.sign());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bytesToString(byte[] b) {

        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static byte[] stringToByte(String s) {
        return Base64.decode(s, Base64.NO_WRAP);
    }
}
