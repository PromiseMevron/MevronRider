package com.mevron.rides.rider.home.model

import com.google.gson.annotations.SerializedName

data class GeoDirectionsResponse(
    @SerializedName("geocoded_waypoints") var geoCodedWayPoints: List<GeoCodedWayPoint>?,
    var routes: List<GeoCodeRoute>?,
    var status: String?
)

data class GeoCodedWayPoint(
    @SerializedName("geocoder_status") var geocoderStatus: String?,
    @SerializedName("place_id") var placeId: String?,
    var types: List<String>?
)

data class GeoCodeRoute(
    var bounds: GeoBounds?,
    var copyrights: String?,
    var legs: List<GeoDirections>?,
    var summary: String?
)

data class GeoDirections(
    var distance: GeoDirectionMetric?,
    var duration: GeoDirectionMetric?,
    @SerializedName("end_address") var endAddress: String?,
    @SerializedName("end_location") var endLocation: GeoLocation?,
    @SerializedName("start_address") var startAddress: String?,
    @SerializedName("start_location") var startLocation: GeoLocation?,
    var steps: List<DirectionStep>?
)

data class DirectionStep(
    var distance: GeoDirectionMetric?,
    var duration: GeoDirectionMetric?,
    @SerializedName("end_location") var endLocation: GeoLocation?,
    @SerializedName("start_location") var startLocation: GeoLocation?,
    @SerializedName("html_instructions") var instructions: String?,
    var travelMode: String?,
    var polyline: GeoPolyline?
)

data class GeoDirectionMetric(
    var text: String?,
    var value: Long?
)

data class GeoPolyline(
    var points: String?
)

data class GeoBounds(
    var northeast: GeoLocation?,
    var southwest: GeoLocation?
)

data class GeoLocation(
    var lat: Double?,
    var lng: Double?
)