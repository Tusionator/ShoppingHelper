package com.example.tusia.shoppinghelper.main

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.tusia.shoppinghelper.R
import com.example.tusia.shoppinghelper.imagecapturing.camera.CameraControl
import com.example.tusia.shoppinghelper.utils.Utils
import android.content.Intent
import com.example.tusia.shoppinghelper.productdetection.Detector
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.main.*


class Main : AppCompatActivity() {
    var TAG = "Main"
    private lateinit var cameraControl: CameraControl
    var SELECT_PHOTO = 1
    var tessOCT: Detector =
        Detector()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
//        setContentView(R.layout.activity_camera)
        // cameraControl = CameraControl(this, preview_surface)


        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, SELECT_PHOTO)
    }

    override fun onResume() {
        super.onResume()
        Utils.checkOrGetPermissions(this)
        //  cameraControl.startCamera()
    }

    override fun onPause() {
        super.onPause()
        //  cameraControl.stopCamera()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == SELECT_PHOTO) {

            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    // Get the URI of the selected file
                    val imageUri = intent.data
                    Log.i(TAG, imageUri.toString())


                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    img_view.setImageBitmap(bitmap)

                    val message = tessOCT.getProductName(bitmap)
                    Log.i(TAG, "message: $message")

                }
            }
            super.onActivityResult(requestCode, resultCode, intent)

        }
    }
}
