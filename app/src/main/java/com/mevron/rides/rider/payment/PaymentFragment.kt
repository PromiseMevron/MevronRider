package com.mevron.rides.rider.payment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PaymentFragmentBinding
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.model.getCard.Data
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.geolocation.GeoAPIClient
import com.mevron.rides.rider.remote.geolocation.GeoAPIInterface
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.bitmapFromVector
import com.mevron.rides.rider.util.displayLocationSettingsRequest
import com.mevron.rides.rider.util.getGeoLocation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : Fragment(), OnMapReadyCallback, PaySelected {

    companion object {
        fun newInstance() = PaymentFragment()
    }


    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var binding:PaymentFragmentBinding
    private lateinit var mapView: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var geoDirections: GeoDirectionsResponse
    private lateinit var apiInterface: GeoAPIInterface
    private lateinit var location:Array<LocationModel>
    private lateinit var adapter: PaymentAdapter
    private var data: List<Data> = ArrayList()

    private var uiid:String = ""
    private var method = ""
    private var isCard = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.payment_fragment, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (this::gMap.isInitialized) {
            gMap.clear()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        method = "Cash"
        isCard = false
        binding.mevronPayBottom.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
            RecyclerView.VERTICAL, false)

        location = arguments?.let { PaymentFragmentArgs.fromBundle(it).location }!!
        apiInterface = GeoAPIClient().getClient()?.create(GeoAPIInterface::class.java)!!

        binding.payCash.setOnClickListener {
            if (method.isEmpty()){
                Toast.makeText(context, "Select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val action = PaymentFragmentDirections.actionPaymentFragment2ToConfirmRideFragment(location, isCard, uiid)
            findNavController().navigate(action)
        }
        binding.bqckButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // name = arguments?.let { EmailLoginFragmentArgs.fromBundle(it).name }!!
        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        mapView.getMapAsync(this)

        displayLocationSettingsRequest(binding)
        getCards()
        binding.addVoucher.setOnClickListener {
            binding.mevronPayBottom.payTypeLayout.visibility = View.GONE
            binding.mevronPayBottom.voucherAddLayout.visibility = View.VISIBLE
        }
        binding.mevronPayBottom.doneButton.setOnClickListener {
            if (binding.mevronPayBottom.riderCode.text.toString().isEmpty()){
                Toast.makeText(context, "Enter code", Toast.LENGTH_LONG).show()
            }else{
                binding.mevronPayBottom.payTypeLayout.visibility = View.VISIBLE
                binding.mevronPayBottom.voucherAddLayout.visibility = View.GONE
                binding.addVoucher.visibility = View.GONE
                binding.voucherAdded.visibility = View.VISIBLE
                binding.voucherCode.text = binding.mevronPayBottom.riderCode.text.toString()
            }
        }
    }





    private fun addMarkerToPolyLines() {

        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
        var loc1 = location[0].address
        if (loc1.length > 20){
            loc1 = location[0].address.substring(0..20)
        }

        var loc2 = location[1].address
        if (loc2.length > 20){
            loc2 = location[1].address.substring(0..20)
        }

        val sLl= (startLocation?.lat ?: 0.0) - 0.00025
        val sLlg= (startLocation?.lng ?: 0.0) - 0.00025

        val sLl2= (endLocation?.lat ?: 0.0) + 0.0001
        val sLlg2= (endLocation?.lng ?: 0.0) + 0.0001


        val marker1 =  MarkerOptions()
            .position(LatLng(sLl, sLlg))
            .icon(BitmapDescriptorFactory.fromBitmap(createClusterBitmap(add = loc1, img = R.drawable.ic_driver_pick)))

        val marker2 =  MarkerOptions()
            .position(LatLng(sLl2, sLlg2))
            .icon(BitmapDescriptorFactory.fromBitmap(createClusterBitmap(add = loc2, img = R.drawable.ic_driver_dest)))


        val marker3 =  MarkerOptions()
            .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_pick))

        val marker4 =  MarkerOptions()
            .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_dest))

        gMap.addMarker(marker1)
        gMap.addMarker(marker2)
        gMap.addMarker(marker3)
        gMap.addMarker(marker4)





    }

    private fun createClusterBitmap(add: String, img: Int): Bitmap {
        val cluster: View = LayoutInflater.from(context).inflate(
            R.layout.make_payment_marker,
            null
        )
        val clusterSizeText = cluster.findViewById<View>(R.id.address) as TextView
        clusterSizeText.text = add
        val clusterSizeText2 = cluster.findViewById<View>(R.id.type_image) as ImageView

        clusterSizeText2.setImageResource(img)


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
        return clusterBitmap
    }








    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            gMap = p0
        }
        MapsInitializer.initialize(context?.applicationContext)

        getGeoLocation(location, gMap) {
            geoDirections = it
            addMarkerToPolyLines()
        }

        location = arguments?.let { PaymentFragmentArgs.fromBundle(it).location }!!
        if (location.isNotEmpty()){
            val currentLocation = LatLng(location[0].lat, location[0].lng)
            val cameraPosition = CameraPosition.Builder()
                .bearing(0.toFloat())
                .target(currentLocation)
                .zoom(15.5.toFloat())
                .build()
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }


        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
            != PackageManager.PERMISSION_GRANTED && context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION), Constants.LOCATION_REQUEST_CODE)
            return
        }


        p0?.isMyLocationEnabled = true



    }

    fun getCards(){
        viewModel.getACards().observe(viewLifecycleOwner, androidx.lifecycle.Observer  {
            it.let {  res ->
                when(res){

                    is  GenericStatus.Success ->{

                        adapter = res.data?.success?.data?.let { it1 -> PaymentAdapter(this, it1, 1) }!!
                        data = res.data.success.data
                        binding.mevronPayBottom.recyclerView.adapter = adapter

                    }

                    is  GenericStatus.Error ->{

                        Toast.makeText(context, res.error?.error?.message, Toast.LENGTH_LONG).show()
                    }

                    is GenericStatus.Unaunthenticated -> {

                    }
                }
            }
        })
    }

    fun getLocationProvider(): FusedLocationProviderClient? {
        return activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this)
        }
    }

    override fun selected(pos: Int) {
        findNavController().navigate(R.id.action_paymentFragment2_to_paymentMethodFragment)
    }

    override fun selectedMethod(isCard: Boolean, uiid: String, method: String, pos: Int) {
        this.isCard = isCard
        this.uiid = uiid
        this.method = method
        if (isCard){
            binding.payCash.text = "Pay with Card"
        }else{
            binding.payCash.text = "Pay with Cash"
        }

        adapter = PaymentAdapter(this, data, pos)
        binding.mevronPayBottom.recyclerView.adapter = adapter
    }
}