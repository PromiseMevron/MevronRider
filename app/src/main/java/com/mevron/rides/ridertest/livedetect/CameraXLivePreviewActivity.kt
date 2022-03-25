package com.mevron.rides.ridertest.livedetect

//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.os.Build.VERSION_CODES
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.*
//import android.widget.AdapterView.OnItemSelectedListener
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.chamsmobile.android.features.onboarding.presentation.ui.facedetection.GraphicOverlay
//import com.chamsmobile.android.features.onboarding.presentation.ui.facedetection.VisionImageProcessor
//import com.google.android.gms.common.annotation.KeepName
//import com.google.mlkit.common.MlKitException
//import iamalive.chamsmobile.com.CropAlgorithm
//import iamalive.chamsmobile.com.processFace
//import com.google.mlkit.vision.face.Face
//import java.util.*
//
///** Live preview demo app for ML Kit APIs using CameraX. */
//@KeepName
//@RequiresApi(VERSION_CODES.LOLLIPOP)
//class CameraXLivePreviewActivity :
//    AppCompatActivity(),
//    ActivityCompat.OnRequestPermissionsResultCallback,
//    OnItemSelectedListener,
//    CompoundButton.OnCheckedChangeListener {
//
//
//    private var previewView: PreviewView? = null
//    private var graphicOverlay: GraphicOverlay? = null
//    private var cameraProvider: ProcessCameraProvider? = null
//    private var previewUseCase: Preview? = null
//    private var analysisUseCase: ImageAnalysis? = null
//    private var imageProcessor: VisionImageProcessor? = null
//    private var needUpdateGraphicOverlayImageSourceInfo = false
//    private var selectedModel = FACE_DETECTION
//    private var lensFacing = CameraSelector.LENS_FACING_FRONT
//    private var cameraSelector: CameraSelector? = null
//
//
//    private var singlePerson: TextView? = null
//    private var smileCheck: TextView? = null
//    private var blinkingCheck: TextView? = null
//    private var leftEyeOpenCheck: TextView? = null
//    private var rightEyeOpenCheck: TextView? = null
//    private var eyeLookLeft: TextView? = null
//    private var eyeLookRight: TextView? = null
//    private var recaptureButton: Button? = null
//    private var imageDetected: ImageView? = null
//
//
//    var blinkCheck1 = false
//    var blinkCheck2 = false
//
//
//    var isSingleFaceDetected = MutableLiveData(false)
//    var isSmilingDetected = MutableLiveData(false)
//    var isBlinking = MutableLiveData(false)
//    var isLeftEyeOpen = MutableLiveData(false)
//    var isRightEyeOpen = MutableLiveData(false)
//    var eyeLookRightMutable = MutableLiveData(false)
//    var eyeLookLeftMutable = MutableLiveData(false)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "onCreate")
//
//        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//        setContentView(R.layout.activity_vision_live_preview)
//        previewView = findViewById(R.id.preview_view)
//
//        singlePerson = findViewById(R.id.single_face_check)
//        smileCheck = findViewById(R.id.smile_face_check)
//        blinkingCheck = findViewById(R.id.blink_face_check)
//        leftEyeOpenCheck = findViewById(R.id.left_eye_open_face_check)
//        rightEyeOpenCheck = findViewById(R.id.right_eye_open_face_check)
//        recaptureButton = findViewById(R.id.recapture_btn)
//        imageDetected = findViewById(R.id.image_detected)
//        eyeLookLeft = findViewById(R.id.look_left_face_check)
//        eyeLookRight = findViewById(R.id.look_right_face_check)
//
//
//        updateSingleFaceCheck()
//        updateSmilingCheck()
//        updateBlinkCheck()
//        updateLeftEyeOpenCheck()
//        updateRightEyeOpenCheck()
//        updateLookRightEyeCheck()
//        updateLookLefttEyeCheck()
//
//
//
//        if (previewView == null) {
//            Log.d(TAG, "previewView is null")
//        }
//        graphicOverlay = findViewById(R.id.graphic_overlay)
//        if (graphicOverlay == null) {
//            Log.d(TAG, "graphicOverlay is null")
//        }
//
//        recaptureButton?.setOnClickListener {
//
//            bindAllCameraUseCases()
//            previewView?.visibility = View.VISIBLE
//            graphicOverlay?.visibility = View.VISIBLE
//            imageDetected?.visibility = View.GONE
//        }
//
//        val options: MutableList<String> = ArrayList()
//
//        options.add(FACE_DETECTION)
//
//        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
//            .get(CameraXViewModel::class.java)
//            .processCameraProvider
//            .observe(
//                this,
//                Observer { provider: ProcessCameraProvider? ->
//                    cameraProvider = provider
//                    if (allPermissionsGranted()) {
//                        bindAllCameraUseCases()
//                    }
//                }
//            )
//
//
//        if (!allPermissionsGranted()) {
//            runtimePermissions
//        }
//
//
//    }
//
//    override fun onSaveInstanceState(bundle: Bundle) {
//        super.onSaveInstanceState(bundle)
//        bundle.putString(STATE_SELECTED_MODEL, selectedModel)
//    }
//
//    @Synchronized
//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
//        // An item was selected. You can retrieve the selected item using
//        // parent.getItemAtPosition(pos)
//        selectedModel = parent?.getItemAtPosition(pos).toString()
//        Log.d(TAG, "Selected model: $selectedModel")
//        bindAnalysisUseCase()
//    }
//
//    override fun onNothingSelected(parent: AdapterView<*>?) {
//        // Do nothing.
//    }
//
//    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
//        if (cameraProvider == null) {
//            return
//        }
//        val newLensFacing =
//            if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
//                CameraSelector.LENS_FACING_BACK
//            } else {
//                CameraSelector.LENS_FACING_FRONT
//            }
//        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
//        try {
//            if (cameraProvider!!.hasCamera(newCameraSelector)) {
//                Log.d(TAG, "Set facing to " + newLensFacing)
//                lensFacing = newLensFacing
//                cameraSelector = newCameraSelector
//                bindAllCameraUseCases()
//                return
//            }
//        } catch (e: CameraInfoUnavailableException) {
//            // Falls through
//        }
//        Toast.makeText(
//            applicationContext,
//            "This device does not have lens with facing: $newLensFacing",
//            Toast.LENGTH_SHORT
//        )
//            .show()
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        bindAllCameraUseCases()
//    }
//
//    override fun onPause() {
//        super.onPause()
//
//        imageProcessor?.run { this.stop() }
//    }
//
//    public override fun onDestroy() {
//        super.onDestroy()
//        imageProcessor?.run { this.stop() }
//    }
//
//    private fun bindAllCameraUseCases() {
//        if (cameraProvider != null) {
//            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
//            cameraProvider!!.unbindAll()
//            bindPreviewUseCase()
//            bindAnalysisUseCase()
//        }
//    }
//
//    private fun bindPreviewUseCase() {
//        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
//            return
//        }
//        if (cameraProvider == null) {
//            return
//        }
//        if (previewUseCase != null) {
//            cameraProvider!!.unbind(previewUseCase)
//        }
//
//        val builder = Preview.Builder()
//        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
//        if (targetResolution != null) {
//            builder.setTargetResolution(targetResolution)
//        }
//        previewUseCase = builder.build()
//        previewUseCase!!.setSurfaceProvider(previewView!!.getSurfaceProvider())
//        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this,
//            cameraSelector!!,
//            previewUseCase)
//    }
//
//    private fun bindAnalysisUseCase() {
//        if (cameraProvider == null) {
//            return
//        }
//        if (analysisUseCase != null) {
//            cameraProvider!!.unbind(analysisUseCase)
//        }
//        if (imageProcessor != null) {
//            imageProcessor!!.stop()
//        }
//        imageProcessor =
//            try {
//                when (selectedModel) {
//
//                    FACE_DETECTION -> {
//                        Log.i(TAG, "Using Face Detector Processor")
//                        val faceDetectorOptions = PreferenceUtils.getFaceDetectorOptions(this)
//
//                        FaceDetectorProcessor(this, faceDetectorOptions).apply {
//                            setFaceListener(object : FaceListener {
//                                override fun onFaceSuccess(faces: List<Face>, bitmap: Bitmap) {
//                                    println("Face ::: ${faces.size}")
//                                    if (faces.isEmpty()) {
//                                        isSingleFaceDetected.value = false
//                                        isSmilingDetected.value = false
//                                        isBlinking.value = false
//                                        isLeftEyeOpen.value = false
//                                        isRightEyeOpen.value = false
//                                        blinkCheck1 = false
//                                        blinkCheck2 = false
//                                        eyeLookLeftMutable.value = false
//                                        eyeLookRightMutable.value = false
//                                        return
//                                    } else {
//                                        if (faces.size == 1) {
//                                            isSingleFaceDetected.value = true
//
//                                            for (face in faces) {
//                                                val leftEye =
//                                                    "%.2f".format(face.leftEyeOpenProbability)
//                                                        .toDouble()
//                                                val rightEye =
//                                                    "%.2f".format(face.rightEyeOpenProbability)
//                                                        .toDouble()
//                                                val smile = "%.2f".format(face.smilingProbability)
//                                                    .toDouble()
//
//
//                                                println("Smile $smile !!!!!!!!!!!!!!!!!!!!!!!!!!!! ${smile.compareTo(0.7)}")
//                                                val compareSmile = smile.compareTo(0.7)
//                                                val compareRight = rightEye.compareTo(0.10)
//                                                val compareLeft = leftEye.compareTo(0.10)
//                                                val compareBlinkLeft = leftEye.compareTo(0.15)
//                                                val compareBlinkRight = rightEye.compareTo(0.15)
////
////                                                println("Compare LeftA:::::: $leftEye")
////                                                println("Compare RightA:::::: $rightEye")
////
////                                                println("Compare LeftB:::::: $compareLeft")
////                                                println("Compare RightB:::::: $compareRight")
//
//                                                //detected smile
//                                                //detect left eye
//                                                //detect right eye
//
////
//
//                                                if (compareSmile == 1) {
//                                                    isSmilingDetected.value = true
//                                                }
//                                                //checking blink..
//                                                println("Left:::::::::::::::::$leftEye")
//                                                println("Right:::::::::::::::::$rightEye")
//                                                //val blinking =
//                                                println("::::::::::::::::::::::::${
//                                                    leftEye.compareTo(0.04)
//                                                }")
//
//                                                //return -1
//                                                val leftEyeClose = leftEye.compareTo(0.07)
//                                                val rightEyeClose = rightEye.compareTo(0.07)
//
//                                                //return 1
//                                                val leftEyeOpen = leftEye.compareTo(0.07)
//                                                val rightEyeOpen = rightEye.compareTo(0.07)
//
//                                                //lefteyeclose =-1 meaning the eye is close
//                                                println("$leftEyeClose :::::::: $rightEyeClose")
//
//                                                if (leftEyeClose == -1 && rightEyeClose == -1) {
//                                                    blinkCheck1 = true
//                                                }
//                                                if (leftEyeOpen == 1 && rightEyeOpen == 1) {
//                                                    blinkCheck2 = true
//                                                }
//
//                                                val isBlink = blinkCheck1 && blinkCheck2
//
//                                                println("The image is blink::::::::::::::::::::::::::::$isBlink")
//                                                if (isBlink) {
//                                                    isBlinking.value = true
//                                                }
//                                                if (compareLeft > 0) {
//                                                    isLeftEyeOpen.value = true
//                                                }
//                                                if (compareRight > 0) {
//                                                    isRightEyeOpen.value = true
//                                                }
//
//                                                if (face.headEulerAngleY <-40) {
//                                                    //look right.........
//                                                    println("%%%%%%% Right ${face.headEulerAngleY}")
//                                                    eyeLookRightMutable.value = true
//                                                }
//                                                if (face.headEulerAngleY > 35) {
//                                                    //left
//                                                    eyeLookLeftMutable.value = true
//                                                }
//
//
//                                                println("angleZ^^^^^^^^^^^^^^^^^^^^^^^^ ${face.headEulerAngleZ}")
//                                                println("angleY^^^^^^^^^^^^^^^^^^^^^^^^ ${face.headEulerAngleY}")
//                                                println("angleX^^^^^^^^^^^^^^^^^^^^^^^^ ${face.headEulerAngleX}")
//
//                                                val validate1 = face.headEulerAngleZ >=-3 && face.headEulerAngleZ < 0
//                                                val validate2 = leftEyeOpen == 1 && rightEyeOpen == 1
//                                                    println("validate1::::::: ${face.headEulerAngleZ}******************  $validate1")
//
//                                                val isValidImage = validate1 && validate2
//                                                println("snap image %%%%%%%%%% $isValidImage")
//
//                                                if (isValidImage) {
//                                                    cropImage(bitmap,face)
//                                                    break
//                                                }
//                                            }
//
//                                        } else {
//                                            println("Detected:: 2 and greater")
//                                            isSingleFaceDetected.value = false
//                                            isSmilingDetected.value = false
//                                            isBlinking.value = false
//                                            isLeftEyeOpen.value = false
//                                            isRightEyeOpen.value = false
//                                            eyeLookLeftMutable.value = false
//                                            eyeLookRightMutable.value = false
//                                            blinkCheck1 = false
//                                            blinkCheck2 = false
//                                            return
//                                        }
//                                    }
//                                }
//
//                            })
//
//
//                        }
//
//
//                    }
//                    else -> throw IllegalStateException("Invalid model name")
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Can not create image processor: $selectedModel", e)
//                Toast.makeText(
//                    applicationContext,
//                    "Can not create image processor: " + e.localizedMessage,
//                    Toast.LENGTH_LONG
//                )
//                    .show()
//                return
//            }
//
//        val builder = ImageAnalysis.Builder()
//        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
//        if (targetResolution != null) {
//            builder.setTargetResolution(targetResolution)
//        }
//        analysisUseCase = builder.build()
//
//        needUpdateGraphicOverlayImageSourceInfo = true
//
//        analysisUseCase?.setAnalyzer(
//            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
//            // thus we can just runs the analyzer itself on main thread.
//            ContextCompat.getMainExecutor(this),
//            ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
//                if (needUpdateGraphicOverlayImageSourceInfo) {
//                    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
//                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
//                    if (rotationDegrees == 0 || rotationDegrees == 180) {
//                        graphicOverlay!!.setImageSourceInfo(imageProxy.width,
//                            imageProxy.height,
//                            isImageFlipped)
//                    } else {
//                        graphicOverlay!!.setImageSourceInfo(imageProxy.height,
//                            imageProxy.width,
//                            isImageFlipped)
//                    }
//                    needUpdateGraphicOverlayImageSourceInfo = false
//                }
//                try {
//                    imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
//                } catch (e: MlKitException) {
//                    Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
//                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        )
//        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this,
//            cameraSelector!!,
//            analysisUseCase)
//    }
//
//    private fun cropImage(bitmap: Bitmap, face: Face) {
//        //after successful cropping save to server
//       // uploadImageToServer(bitmap = bitmap)
//
//      val cropImage =  processFace(
//            face = face,
//            bitmap,
//            CropAlgorithm.SQUARE,
//            15
//        )
//        val croppedBitmap = cropImage?.face
//        uploadImageToServer(croppedBitmap)
//    }
//
//    @SuppressLint("RestrictedApi")
//    private fun destroyCamera() {
//        imageProcessor?.run { this.stop() }
//        // graphicOverlay?.clear()
//        cameraProvider?.unbindAll()
//        //cameraProvider?.shutdown()
//    }
//
//    private fun uploadImageToServer(bitmap: Bitmap? = null) {
//
////        imageDetected?.visibility = View.VISIBLE
////        previewView?.visibility = View.GONE
////        graphicOverlay?.visibility = View.GONE
////        imageDetected?.setImageBitmap(bitmap)
//    }
//
//    private fun updateSingleFaceCheck() {
//        isSingleFaceDetected.observeForever {
//            if (it) {
//                singlePerson?.visibility = View.VISIBLE
//            } else {
//                singlePerson?.visibility = View.INVISIBLE
//            }
//        }
//    }
//
//    private fun updateSmilingCheck() {
//        isSmilingDetected.observeForever {
//            if (it) {
//                smileCheck?.visibility = View.VISIBLE
//            } else {
//                smileCheck?.visibility = View.INVISIBLE
//            }
//        }
//    }
//
//    private fun updateBlinkCheck() {
//        isBlinking.observeForever {
//            if (it) {
//                blinkingCheck?.visibility = View.VISIBLE
//            } else {
//                blinkingCheck?.visibility = View.INVISIBLE
//            }
//        }
//    }
//
//    private fun updateLeftEyeOpenCheck() {
//        isLeftEyeOpen.observeForever {
//            if (it) {
//                leftEyeOpenCheck?.visibility = View.VISIBLE
//            } else {
//                leftEyeOpenCheck?.visibility = View.INVISIBLE
//            }
//        }
//    }
//
//
//    private fun updateLookRightEyeCheck() {
//        eyeLookRightMutable.observeForever {
//            if (it) {
//                eyeLookRight?.visibility = View.VISIBLE
//            } else {
//                eyeLookRight?.visibility = View.GONE
//            }
//        }
//    }
//
//
//    fun updateLookLefttEyeCheck() {
//        eyeLookLeftMutable.observeForever {
//            if (it) {
//                eyeLookLeft?.visibility = View.VISIBLE
//            } else {
//                eyeLookLeft?.visibility = View.GONE
//            }
//        }
//    }
//
//
//    private fun updateRightEyeOpenCheck() {
//        isRightEyeOpen.observeForever {
//            if (it) {
//                rightEyeOpenCheck?.visibility = View.VISIBLE
//            } else {
//                rightEyeOpenCheck?.visibility = View.INVISIBLE
//            }
//        }
//    }
//
//    private val requiredPermissions: Array<String?>
//        get() =
//            try {
//                val info =
//                    this.packageManager.getPackageInfo(this.packageName,
//                        PackageManager.GET_PERMISSIONS)
//                val ps = info.requestedPermissions
//                if (ps != null && ps.isNotEmpty()) {
//                    ps
//                } else {
//                    arrayOfNulls(0)
//                }
//            } catch (e: Exception) {
//                arrayOfNulls(0)
//            }
//
//    private fun allPermissionsGranted(): Boolean {
//        for (permission in requiredPermissions) {
//            if (!isPermissionGranted(this, permission)) {
//                return false
//            }
//        }
//        return true
//    }
//
//    private val runtimePermissions: Unit
//        get() {
//            val allNeededPermissions: MutableList<String?> = ArrayList()
//            for (permission in requiredPermissions) {
//                if (!isPermissionGranted(this, permission)) {
//                    allNeededPermissions.add(permission)
//                }
//            }
//            if (allNeededPermissions.isNotEmpty()) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    allNeededPermissions.toTypedArray(),
//                    PERMISSION_REQUESTS
//                )
//            }
//        }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray,
//    ) {
//        Log.i(TAG, "Permission granted!")
//        if (allPermissionsGranted()) {
//            bindAllCameraUseCases()
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    companion object {
//        private const val FACE_DETECTION = "Face Detection"
//        private const val TAG = "CameraXLivePreview"
//        private const val PERMISSION_REQUESTS = 1
//        private const val STATE_SELECTED_MODEL = "selected_model"
//
//        private fun isPermissionGranted(context: Context, permission: String?): Boolean {
//            if (ContextCompat.checkSelfPermission(context, permission!!) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                Log.i(TAG, "Permission granted: $permission")
//                return true
//            }
//            Log.i(TAG, "Permission NOT granted: $permission")
//            return false
//        }
//    }
//
//    private fun disableCheck() {
//        smileCheck?.visibility = View.INVISIBLE
//        leftEyeOpenCheck?.visibility = View.INVISIBLE
//        rightEyeOpenCheck?.visibility = View.INVISIBLE
//        singlePerson?.visibility = View.INVISIBLE
//        blinkingCheck?.visibility = View.INVISIBLE
//    }
//}
