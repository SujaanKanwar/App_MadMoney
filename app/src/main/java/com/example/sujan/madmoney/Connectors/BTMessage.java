package com.example.sujan.madmoney.Connectors;


import java.io.Serializable;

/**
 * Created by Sujan on 11/24/2015.
 */
public class BTMessage implements Serializable {
    int length;
    byte[] data;
    public BTMessage(int length, byte[] dataToTransfer) {
        this.length =length;
        this.data = dataToTransfer;
    }
}