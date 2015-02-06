package com.example.cobi.ais;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cobi on 03.02.15.
 */
public class XMLReader {


    public static String readXMLFile(InputStream inputStream) {
        Log.d("output ĺsa:  " , "###########  readXMLFile");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException ioE) {
            Log.d("irgendwas  " , "### löuft schief +++ ");
        }
        Log.d("output ĺsa:  " , "#########" + outputStream.toString());
        return outputStream.toString();
    }
}
