package com.payment.sujan.madmoney.Resources;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by sujan on 5/10/15.
 */
public class FileOperations {
    
    private File apkFile = null;

    public FileOperations(Context context, String filename) {
        apkFile = new File(context.getFilesDir(), filename);
    }

    public Boolean write(String dataToWrite) {
        try {
            FileWriter fw = new FileWriter(apkFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(dataToWrite);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String read() {
        BufferedReader br = null;

        String response = null;

        try {
            StringBuffer output = new StringBuffer();
            br = new BufferedReader(new FileReader(apkFile.getAbsoluteFile()));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            response = output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
}
