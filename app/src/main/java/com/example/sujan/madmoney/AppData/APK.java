package com.example.sujan.madmoney.AppData;

/**
 * Created by sujan on 13/10/15.
 */
public class APK {
    private String publicKey;
    private int siblingIndex;
    private String value;

    public APK(String publicKey, int siblingIndex, String value) {
        this.publicKey = publicKey;
        this.siblingIndex = siblingIndex;
        this.value = value;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public int getSiblingIndex() {
        return siblingIndex;
    }

    public String getValue() {
        return value;
    }

}
