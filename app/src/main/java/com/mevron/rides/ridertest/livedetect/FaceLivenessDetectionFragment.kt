package com.mevron.rides.ridertest.livedetect

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.face.Face
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.FragmentFacialRecognitionBinding
import com.mevron.rides.ridertest.livedetect.crop.CropAlgorithm
import com.mevron.rides.ridertest.livedetect.crop.processFace
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaceLivenessDetectionFragment() : Fragment() {

    //  val viewModel: PensionViewModel by viewModels()
    private lateinit var binding: FragmentFacialRecognitionBinding

    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var selectedModel = FACE_DETECTION
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var cameraSelector: CameraSelector? = null
    var mBitmap: Bitmap?= null


    var blinkCheck1 = false
    var blinkCheck2 = false
    var _singleFaceCheck = false
    var _blinkCheck = false
    var _lookLeftCheck = false
    var _lookRightCheck = false
    var _smileCheck = false


    var isSingleFaceDetected = MutableLiveData(false)
    var isSmilingDetected = MutableLiveData(false)
    var isBlinking = MutableLiveData(false)
    var isLeftEyeOpen = MutableLiveData(false)
    var isRightEyeOpen = MutableLiveData(false)
    var eyeLookRightMutable = MutableLiveData(false)
    var eyeLookLeftMutable = MutableLiveData(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_facial_recognition, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateSingleFaceCheck()
        updateSmilingCheck()
        updateBlinkCheck()
        updateLeftEyeOpenCheck()
        updateRightEyeOpenCheck()
        updateLookRightEyeCheck()
        updateLookLefttEyeCheck()


     /*   binding.toolbar5.setNavigationOnClickListener {
            findNavController().navigateUp()
        }*/

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(
                viewLifecycleOwner,
                { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    if (allPermissionsGranted()) {
                        println("permission granted")
                        bindAllCameraUseCases()
                    } else {
                        try {
                            if (!allPermissionsGranted()) {
                                runtimePermissions
                            }
                        } catch (exc: Exception) {
                            exc.printStackTrace()
                        }

                    }
                }
            )


    }

    override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()

        imageProcessor?.run { this.stop() }

    }

    override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run { this.stop() }
    }


    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        val binding = binding
        if (!PreferenceUtils.isCameraLiveViewportEnabled(requireContext())) {
            return
        }
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        val targetResolution =
            PreferenceUtils.getCameraXTargetResolution(requireContext(), lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(binding.previewView.surfaceProvider)
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this,
            cameraSelector!!,
            previewUseCase)
    }

    private fun bindAnalysisUseCase() {
        val binding = binding
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor =
            try {
                when (selectedModel) {

                    FACE_DETECTION -> {
                        Log.i(TAG, "Using Face Detector Processor")
                        val faceDetectorOptions =
                            PreferenceUtils.getFaceDetectorOptions(requireContext())


                        FaceDetectorProcessor(requireContext(), faceDetectorOptions).apply {
                            setFaceListener(object : FaceListener {
                                override fun onFaceSuccess(faces: List<Face>, bitmap: Bitmap) {
                                    println("Face ::: ${faces.size}")
                                    if (faces.isEmpty()) {
                                        isSingleFaceDetected.value = false
                                        isSmilingDetected.value = false
                                        isBlinking.value = false
                                        isLeftEyeOpen.value = false
                                        isRightEyeOpen.value = false
                                        blinkCheck1 = false
                                        blinkCheck2 = false
                                        eyeLookLeftMutable.value = false
                                        eyeLookRightMutable.value = false
                                        binding.faceDetectedTxt.visibility = View.INVISIBLE
                                        return
                                    } else {
                                        if (faces.size == 1) {
                                            isSingleFaceDetected.value = true
                                            binding.faceDetectedTxt.visibility = View.VISIBLE

                                            for (face in faces) {
                                                val leftEye =
                                                    "%.2f".format(face.leftEyeOpenProbability)
                                                        .toDouble()
                                                val rightEye =
                                                    "%.2f".format(face.rightEyeOpenProbability)
                                                        .toDouble()
                                                val smile = "%.2f".format(face.smilingProbability)
                                                    .toDouble()

                                                val compareSmile = smile.compareTo(0.7)
                                                val compareRight = rightEye.compareTo(0.10)
                                                val compareLeft = leftEye.compareTo(0.10)


                                                val compareBlinkLeft = leftEye.compareTo(0.15)
                                                val compareBlinkRight = rightEye.compareTo(0.15)
//
//                                                println("Compare LeftA:::::: $leftEye")
//                                                println("Compare RightA:::::: $rightEye")
//
//                                                println("Compare LeftB:::::: $compareLeft")
//                                                println("Compare RightB:::::: $compareRight")

                                                //detected smile
                                                //detect left eye
                                                //detect right eye

//

                                                if (compareSmile == 1) {
                                                    isSmilingDetected.value = true
                                                }
                                                //checking blink..
                                                println("Left:::::::::::::::::$leftEye")
                                                println("Right:::::::::::::::::$rightEye")
                                                //val blinking =
                                                println("::::::::::::::::::::::::${
                                                    leftEye.compareTo(0.04)
                                                }")

                                                //return -1
                                                val leftEyeClose = leftEye.compareTo(0.07)
                                                val rightEyeClose = rightEye.compareTo(0.07)

                                                //return 1
                                                val leftEyeOpen = leftEye.compareTo(0.07)
                                                val rightEyeOpen = rightEye.compareTo(0.07)

                                                //lefteyeclose =-1 meaning the eye is close
                                                println("$leftEyeClose :::::::: $rightEyeClose")

                                                if (leftEyeClose == -1 && rightEyeClose == -1) {
                                                    blinkCheck1 = true
                                                }
                                                if (leftEyeOpen == 1 && rightEyeOpen == 1) {
                                                    blinkCheck2 = true
                                                }

                                                val isBlink = blinkCheck1 && blinkCheck2

                                                println("The image is blink::::::::::::::::::::::::::::$isBlink")
                                                if (isBlink) {
                                                    isBlinking.value = true
                                                }
                                                if (compareLeft > 0) {
                                                    isLeftEyeOpen.value = true
                                                }
                                                if (compareRight > 0) {
                                                    isRightEyeOpen.value = true
                                                }

                                                val isFaceRight = face.headEulerAngleY < -40
                                                val isFaceLeft = face.headEulerAngleY > 35

                                                if (isFaceRight) {
                                                    //look right.........
                                                    println("%%%%%%% Right ${face.headEulerAngleY}")
                                                    eyeLookRightMutable.value = true
                                                }
                                                if (isFaceLeft) {
                                                    //left
                                                    eyeLookLeftMutable.value = true
                                                }


                                                val facez = face.headEulerAngleZ
                                                val facey = face.headEulerAngleY
                                                println("::FaceZ:: $facez")
                                                println("::Facey:: $facey")
                                                val validate1 =
                                                    face.headEulerAngleZ >= -3 && face.headEulerAngleY < 0
                                                val validate2 =
                                                    leftEyeOpen == 1 && rightEyeOpen == 1

                                                val isValidImage = face.headEulerAngleX < 4 &&  face.headEulerAngleX > -4
                                                        && face.headEulerAngleZ < 4 &&  face.headEulerAngleZ > -4 &&
                                                        face.headEulerAngleY < 4 &&  face.headEulerAngleY > -4

                                                //validate2 && !isFaceLeft && !isFaceRight
                                                //println("snap image %%%%%%%%%% $isValidImage")

                                                if (isValidImage) {
                                                    cropImage(bitmap, face)
                                                    break
                                                }
                                            }

                                        } else {
                                            println("Detected:: 2 and greater")
                                            isSingleFaceDetected.value = false
                                            isSmilingDetected.value = false
                                            isBlinking.value = false
                                            isLeftEyeOpen.value = false
                                            isRightEyeOpen.value = false
                                            eyeLookLeftMutable.value = false
                                            eyeLookRightMutable.value = false
                                            blinkCheck1 = false
                                            blinkCheck2 = false
                                            return
                                        }
                                    }
                                }

                            })


                        }


                    }
                    else -> throw IllegalStateException("Invalid model name")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Can not create image processor: $selectedModel", e)
                Toast.makeText(
                    requireContext(),
                    "Can not create image processor: " + e.localizedMessage,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

        val builder = ImageAnalysis.Builder()
        val targetResolution =
            PreferenceUtils.getCameraXTargetResolution(requireContext(), lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(requireContext()),
            ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        binding.graphicOverlay.setImageSourceInfo(imageProxy.width,
                            imageProxy.height,
                            isImageFlipped)
                    } else {
                        binding.graphicOverlay.setImageSourceInfo(imageProxy.height,
                            imageProxy.width,
                            isImageFlipped)
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    imageProcessor!!.processImageProxy(imageProxy, binding.graphicOverlay)
                } catch (e: MlKitException) {
                    Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
        if (cameraSelector != null) {
            cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this,
                cameraSelector!!,
                analysisUseCase)
        }

    }


    private fun cropImage(bitmap: Bitmap, face: Face) {
        //after successful cropping save to server
        // uploadImageToServer(bitmap = bitmap)

        val cropImage = processFace(
            face = face,
            bitmap,
            CropAlgorithm.SQUARE,
            15
        )
        val croppedBitmap = cropImage?.face
        uploadImageToServer(croppedBitmap)
    }

    @SuppressLint("RestrictedApi")
    private fun destroyCamera() {
        imageProcessor?.run { this.stop() }
        // graphicOverlay?.clear()
        cameraProvider?.unbindAll()
        //cameraProvider?.shutdown()
    }

    private fun uploadImageToServer(bitmap: Bitmap? = null) {
        val singleCheck = _singleFaceCheck
        val blinkCheck = _blinkCheck
        val lookLeftCheck = _lookLeftCheck
        val lookRightCheck = _lookRightCheck
        val smileCheck = _smileCheck

        val allValid = singleCheck && blinkCheck && lookLeftCheck && lookRightCheck && smileCheck

        println("All valid $allValid")
        println("Bitmap  $bitmap")

        if (bitmap != null){
            mBitmap = bitmap
            if (allValid) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("key", mBitmap)
                findNavController().navigateUp()

            }
        }

    }

    private fun updateSingleFaceCheck() {
        isSingleFaceDetected.observeForever {
            if (it) {
                binding.livenessCheckParent.singleFaceCheck.visibility = View.VISIBLE
                _singleFaceCheck = true
            } else {
                binding.livenessCheckParent.singleFaceCheck.visibility = View.INVISIBLE
                _singleFaceCheck = false
            }
        }
    }

    private fun updateSmilingCheck() {
        isSmilingDetected.observeForever {
            if (it) {
                binding.livenessCheckParent.smileFaceCheck.visibility = View.VISIBLE
                _smileCheck = true
            } else {
                binding.livenessCheckParent.smileFaceCheck.visibility = View.INVISIBLE
                _smileCheck = false
            }
        }
    }

    private fun updateBlinkCheck() {
        isBlinking.observeForever {
            if (it) {
                binding.livenessCheckParent.blinkFaceCheck.visibility = View.VISIBLE
                _blinkCheck = true
            } else {
                binding.livenessCheckParent.blinkFaceCheck.visibility = View.INVISIBLE
                _blinkCheck = false
            }
        }
    }

    private fun updateLeftEyeOpenCheck() {
        isLeftEyeOpen.observeForever {
            if (it) {
                binding.livenessCheckParent.leftEyeOpenFaceCheck.visibility = View.VISIBLE
            } else {
                binding.livenessCheckParent.leftEyeOpenFaceCheck.visibility = View.INVISIBLE
            }
        }
    }


    private fun updateLookRightEyeCheck() {
        eyeLookRightMutable.observeForever {
            if (it) {
                binding.livenessCheckParent.lookRightFaceCheck.visibility = View.VISIBLE
                _lookRightCheck = true
            } else {
                binding.livenessCheckParent.lookRightFaceCheck.visibility = View.GONE
                _lookRightCheck = false
            }
        }
    }


    fun updateLookLefttEyeCheck() {
        eyeLookLeftMutable.observeForever {
            if (it) {
                binding.livenessCheckParent.lookLeftFaceCheck.visibility = View.VISIBLE
                _lookLeftCheck = true
            } else {
                binding.livenessCheckParent.lookLeftFaceCheck.visibility = View.GONE
                _lookLeftCheck = false
            }
        }
    }


    private fun updateRightEyeOpenCheck() {
        isRightEyeOpen.observeForever {
            if (it) {
                binding.livenessCheckParent.rightEyeOpenFaceCheck.visibility = View.VISIBLE
            } else {
                binding.livenessCheckParent.rightEyeOpenFaceCheck.visibility = View.INVISIBLE
            }
        }
    }


    private val requiredPermissions: Array<String?>
        get() =
            try {
                val info =
                    requireActivity().packageManager.getPackageInfo(requireActivity().packageName,
                        PackageManager.GET_PERMISSIONS)
                val ps = info.requestedPermissions
                if (ps != null && ps.isNotEmpty()) {
                    ps
                } else {
                    arrayOfNulls(0)
                }
            } catch (e: Exception) {
                arrayOfNulls(0)
            }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(requireContext(), permission)) {
                return false
            }
        }
        return true
    }


    private val runtimePermissions: Unit
        get() {
            val allNeededPermissions: MutableList<String?> = ArrayList()
            for (permission in requiredPermissions) {
                if (!isPermissionGranted(requireContext(), permission)) {
                    allNeededPermissions.add(permission)
                }
            }
            if (allNeededPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    allNeededPermissions.toTypedArray(),
                    PERMISSION_REQUESTS
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        Log.i(TAG, "Permission granted!")
        if (allPermissionsGranted()) {
            println("Perm")
            bindAllCameraUseCases()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val FACE_DETECTION = "Face Detection"
        private const val TAG = "CameraXLivePreview"
        private const val PERMISSION_REQUESTS = 1
        private const val STATE_SELECTED_MODEL = "selected_model"

        private fun isPermissionGranted(context: Context, permission: String?): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission!!) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Permission granted: $permission")
                return true
            }
            Log.i(TAG, "Permission NOT granted: $permission")
            return false
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()

    }



}
