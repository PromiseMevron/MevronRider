package com.mevron.rides.ridertest.util

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.home.model.GeoDirectionsResponse
import com.mevron.rides.ridertest.home.model.LocationModel
import com.mevron.rides.ridertest.remote.geolocation.GeoAPIClient
import com.mevron.rides.ridertest.remote.geolocation.GeoAPIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun View.hideKeyboard() {
    val hideAction = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    hideAction.hideSoftInputFromWindow(windowToken, 0)
}


fun EditText.getString(): String{
    return this.text.toString()
}

/*fun Fragment.mixpanel(): MixpanelAPI {
    return MixpanelAPI.getInstance(context, "YOUR_TOKEN")
}*/

fun Fragment.bitmapFromVector(id: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(requireContext(), id)

// below line is use to set bounds to our vector drawable.
    vectorDrawable!!.setBounds(
        0,
        0,
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight
    )

// below line is use to create a bitmap for our
// drawable which we have added.
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

// below line is use to add bitmap in our canvas.
    val canvas = Canvas(bitmap)

// below line is use to draw our
// vector drawable in canvas.
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun Fragment.displayLocationSettingsRequest(binding: ViewDataBinding){
    val locationRequest = LocationRequest.create()
    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    locationRequest.interval = 4000
    locationRequest.fastestInterval = 1000
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
    val client = context?.let { LocationServices.getSettingsClient(it) }
    val task = client?.checkLocationSettings(builder.build())
    task?.addOnFailureListener { locationException: java.lang.Exception? ->
        if (locationException is ResolvableApiException) {
            try {
                activity?.let { locationException.startResolutionForResult(it, Constants.LOCATION_REQUEST_CODE) }
            } catch (senderException: IntentSender.SendIntentException) {
                senderException.printStackTrace()
                val snackbar = Snackbar
                    .make(binding.root, "Please enable location setting to use your current address.", Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        displayLocationSettingsRequest(binding)
                    }

                snackbar.show()
                // showErrorMessage(context, constraintLayout, "Please enable location setting to use your current address.",
                //  View.OnClickListener { displayLocationSettingsRequest() }, getString(com.google.android.gms.maps.R.string.retry_text))
            }
        }
    }
}

fun Fragment.getGeoLocation(location: Array<LocationModel>, gMap: GoogleMap, isArrival: Boolean = false, onTrip: Boolean = false, addMarker: (GeoDirectionsResponse) -> Unit){

    val directionsEndpoint = "json?origin=" + "${location[0].lat}" + "," + "${location[0].lng}"+
            "&destination=" + "${location[1].lat}" + "," + "${location[1].lng}" +
            "&sensor=false&units=metric&mode=driving"+ "&key=" + "AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE"
    val call: Call<GeoDirectionsResponse> = GeoAPIClient().getClient()?.create(GeoAPIInterface::class.java)!!.getGeoDirections(directionsEndpoint)
    call.enqueue(object : Callback<GeoDirectionsResponse?> {
        override fun onResponse(call: Call<GeoDirectionsResponse?>?, response: Response<GeoDirectionsResponse?>) {
            if (response.isSuccessful) {
                response.body().let {
                    val directionsPayload = it
                    if (directionsPayload != null) {
                        if (isArrival){
                            plotPolyLinesForDriverArrival(directionsPayload, gMap, addMarker)
                        }
                        else{
                            if (onTrip){
                                plotPolyLinesForOnTrip(directionsPayload, gMap, addMarker)
                            }else{
                                plotPolyLines(directionsPayload, gMap, addMarker)
                            }
                        }


                    }
                    else {

                    }
                }
            }
            else {

            }

        }

        override fun onFailure(call: Call<GeoDirectionsResponse?>, t: Throwable?) {
            call.cancel()
        }
    })
}

fun Fragment.plotPolyLines(geoDirections: GeoDirectionsResponse, gMap: GoogleMap,  addMarker: (GeoDirectionsResponse) -> Unit){
    val steps: ArrayList<LatLng> = ArrayList()
    if (geoDirections.routes.isNullOrEmpty()) {
        return
    }
    addMarker(geoDirections)

    val geoBounds = geoDirections.routes?.get(0)?.bounds
    val geoSteps = geoDirections.routes?.get(0)?.legs?.get(0)?.steps
    geoSteps?.forEach { geoStep ->
        steps.addAll(decodePolyline(geoStep.polyline?.points!!))
    }
    val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
    steps.add(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))

    val builder = LatLngBounds.Builder()
    builder.include(LatLng(geoBounds?.northeast?.lat ?: 0.0, geoBounds?.northeast?.lng ?: 0.0))
    builder.include(LatLng(geoBounds?.southwest?.lat ?: 0.0, geoBounds?.southwest?.lng ?: 0.0))

    val bounds = builder.build()
    val width = resources.displayMetrics.widthPixels
    val height = resources.displayMetrics.heightPixels
    val padding = (width * 0.3).toInt()

    //   val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
    //   gMap.animateCamera(boundsUpdate)
    val rectLine = PolylineOptions().width(8f).color(ContextCompat.getColor(requireContext(), R.color.primary))
    for (step in steps) { rectLine.add(step) }
    // gMap.clear()
    gMap.addPolyline(rectLine)
}



