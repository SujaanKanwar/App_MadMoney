package com.example.sujan.madmoney.Cryptography;

import android.util.Base64;
import android.util.Log;

import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sujan on 13/10/15.
 */
public class AESEncryptionDecryption {

    private static String TAG = "AESEncryptionDecryption";

    public static SecretKeySpec generateKey() {
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error" + e.getMessage());
        }
        return sks;
    }

    public static String Encrypt(SecretKeySpec keySpec, String data) {
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");

            c.init(Cipher.ENCRYPT_MODE, keySpec);

            encodedBytes = c.doFinal(data.getBytes());

        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }
        return bytesToString(encodedBytes);
    }

    public static String Decrypt(SecretKeySpec keySpec, String data) {
        byte[] decodedBytes = null;
        byte[] dataBytes = stringToByte(data);
        try {
            Cipher c = Cipher.getInstance("AES");

            c.init(Cipher.DECRYPT_MODE, keySpec);

            decodedBytes = c.doFinal(dataBytes);

        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }
        return new String(decodedBytes);
    }

    public static String bytesToString(byte[] b) {

        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static byte[] stringToByte(String s) {
        return Base64.decode(s, Base64.NO_WRAP);
    }
}
