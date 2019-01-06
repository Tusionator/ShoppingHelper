package com.example.tusia.shoppinghelper.productdetection;

import android.graphics.Bitmap;

public class Detector {
    private OCRInterface ocr;

    public Detector() {
        ocr = new OCRTesseract();
    }

    public String getProductName(Bitmap bitmap) {
        return ocr.getProductName(bitmap);
    }
}