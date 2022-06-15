package com.mevron.rides.rider.livedetect

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.hardware.camera2.CameraAccessException
import android.util.Log

internal class FlashlightProvider(private val context: Context) {
    private var mCamera: Camera? = null
    private var parameters: Camera.Parameters? = null
    private var camManager: CameraManager? = null
    fun turnFlashlightOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                var cameraId: String? = null
                if (camManager != null) {
                    cameraId = camManager!!.cameraIdList[0]
                    camManager!!.setTorchMode(cameraId, true)
                }
            } catch (e: CameraAccessException) {
                Log.e(TAG, e.toString())
            }
        } else {
            mCamera = Camera.open()
            parameters = mCamera?.getParameters()
            parameters?.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
            mCamera?.setParameters(parameters)
            mCamera?.startPreview()
        }
    }

    private fun turnFlashlightOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val cameraId: String
                camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                if (camManager != null) {
                    cameraId =
                        camManager!!.cameraIdList[0] // Usually front camera is at 0 position.
                    camManager!!.setTorchMode(cameraId, false)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            mCamera = Camera.open()
            parameters = mCamera?.getParameters()
            parameters?.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
            mCamera?.setParameters(parameters)
            mCamera?.stopPreview()
        }
    }

    companion object {
        private val TAG = FlashlightProvider::class.java.simpleName
    }
}