fun Fragment.plotPolyLinesForDriverArrival(geoDirections: GeoDirectionsResponse, gMap: GoogleMap,  addMarker: (GeoDirectionsResponse) -> Unit){
    val steps: ArrayList<LatLng> = ArrayList()
    val steps2: ArrayList<LatLng> = ArrayList()
    if (geoDirections.routes.isNullOrEmpty()) {
        return
    }
    addMarker(geoDirections)

    val geoBounds = geoDirections.routes?.get(0)?.bounds
    val geoSteps = geoDirections.routes?.get(0)?.legs?.get(0)?.steps

    if (geoSteps?.size!! > 2){

        for (i in 0 until (geoSteps.size - 2)){
            steps.addAll(decodePolyline(geoSteps[i].polyline?.points!!))
        }

        for (i in (geoSteps.size - 2) until (geoSteps.size)){
            steps2.addAll(decodePolyline(geoSteps[i].polyline?.points!!))
        }

    }else{
        for (i in 0 until (geoSteps.size)){
            steps2.addAll(decodePolyline(geoSteps[i].polyline?.points!!))
        }
    }

    val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
    val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
    steps2.add(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))

    val builder = LatLngBounds.Builder()
    builder.include(LatLng(geoBounds?.northeast?.lat ?: 0.0, geoBounds?.northeast?.lng ?: 0.0))
    builder.include(LatLng(geoBounds?.southwest?.lat ?: 0.0, geoBounds?.southwest?.lng ?: 0.0))

    val bounds = builder.build()
    val width = resources.displayMetrics.widthPixels
    val height = resources.displayMetrics.heightPixels
    val padding = (width * 0.3).toInt()

    //   val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
    //   gMap.animateCamera(boundsUpdate)
    val pattern = listOf(
       Dash(15F), Gap(12f)
    )

    if (geoSteps.size > 2){
        val rectLine = PolylineOptions().width(8f).color(ContextCompat.getColor(requireContext(), R.color.primary))
        for (step in steps) { rectLine.add(step) }
        gMap.addPolyline(rectLine)
        val marker =  MarkerOptions()
            .position(LatLng(steps[(steps.size - 1)].latitude, steps[(steps.size - 1)].longitude))
            .icon(bitmapFromVector(R.drawable.ic_driver_pick))
        gMap.addMarker(marker)



        val cluster: View = LayoutInflater.from(context).inflate(
            R.layout.make_payment_marker,
            null
        )
        val clusterSizeText = cluster.findViewById<View>(R.id.address) as TextView
        clusterSizeText.text = "Pick-up Spot"
        val clusterSizeText2 = cluster.findViewById<View>(R.id.type_image) as ImageView
        clusterSizeText2.visibility = View.GONE

        //  clusterSizeText.text = clusterSize.toString()
        cluster.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        cluster.layout(0, 0, cluster.measuredWidth, cluster.measuredHeight)
        val clusterBitmap = Bitmap.createBitmap(
            cluster.measuredWidth,
            cluster.measuredHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(clusterBitmap)
        cluster.draw(canvas)

        val marker1 =  MarkerOptions()
            .position(LatLng(steps[(steps.size - 1)].latitude, steps[(steps.size - 1)].longitude))
            .anchor(1.0f,1.05f)
            .icon(BitmapDescriptorFactory.fromBitmap(clusterBitmap))
        gMap.addMarker(marker1)

    }
    val marker3 =  MarkerOptions()
        .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
        .icon(bitmapFromVector( R.drawable.group))
    gMap.addMarker(marker3)

    val rectLine2 = PolylineOptions().width(8f).color(ContextCompat.getColor(requireContext(), R.color.dashColor)).pattern(pattern)
    for (step in steps2) { rectLine2.add(step) }
    gMap.addPolyline(rectLine2)
}


fun Fragment.plotPolyLinesForOnTrip(geoDirections: GeoDirectionsResponse, gMap: GoogleMap,  addMarker: (GeoDirectionsResponse) -> Unit){
    val steps: ArrayList<LatLng> = ArrayList()

    if (geoDirections.routes.isNullOrEmpty()) {
        return
    }
    addMarker(geoDirections)

    val geoBounds = geoDirections.routes?.get(0)?.bounds
    val geoSteps = geoDirections.routes?.get(0)?.legs?.get(0)?.steps
    geoSteps?.forEach { geoStep ->
        steps.addAll(decodePolyline(geoStep.polyline?.points!!))
    }

    val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
    steps.add(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))

    val builder = LatLngBounds.Builder()
    builder.include(LatLng(geoBounds?.northeast?.lat ?: 0.0, geoBounds?.northeast?.lng ?: 0.0))
    builder.include(LatLng(geoBounds?.southwest?.lat ?: 0.0, geoBounds?.southwest?.lng ?: 0.0))

    val bounds = builder.build()
    val width = resources.displayMetrics.widthPixels
    val height = resources.displayMetrics.heightPixels
    val padding = (width * 0.3).toInt()

    //   val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
    //   gMap.animateCamera(boundsUpdate)
    val rectLine = PolylineOptions().width(8f).color(ContextCompat.getColor(requireContext(), R.color.primary))
    for (step in steps) { rectLine.add(step) }
    // gMap.clear()
    gMap.addPolyline(rectLine)




  //  val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
    val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation



    val marker3 =  MarkerOptions()
        .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
        .icon(bitmapFromVector( R.drawable.group))
    gMap.addMarker(marker3)

}



private fun decodePolyline(encoded: String): ArrayList<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0
    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat
        shift = 0
        result = 0
        do {
            b = encoded[index++].toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val position = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
        poly.add(position)
    }
    return poly
}

 fun Fragment.toggleBusyDialog(busy: Boolean, desc: String? = null){
     var mDialog: Dialog? = null
    if(busy){
        if(mDialog == null){
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_busy_layout,null)
          mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
        }else{
            if(!desc.isNullOrBlank()){
                val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_busy_layout,null)
                mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
            }
        }
        mDialog.show()
    }else{
        mDialog?.dismiss()
    }
}


fun Fragment.checkPermission2(){
    if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
        != PackageManager.PERMISSION_GRANTED && context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        } != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_REQUEST_CODE)
        return
    }
}

