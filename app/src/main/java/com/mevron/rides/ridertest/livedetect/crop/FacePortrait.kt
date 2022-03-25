package com.mevron.rides.ridertest.livedetect.crop

import android.graphics.Bitmap

data class FacePortrait(
    val face: Bitmap,
    val smileProbability: Float,
    val leftEyeOpenProbability: Float,
    val rightEyeOpenProbability: Float,
    val pixelBetweenEyes: Double,
    val faceSizePercentage: Float,
    val facePose: FacePose
) {

    override fun toString(): String {
        return "FacePortrait(face=$face, smileProbability=$smileProbability, leftEyeOpenProbability=$leftEyeOpenProbability, rightEyeOpenProbability=$rightEyeOpenProbability, pixelBetweenEyes=$pixelBetweenEyes, faceSizePercentage=$faceSizePercentage, facePose=$facePose)"
    }
}