package com.example.tusia.shoppinghelper.productdetection;

import android.graphics.Bitmap;
import android.os.Environment;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class OCRTesseract implements OCRInterface {
    private TessBaseAPI tessBaseAPI;

    public OCRTesseract() {
        tessBaseAPI = new TessBaseAPI();
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        String language = "eng";
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
            dir.mkdirs();
        tessBaseAPI.init(datapath, language);
    }

    public String getProductName(Bitmap bitmap) {
        tessBaseAPI.setImage(bitmap);
        String result = tessBaseAPI.getUTF8Text();
        return result;
    }

    public void onDestroy() {
        if (tessBaseAPI != null)
            tessBaseAPI.end();
    }

}
