package com.mevron.rides.rider.livedetect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;



import com.google.android.gms.common.images.Size;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.mevron.rides.rider.R;


/** Utility class to retrieve shared preferences. */
public class PreferenceUtils {


    @Nullable
    public static CameraSource.SizePair getCameraPreviewSizePair(Context context, int cameraId) {
        Preconditions.checkArgument(
                cameraId == CameraSource.CAMERA_FACING_BACK
                        || cameraId == CameraSource.CAMERA_FACING_FRONT);
        String previewSizePrefKey;
        String pictureSizePrefKey;
        if (cameraId == CameraSource.CAMERA_FACING_BACK) {

            previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
            pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);
        } else {
            previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
            pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
        }

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return new CameraSource.SizePair(
                    Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
                    Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
        } catch (Exception e) {
            return null;
        }
    }

    @RequiresApi(VERSION_CODES.LOLLIPOP)
    @Nullable
    public static android.util.Size getCameraXTargetResolution(Context context, int lensfacing) {
        Preconditions.checkArgument(
                lensfacing == CameraSelector.LENS_FACING_BACK
                        || lensfacing == CameraSelector.LENS_FACING_FRONT);
        String prefKey =
                lensfacing == CameraSelector.LENS_FACING_FRONT
                        ? context.getString(R.string.pref_key_camerax_rear_camera_target_resolution)
                        : context.getString(R.string.pref_key_camerax_front_camera_target_resolution);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return android.util.Size.parseSize(sharedPreferences.getString(prefKey, null));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean shouldHideDetectionInfo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(R.string.pref_key_info_hide);
        return sharedPreferences.getBoolean(prefKey, false);
    }



    public static FaceDetectorOptions getFaceDetectorOptions(Context context) {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableFaceTracking =
                sharedPreferences.getBoolean(
                        context.getString(R.string.pref_key_live_preview_face_detection_face_tracking), false);
        float minFaceSize =
                Float.parseFloat(
                        sharedPreferences.getString(
                                context.getString(R.string.pref_key_live_preview_face_detection_min_face_size),
                                "0.1"));

        FaceDetectorOptions.Builder optionsBuilder =
                new FaceDetectorOptions.Builder()
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setMinFaceSize(minFaceSize);
        if (enableFaceTracking) {
            optionsBuilder.enableTracking();
        }
        return optionsBuilder.build();
    }



    public static boolean isCameraLiveViewportEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
        return sharedPreferences.getBoolean(prefKey, false);
    }

    private PreferenceUtils() {}
}
