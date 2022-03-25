package com.mevron.rides.ridertest.livedetect.crop


data class FacePose(val eulerX: Float, val eulerY: Float, val eulerZ: Float) {
    override fun toString(): String {
        return "FacePose(eulerX=$eulerX, eulerY=$eulerY, eulerZ=$eulerZ)"
    }
}