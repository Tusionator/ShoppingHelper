package com.example.tusia.shoppinghelper.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

class Utils {
    companion object {
        private val CAMERA_REQUEST_CODE = 200

        fun checkOrGetPermissions(activity: Activity) {
            if ((ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED
                        )
                &&
                (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                        )
                && (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                        )
            ) {
                return
            }
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST_CODE)
            //TODO don't let the application work without permissions
        }
    }
}