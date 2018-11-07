package com.example.tusia.shoppinghelper.imagecapturing.camera

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.util.Size
import android.view.Surface
import android.view.TextureView
import java.util.*

class CameraControl(private val context: Context, private val preview_surface: TextureView) {
    //------------------ API ---------------------------------------------
    fun startCamera() {
        openBackgroundThread()
        if (preview_surface.isAvailable) {
            setUpCamera()
            openCamera()
        } else {
            preview_surface.surfaceTextureListener = surfaceTextureListener
        }
    }

    fun stopCamera() {
        closeCamera()
        closeBackgroundThread()
    }
    //--------------------------------------------------------------------

    private val TAG = "CameraControl"
    private val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraFacing = CameraCharacteristics.LENS_FACING_BACK
    private lateinit var previewSize: Size
    private var cameraId = "0"
    private var cameraDevice: CameraDevice? = null


    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            setUpCamera()
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {

        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {

        }
    }

    private fun setUpCamera() {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                    val streamConfigurationMap = cameraCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                    )
                    previewSize = streamConfigurationMap!!.getOutputSizes(SurfaceTexture::class.java)[0]
                    this.cameraId = cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    var backgroundThread: HandlerThread? = null
    var backgroundHandler: Handler? = null

    private fun openBackgroundThread() {
        backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            cameraDevice = null
        }
    }


    var cameraCaptureSession: CameraCaptureSession? = null
    private fun closeCamera() {
        cameraCaptureSession?.close()
        cameraCaptureSession = null

        cameraDevice?.close()
        cameraDevice = null
    }

    private fun closeBackgroundThread() {
        backgroundThread?.quitSafely()
        backgroundThread = null
        backgroundHandler = null
    }

    lateinit var captureRequestBuilder: CaptureRequest.Builder
    lateinit var captureRequest: CaptureRequest

    private fun createPreviewSession() {
        try {
            val surfaceTexture = preview_surface.getSurfaceTexture()
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight())
            val previewSurface = Surface(surfaceTexture)
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(previewSurface)

            cameraDevice!!.createCaptureSession(
                Collections.singletonList(previewSurface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (cameraDevice == null) {
                            return
                        }

                        try {
                            captureRequest = captureRequestBuilder.build()
                            this@CameraControl.cameraCaptureSession = cameraCaptureSession
                            this@CameraControl.cameraCaptureSession!!.setRepeatingRequest(
                                captureRequest,
                                null,
                                backgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {

                    }
                }, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}