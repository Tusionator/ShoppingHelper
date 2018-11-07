package com.example.tusia.shoppinghelper.imagecapturing.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.tusia.shoppinghelper.R
import com.example.tusia.shoppinghelper.imagecapturing.camera.CameraControl
import com.example.tusia.shoppinghelper.utils.Utils
import kotlinx.android.synthetic.main.activity_camera.*


class CameraActivity : AppCompatActivity() {
    var TAG = "CameraActivity"
    private lateinit var cameraControl: CameraControl


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraControl = CameraControl(this, preview_surface)
    }

    override fun onResume() {
        super.onResume()
        Utils.checkOrGetPermissions(this)
        cameraControl.startCamera()
    }

    override fun onPause() {
        super.onPause()
        cameraControl.stopCamera()
    }
}
