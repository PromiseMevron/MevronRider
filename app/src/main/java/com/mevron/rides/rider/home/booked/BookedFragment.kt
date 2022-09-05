package com.mevron.rides.rider.home.booked

import android.app.Dialog
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.BookedFragmentBinding
import com.mevron.rides.rider.home.booked.domain.BookedTripEvent
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.payment.ui.CashOutAddFundEventListener
import com.mevron.rides.rider.payment.ui.CashOutAddFundLayout
import com.mevron.rides.rider.payment.ui.CustomRatingEventListener
import com.mevron.rides.rider.payment.ui.CustomRatingLayout
import com.mevron.rides.rider.shared.ui.services.LocationProcessor
import com.mevron.rides.rider.socket.domain.models.MetaData
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.LauncherUtil
import com.mevron.rides.rider.util.bitmapFromVector
import com.mevron.rides.rider.util.getGeoLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class BookedFragment : Fragment(), OnMapReadyCallback, LocationListener,
    CashOutAddFundEventListener, CustomRatingEventListener {

    companion object {
        fun newInstance() = BookedFragment()
    }

    private val viewModel: BookedViewModel by viewModels()
    private lateinit var binding: BookedFragmentBinding
    private lateinit var driverAllBottomSheetBehavior: BottomSheetBehavior<ScrollView>
    private lateinit var onRideBottomSheetBehavior: BottomSheetBehavior<ScrollView>
    private lateinit var emergedBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var reachedBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomView: CashOutAddFundLayout
    private lateinit var ratingView: CustomRatingLayout

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawer: ImageButton
    private lateinit var gMap: GoogleMap
    private lateinit var mapView: SupportMapFragment
    private var mCircle: Circle? = null
    private val locationProcessor = LocationProcessor()
    private var mDialog: Dialog? = null

    var radiusInMeters = 100.0
    var strokeColor = -0x10000 //Color Code you want

    var shadeColor = 0x44ff0000 //opaque red fill

    private lateinit var location: Array<LocationModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.booked_fragment, container, false)

        return binding.root
    }

    private fun cancelRideClicks(){
        binding.cancelReasonLayout.inefficientRoute.setOnClickListener {
            binding.cancelReasonLayout.inefficientRoute.visibility = View.GONE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.VISIBLE

            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaCancelForButtons("inefficient route")
        }

        binding.cancelReasonLayout.inefficientRoute1.setOnClickListener {
            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE

            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaCancelForButtons("")
        }

        binding.cancelReasonLayout.bookedByMistake.setOnClickListener {
            binding.cancelReasonLayout.bookedByMistake.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.VISIBLE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaCancelForButtons("Booked by mistake")
        }

        binding.cancelReasonLayout.bookedByMistake1.setOnClickListener {
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaCancelForButtons("")
        }

        binding.cancelReasonLayout.changeInPlan.setOnClickListener {
            binding.cancelReasonLayout.changeInPlan.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.VISIBLE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaCancelForButtons("Change in plan")
        }

        binding.cancelReasonLayout.changeInPlan1.setOnClickListener {
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            setAlphaCancelForButtons("")
        }

        binding.cancelReasonLayout.other.setOnClickListener {
            binding.cancelReasonLayout.other.visibility = View.GONE
            binding.cancelReasonLayout.other1.visibility = View.VISIBLE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE

            setAlphaCancelForButtons("other")
        }

        binding.cancelReasonLayout.other1.setOnClickListener {
            binding.cancelReasonLayout.other.visibility = View.VISIBLE
            binding.cancelReasonLayout.other1.visibility = View.GONE

            binding.cancelReasonLayout.inefficientRoute.visibility = View.VISIBLE
            binding.cancelReasonLayout.inefficientRoute1.visibility = View.GONE
            binding.cancelReasonLayout.bookedByMistake.visibility = View.VISIBLE
            binding.cancelReasonLayout.bookedByMistake1.visibility = View.GONE
            binding.cancelReasonLayout.changeInPlan.visibility = View.VISIBLE
            binding.cancelReasonLayout.changeInPlan1.visibility = View.GONE

            setAlphaCancelForButtons("")
        }
    }

    private fun bindDriverArrived(data: MetaData?) {
        binding.tipDriverLayout.rootView.visibility = View.GONE
        binding.ratingDriverLayout.rootView.visibility = View.GONE
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.bookedHomeBottom.text1.visibility = View.GONE
        binding.bookedHomeBottom.text2.visibility = View.GONE
        binding.bookedHomeBottom.text11.text = "${data?.driver?.firstName} has arrived"
        binding.bookedHomeBottom.text11.visibility = View.VISIBLE
        binding.bookedHomeBottom.text22.visibility = View.VISIBLE
        binding.verifiedCode.visibility = View.GONE
        binding.bookedHomeBottom.driverLinera.visibility = View.GONE
        generalView(data)
    }

    private fun bindRateDriver(data: MetaData?){
        binding.tipDriverLayout.rootView.visibility = View.VISIBLE
        binding.ratingDriverLayout.rootView.visibility = View.GONE
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.verifiedCode.visibility = View.GONE
        onRideBottomSheetBehavior.isHideable = true
        reachedBottomSheetBehavior.isHideable = false
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        reachedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        onRideBottomSheetBehavior.isHideable = true
        emergedBottomSheetBehavior.isHideable = true
        reachedBottomSheetBehavior.isHideable = true
        driverAllBottomSheetBehavior.isHideable = true
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        emergedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        reachedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
       // binding.tipDriverLayout.rootView.visibility = View.GONE
      //  binding.ratingDriverLayout.rootView.visibility = View.VISIBLE
        binding.tipDriverLayout.nameDisplay.text = "How was your ride with ${data?.driver?.firstName}?"
        binding.tipDriverLayout.rating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
           // binding.tipDriverLayout.rating.rating = rating
            viewModel.rateCustom(rating)
        }
    }

    private fun bindTripStarted(data: MetaData?) {
        binding.tipDriverLayout.rootView.visibility = View.GONE
        binding.ratingDriverLayout.rootView.visibility = View.GONE
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        driverAllBottomSheetBehavior.isHideable = true
        onRideBottomSheetBehavior.isHideable = false
        binding.verifiedCode.visibility = View.GONE
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.bookedOnrideBottom.pickName.text = ""
        binding.bookedOnrideBottom.text22.text = "fix up arriving time"
        binding.bookedOnrideBottom.cardNumber.text = data?.paymentMethod?.type
        binding.bookedOnrideBottom.userRating.text = data?.driver?.rating
        binding.bookedOnrideBottom.pickAddress.text = data?.trip?.pickupAddress
        binding.bookedOnrideBottom.driverName.text = "${data?.driver?.firstName} ${data?.driver?.lastName}"
        binding.bookedOnrideBottom.driverCar.text = "${data?.driver?.vehicle?.type} . ${data?.driver?.vehicle?.plateNumber}"
        if (data?.fare?.isEmpty() == false){
            data.fare.let {
                binding.bookedOnrideBottom.baseFareText.text = it[0].name
                binding.bookedOnrideBottom.baseFare.text == it[0].amount
                if (it.size > 1 ){
                    binding.bookedOnrideBottom.bookingFareText.text = it[1].name
                    binding.bookedOnrideBottom.bookingFare.text = it[1].amount
                }
                if (it.size > 2 ){
                    binding.bookedOnrideBottom.promoText.text = it[2].name
                    binding.bookedOnrideBottom.promo.text = it[2].amount
                }
            }

        }
        generalView(data)

    }

    private fun bindTripCompleted(data: MetaData?) {
        binding.tipDriverLayout.rootView.visibility = View.GONE
        binding.ratingDriverLayout.rootView.visibility = View.GONE
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.verifiedCode.visibility = View.GONE
        onRideBottomSheetBehavior.isHideable = true
        reachedBottomSheetBehavior.isHideable = false
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        reachedBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.bookedArrivedBottom.pickName.text = ""
        binding.bookedArrivedBottom.pickAddress.text = data?.trip?.pickupAddress
        generalView(data)
    }

    private fun bindStartRide(data: MetaData?) {
        binding.tipDriverLayout.rootView.visibility = View.GONE
        binding.ratingDriverLayout.rootView.visibility = View.GONE
        binding.verifiedCode.visibility = View.VISIBLE
        generalView(data)
    }

    private fun setUpDefaultView(data: MetaData?) {
        binding.tipDriverLayout.rootView.visibility = View.GONE
        binding.ratingDriverLayout.rootView.visibility = View.GONE
        generalView(data)
    }

    private fun generalView(data: MetaData?){
        binding.bookedHomeBottom.optNum.text = data?.trip?.verificationCode
        binding.bookedHomeBottom.pickName.text = ""
        binding.bookedHomeBottom.dropName.text = ""
        binding.bookedHomeBottom.pickName.visibility = View.GONE
        binding.bookedHomeBottom.dropName.visibility = View.GONE
        binding.bookedHomeBottom.driverCar.text = "${data?.driver?.vehicle?.type} . ${data?.driver?.vehicle?.plateNumber}"
        binding.bookedHomeBottom.cardNumber.text = data?.paymentMethod?.type
        binding.bookedHomeBottom.userRating.text = data?.driver?.rating
        binding.bookedHomeBottom.pickAddress.text = data?.trip?.pickupAddress
        binding.bookedHomeBottom.dropAddress.text = data?.trip?.destinationAddress
        val timeSplit = data?.trip?.estimatedPickupTime?.toString()?.split(" ")
        //data?.trip?.estimatedPickupTime?.toString()
        if (timeSplit?.isEmpty() == false){
            binding.bookedHomeBottom.time.text = timeSplit[0]
        }else{
            binding.bookedHomeBottom.time.text = data?.trip?.estimatedPickupTime?.toString()
        }

        if ((timeSplit?.size ?: 0) > 1){
            binding.bookedHomeBottom.timeType.text = timeSplit?.get(1)
        }else{
            var time = ((data?.trip?.estimatedPickupTime?.toString() ?: "0").toDoubleOrNull() ?: 0.0).toInt()
            time /= 60
            binding.bookedHomeBottom.time.text = time.toString()
            binding.bookedHomeBottom.timeType.text = "min"
        }

        binding.bookedHomeBottom.driverName.text = "${data?.driver?.firstName} ${data?.driver?.lastName}"
        binding.bookedHomeBottom.driverCar.text = "${data?.driver?.vehicle?.type} . ${data?.driver?.vehicle?.plateNumber}"
        if (data?.fare?.isEmpty() == false){
            data.fare.let {
                binding.bookedHomeBottom.baseFareText.text = it[0].name
                binding.bookedHomeBottom.baseFare.text = it[0].amount
                if (it.size > 1 ){
                    binding.bookedHomeBottom.bookingFareText.text = it[1].name
                    binding.bookedHomeBottom.bookingFare.text = it[1].amount
                }
                if (it.size > 2 ){
                    binding.bookedHomeBottom.promoText.text = it[2].name
                    binding.bookedHomeBottom.promo.text = it[2].amount
                }
            }

        }
    }

    private fun showRideCancellationDialog() {
        val dialog = activity?.let { Dialog(it) }!!
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cancel_dialog)
        val yesBtn = dialog.findViewById(R.id.do_cancel) as MaterialButton
        val noBtn = dialog.findViewById(R.id.dont) as MaterialButton
        yesBtn.setOnClickListener {
            dialog.dismiss()
            binding.cancelReasonLayout.cancelBottom.visibility = View.VISIBLE
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomView = binding.tipDriverLayout.bottomView
        ratingView = binding.tipDriverLayout.ratingCustom
        bottomView.setEventListener(this)
        ratingView.setEventListener(this)
        bottomView.setUpAddFund(requireContext(), title = "Add a Custom Tip")
        ratingView.setUp(requireContext())
        viewModel.getLocationModels()
        viewModel.getDriverLocation()
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                Toast.makeText(context, "back pressed", Toast.LENGTH_LONG).show()
                activity?.finish()
                // in here you can do logic when backPress is clicked
            }
        })


        driverAllBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bookedHomeBottom.bottomSheet)
        onRideBottomSheetBehavior = BottomSheetBehavior.from(binding.bookedOnrideBottom.bottomSheet)
        emergedBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bookedEmergencyBottom.bottomSheet)
        reachedBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bookedArrivedBottom.bottomSheet)

        mapView = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        onRideBottomSheetBehavior.isHideable = true
        emergedBottomSheetBehavior.isHideable = true
        reachedBottomSheetBehavior.isHideable = true
        driverAllBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        onRideBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        emergedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        reachedBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        viewModel.setEvent(BookedTripEvent.RequestTripStatus)

        binding.tipDriverLayout.doneButtonTipRate.setOnClickListener {
            viewModel.tipAndRateCustom()
        }

        binding.tipDriverLayout.first.setOnClickListener {
            binding.tipDriverLayout.first.visibility = View.GONE
            binding.tipDriverLayout.first2.visibility = View.VISIBLE

            binding.tipDriverLayout.second2.visibility = View.GONE
            binding.tipDriverLayout.second.visibility = View.VISIBLE
            binding.tipDriverLayout.third2.visibility = View.GONE
            binding.tipDriverLayout.third.visibility = View.VISIBLE
            binding.tipDriverLayout.fourth2.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.VISIBLE

            setAlphaForButtons(binding.tipDriverLayout.first, 50)
        }
        binding.tipDriverLayout.first2.setOnClickListener {
            binding.tipDriverLayout.first2.visibility = View.GONE
            binding.tipDriverLayout.first.visibility = View.VISIBLE

            binding.tipDriverLayout.second2.visibility = View.GONE
            binding.tipDriverLayout.second.visibility = View.VISIBLE
            binding.tipDriverLayout.third2.visibility = View.GONE
            binding.tipDriverLayout.third.visibility = View.VISIBLE
            binding.tipDriverLayout.fourth2.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.VISIBLE
            setAlphaForButtons(binding.tipDriverLayout.first, 0)
        }
        binding.tipDriverLayout.second.setOnClickListener {
            binding.tipDriverLayout.second.visibility = View.GONE
            binding.tipDriverLayout.second2.visibility = View.VISIBLE

            binding.tipDriverLayout.first2.visibility = View.GONE
            binding.tipDriverLayout.first.visibility = View.VISIBLE
            binding.tipDriverLayout.third2.visibility = View.GONE
            binding.tipDriverLayout.third.visibility = View.VISIBLE
            binding.tipDriverLayout.fourth2.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.VISIBLE
            setAlphaForButtons(binding.tipDriverLayout.second, 100)
        }
        binding.tipDriverLayout.second2.setOnClickListener {
            binding.tipDriverLayout.second2.visibility = View.GONE
            binding.tipDriverLayout.second.visibility = View.VISIBLE

            binding.tipDriverLayout.first2.visibility = View.GONE
            binding.tipDriverLayout.first.visibility = View.VISIBLE
            binding.tipDriverLayout.third2.visibility = View.GONE
            binding.tipDriverLayout.third.visibility = View.VISIBLE
            binding.tipDriverLayout.fourth2.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.VISIBLE
            setAlphaForButtons(binding.tipDriverLayout.first, 0)
        }
        binding.tipDriverLayout.third.setOnClickListener {
            binding.tipDriverLayout.third.visibility = View.GONE
            binding.tipDriverLayout.third2.visibility = View.VISIBLE

            binding.tipDriverLayout.first2.visibility = View.GONE
            binding.tipDriverLayout.first.visibility = View.VISIBLE
            binding.tipDriverLayout.second2.visibility = View.GONE
            binding.tipDriverLayout.second.visibility = View.VISIBLE
            binding.tipDriverLayout.fourth2.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.VISIBLE
            setAlphaForButtons(binding.tipDriverLayout.third, 200)
        }
        binding.tipDriverLayout.third2.setOnClickListener {
            binding.tipDriverLayout.third2.visibility = View.GONE
            binding.tipDriverLayout.third.visibility = View.VISIBLE

            binding.tipDriverLayout.first2.visibility = View.GONE
            binding.tipDriverLayout.first.visibility = View.VISIBLE
            binding.tipDriverLayout.second2.visibility = View.GONE
            binding.tipDriverLayout.second.visibility = View.VISIBLE
            binding.tipDriverLayout.fourth2.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.VISIBLE
            setAlphaForButtons(binding.tipDriverLayout.first, 0)
        }
        binding.tipDriverLayout.fourth.setOnClickListener {
            bottomView.visibility = View.VISIBLE
            binding.tipDriverLayout.doneButtonTipRate.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.GONE
            binding.tipDriverLayout.fourth2.visibility = View.VISIBLE

            binding.tipDriverLayout.first2.visibility = View.GONE
            binding.tipDriverLayout.first.visibility = View.VISIBLE
            binding.tipDriverLayout.second2.visibility = View.GONE
            binding.tipDriverLayout.second.visibility = View.VISIBLE
            binding.tipDriverLayout.third2.visibility = View.GONE
            binding.tipDriverLayout.third.visibility = View.VISIBLE
        }

        binding.tipDriverLayout.third2.setOnClickListener {
            binding.tipDriverLayout.fourth2.visibility = View.GONE
            binding.tipDriverLayout.fourth.visibility = View.VISIBLE

            binding.tipDriverLayout.first2.visibility = View.GONE
            binding.tipDriverLayout.first.visibility = View.VISIBLE
            binding.tipDriverLayout.second2.visibility = View.GONE
            binding.tipDriverLayout.second.visibility = View.VISIBLE
            binding.tipDriverLayout.third2.visibility = View.GONE
            binding.tipDriverLayout.third.visibility = View.VISIBLE
            setAlphaForButtons(binding.tipDriverLayout.first, 0)
        }

        binding.tipDriverLayout.cleanliness.setOnClickListener {
            viewModel.updateRateComment("Cleanliness")
            binding.tipDriverLayout.cleanliness.visibility = View.GONE
            binding.tipDriverLayout.cleanliness2.visibility = View.VISIBLE
        }
        binding.tipDriverLayout.navigation.setOnClickListener {
            viewModel.updateRateComment("Navigation")
            binding.tipDriverLayout.navigation.visibility = View.GONE
            binding.tipDriverLayout.navigation2.visibility = View.VISIBLE
        }
        binding.tipDriverLayout.price.setOnClickListener {
            viewModel.updateRateComment("Price")
            binding.tipDriverLayout.price.visibility = View.GONE
            binding.tipDriverLayout.price2.visibility = View.VISIBLE
        }
        binding.tipDriverLayout.service.setOnClickListener {
            viewModel.updateRateComment("Service")
            binding.tipDriverLayout.service.visibility = View.GONE
            binding.tipDriverLayout.service2.visibility = View.VISIBLE
        }

        binding.tipDriverLayout.route.setOnClickListener {
            viewModel.updateRateComment("Route")
            binding.tipDriverLayout.route.visibility = View.GONE
            binding.tipDriverLayout.route2.visibility = View.VISIBLE
        }
        binding.tipDriverLayout.driving.setOnClickListener {
            viewModel.updateRateComment("Driving")
            binding.tipDriverLayout.driving.visibility = View.GONE
            binding.tipDriverLayout.driving2.visibility = View.VISIBLE
        }

        binding.tipDriverLayout.cleanliness2.setOnClickListener {
            viewModel.updateRateComment("Cleanliness")
            binding.tipDriverLayout.cleanliness.visibility = View.VISIBLE
            binding.tipDriverLayout.cleanliness2.visibility = View.GONE
        }
        binding.tipDriverLayout.navigation2.setOnClickListener {
            viewModel.updateRateComment("Navigation")
            binding.tipDriverLayout.navigation.visibility = View.VISIBLE
            binding.tipDriverLayout.navigation2.visibility = View.GONE
        }
        binding.tipDriverLayout.price2.setOnClickListener {
            viewModel.updateRateComment("Price")
            binding.tipDriverLayout.price.visibility = View.VISIBLE
            binding.tipDriverLayout.price2.visibility = View.GONE
        }
        binding.tipDriverLayout.service2.setOnClickListener {
            viewModel.updateRateComment("Service")
            binding.tipDriverLayout.service.visibility = View.VISIBLE
            binding.tipDriverLayout.service2.visibility = View.GONE
        }

        binding.tipDriverLayout.route2.setOnClickListener {
            viewModel.updateRateComment("Route")
            binding.tipDriverLayout.route.visibility = View.VISIBLE
            binding.tipDriverLayout.route2.visibility = View.GONE
        }
        binding.tipDriverLayout.driving2.setOnClickListener {
            viewModel.updateRateComment("Driving")
            binding.tipDriverLayout.driving.visibility = View.VISIBLE
            binding.tipDriverLayout.driving2.visibility = View.GONE
        }

        binding.tipDriverLayout.other.setOnClickListener {
         //   viewModel.updateRateComment("Service")
            ratingView.setUp(requireContext())
            ratingView.visibility = View.VISIBLE
            binding.tipDriverLayout.doneButtonTipRate.visibility = View.GONE
        }

        binding.tipDriverLayout.other2.setOnClickListener {
            removeCustomRating()
        }

        binding.tipDriverLayout.otherWord.setOnClickListener {
            removeCustomRating()
        }


        cancelRideClicks()


        binding.bookedHomeBottom.cancelButton.setOnClickListener {
            showRideCancellationDialog()
        }

        binding.bookedOnrideBottom.cancelButton.setOnClickListener {
            showRideCancellationDialog()
        }
        binding.cancelReasonLayout.close.setOnClickListener {
            binding.cancelReasonLayout.cancelBottom.visibility = View.GONE
        }

        binding.cancelReasonLayout.submitFeedback.setOnClickListener {
            if (viewModel.uiState.value.reasonForCancel.isEmpty()){
                Toast.makeText(requireContext(), "Please select a reason for cancellation", Toast.LENGTH_LONG).show()
            }else{
                binding.cancelReasonLayout.cancelBottom.visibility = View.GONE
                viewModel.cancelARide()
            }
        }


        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState.currentStatus) {
                    TripStatus.DRIVER_ARRIVED -> bindDriverArrived(data = uiState.metaData)
                    TripStatus.TRIP_STARTED -> bindTripStarted(data = uiState.metaData)
                    TripStatus.TRIP_COMPLETED -> bindRateDriver(uiState.metaData)//bindTripCompleted(data = uiState.metaData)
                    TripStatus.START_RIDE -> bindStartRide(uiState.metaData)
                    TripStatus.ACCEPTED -> {
                        setUpDefaultView(data = uiState.metaData)
                    }
                    TripStatus.UNKNOWN -> {
                        /** Do nothing **/
                    }
                    else -> {
                        findNavController().navigate(R.id.action_global_homeFragment)
                    }
                }

                if (uiState.isRideCancelled){
                    findNavController().navigate(R.id.action_global_homeFragment)
                }

                if (uiState.isSuccess){
                    Toast.makeText(context, "Tip and Rating Successful", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_global_homeFragment)
                }

                if (uiState.error.isNotEmpty()){
                    Toast.makeText(context, "Tip and Rating unsuccessful", Toast.LENGTH_LONG).show()
                }

                toggleBusyDialog(
                    uiState.loading,
                    desc = if (uiState.loading) "Submitting Data..." else null
                )
            }
        }

        binding.okayRide.setOnClickListener {
            binding.verifiedCode.visibility = View.GONE
        }

        binding.scheduleButton.setOnClickListener {
            emergedBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setAlphaForButtons(button: AppCompatButton, value: Int){
        viewModel.updateTipValue(value)
    }

    private fun setAlphaCancelForButtons(value: String){
        viewModel.updateCancelValue(value)
    }

    override fun onStart() {
        super.onStart()
        drawerLayout = activity?.findViewById(R.id.drawer_layout)!!
        drawer = binding.drawerButton
        drawer.setOnClickListener {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START)
            } else {

                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        if (googleMap != null) {
            gMap = googleMap
        }
        MapsInitializer.initialize(activity?.applicationContext)
/*
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                if (/*!uiState.isLocationProcessed && */uiState.hasValidCoordinates) {
                    var locationModels = arrayOf<LocationModel>()
                    locationModels += LocationModel(
                        lat = uiState.pickupLatitude,
                        lng = uiState.pickupLongitude,
                        address = uiState.pickupAddress
                    )
                    locationModels += LocationModel(
                        lat = uiState.dropOffLatitude,
                        lng = uiState.dropOffLongitude,
                        address = uiState.destinationAddress
                    )
                    val builder = LatLngBounds.Builder()
                    builder.include(LatLng(locationModels[0].lat, locationModels[0].lng))
                    builder.include(LatLng(locationModels[1].lat, locationModels[1].lng))
                    val bounds = builder.build()
                    val width = resources.displayMetrics.widthPixels;
                    val height = resources.displayMetrics.heightPixels;
                    val padding = (width * 0.40).toInt()
                    val cu = CameraUpdateFactory.newLatLngBounds(bounds, 100)

                    //  gMap.setPadding(50,50,50,50)
                    //  gMap.animateCamera(cu)
                    gMap.moveCamera(cu)

                 /*   val currentLocation = LatLng(locationModels[0].lat, locationModels[0].lng)
                    val cameraPosition = CameraPosition.Builder()
                        .bearing(0.toFloat())
                        .target(currentLocation)
                        .zoom(15.5.toFloat())
                        .build()*/
                    // gMap.animateCamera(cu)

                    getGeoLocation(locationModels, gMap, true) {
                        if (!uiState.isMarkerRendered){
                            gMap.clear()
                            addMarkerToPolyLines(it, locationModels) {
                                viewModel.markerAdded()
                            }
                        }
                    }
                }
            }
        }
*/
        // what is driver_pickup_eta
        // trip_began

        locationProcessor.checkLocationPermission(context) {
            locationProcessor.requestLocationPermission(this)
        }

        val gMapView = mapView.view
        if (gMapView?.findViewById<View>("1".toInt()) != null) {
            val locationButton =
                (gMapView.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 850)
        }

        googleMap?.isMyLocationEnabled = true
    }

    private fun addMarkerToPolyLines(
        geoDirections: GeoDirectionsResponse,
        location: Array<LocationModel>,
        onMarkerAdded: () -> Unit = {}
    ) {
        val endLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.endLocation
        val startLocation = geoDirections.routes?.get(0)?.legs?.get(0)?.startLocation
        var loc1 = location[0].address
        if (loc1.length > 20) {
            loc1 = location[0].address.substring(0..20)
        }

        var loc2 = location[1].address
        if (loc2.length > 20) {
            loc2 = location[1].address.substring(0..20)
        }

        val sLl = (startLocation?.lat ?: 0.0)
        val sLlg = (startLocation?.lng ?: 0.0)

        val sLl2 = (endLocation?.lat ?: 0.0)
        val sLlg2 = (endLocation?.lng ?: 0.0)
        Log.d("the latlng", "the start ${sLl}, $sLlg")
        Log.d("the latlng", "the end ${sLl2}, $sLlg2")
        val marker1 = MarkerOptions()
        val marker2 = MarkerOptions()

        if (sLlg2 < sLlg){
            Log.d("the latlng", "the start bigger")
            marker1.position(LatLng(sLl, sLlg))
                .anchor(1.05f, 1.05f)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createClusterBitmap(
                            add = loc1,
                            loc = "Start",
                            color = "#F57519"
                        )
                    )
                )

            marker2 .position(LatLng(sLl2, sLlg2))
                .anchor(0.05f, 1.05f)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createClusterBitmap(
                            add = loc2,
                            loc = "To",
                            color = "#F9170F"
                        )
                    )
                )
        }else{
            Log.d("the latlng", "the start smaller")
            marker1.position(LatLng(sLl, sLlg))
                .anchor(0.05f, 1.05f)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createClusterBitmap(
                            add = loc1,
                            loc = "Start",
                            color = "#F57519"
                        )
                    )
                )

            marker2 .position(LatLng(sLl2, sLlg2))
                .anchor(1.05f, 1.05f)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createClusterBitmap(
                            add = loc2,
                            loc = "To",
                            color = "#F9170F"
                        )
                    )
                )
        }


        /*   val marker1 = MarkerOptions()
               .position(LatLng(sLl, sLlg))
               .anchor(1.05f, 1.05f)
               .icon(
                   BitmapDescriptorFactory.fromBitmap(
                       createClusterBitmap(
                           add = loc1,
                           loc = "Start",
                           color = "#F57519"
                       )
                   )
               )*/

        /*  val marker2 = MarkerOptions()
              .position(LatLng(sLl2, sLlg2))
              .anchor(0.05f, 1.05f)
              .icon(
                  BitmapDescriptorFactory.fromBitmap(
                      createClusterBitmap(
                          add = loc2,
                          loc = "To",
                          color = "#F9170F"
                      )
                  )
              )*/

        val marker3 = MarkerOptions()
            .position(LatLng(startLocation?.lat ?: 0.0, startLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_pick))

        val marker4 = MarkerOptions()
            .position(LatLng(endLocation?.lat ?: 0.0, endLocation?.lng ?: 0.0))
            .icon(bitmapFromVector(R.drawable.ic_driver_dest))

        gMap.addMarker(marker1)
        gMap.addMarker(marker2)
        gMap.addMarker(marker3)
        gMap.addMarker(marker4)

        val builder = LatLngBounds.Builder()
        builder.include(
            LatLng(
                geoDirections.routes?.get(0)?.bounds?.northeast?.lat ?: 0.0,
                geoDirections.routes?.get(0)?.bounds?.northeast?.lng ?: 0.0
            )
        )
        builder.include(
            LatLng(
                geoDirections.routes?.get(0)?.bounds?.southwest?.lat ?: 0.0,
                geoDirections.routes?.get(0)?.bounds?.southwest?.lat ?: 0.0
            )
        )

        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.2).toInt()

        val boundsUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        //  gMap.animateCamera(boundsUpdate)
        //  Toast.makeText(context, "22", Toast.LENGTH_LONG).show()
        onMarkerAdded()
    }

    private fun createClusterBitmap(add: String, loc: String, color: String): Bitmap {
        val cluster: View = LayoutInflater.from(context).inflate(
            R.layout.choose_rider_marker,
            null
        )
        val clusterSizeText = cluster.findViewById<View>(R.id.address) as TextView
        clusterSizeText.text = add
        val clusterSizeText2 = cluster.findViewById<View>(R.id.state) as TextView
        clusterSizeText2.text = loc
        clusterSizeText2.setTextColor(Color.parseColor(color))

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


    private fun displayLocationSettingsRequest() {
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
                    activity?.let {
                        locationException.startResolutionForResult(
                            it,
                            Constants.LOCATION_REQUEST_CODE
                        )
                    }
                } catch (senderException: IntentSender.SendIntentException) {
                    senderException.printStackTrace()
                    /*  Snackbar.make(context, binding.root,"Please enable location setting to use your current address.",

                          View.OnClickListener { displayLocationSettingsRequest() }, "Retry", Snackbar.LENGTH_LONG

                          )*/

                    val snackbar = Snackbar
                        .make(
                            binding.root,
                            "Please enable location setting to use your current address.",
                            Snackbar.LENGTH_LONG
                        )
                        .setAction("Retry") {
                            displayLocationSettingsRequest()
                        }

                    snackbar.show()
                    // showErrorMessage(context, constraintLayout, "Please enable location setting to use your current address.",
                    //  View.OnClickListener { displayLocationSettingsRequest() }, getString(com.google.android.gms.maps.R.string.retry_text))
                }
            }
        }
    }

    fun getLocationProvider(): FusedLocationProviderClient? {
        return activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    override fun onLocationChanged(p0: Location) {
        val currentLocation = LatLng(p0.latitude, p0.longitude)
        getAddressFromLocation(currentLocation)

        val addCircle =
            CircleOptions().center(LatLng(p0.latitude, p0.longitude)).radius(radiusInMeters)
                .fillColor(shadeColor)
                .strokeColor(strokeColor).strokeWidth(8f)
        mCircle = gMap.addCircle(addCircle)

        //move map camera

        //move map camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(p0.latitude, p0.longitude)))
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this)
        }
    }

    fun getAddressFromLocation(location: LatLng?) {

        try {
            if (location != null) {
                val geoCoder = Geocoder(context, Locale.getDefault())

                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                val address = if (addresses.isNotEmpty())
                    addresses[0].getAddressLine(0)
                else ""


            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun toggleBusyDialog(busy: Boolean, desc: String? = null) {
        if (busy) {
            if (mDialog == null) {
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_busy_layout, null)
                mDialog = LauncherUtil.showPopUp(requireContext(), view, desc)
            } else {
                if (!desc.isNullOrBlank()) {
                    val view = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_busy_layout, null)
                    mDialog = LauncherUtil.showPopUp(requireContext(), view, desc)
                }
            }
            mDialog?.show()
        } else {
            mDialog?.dismiss()
        }
    }

    override fun closeButton() {
        binding.tipDriverLayout.doneButtonTipRate.visibility = View.VISIBLE
        bottomView.visibility = View.GONE
    }

    override fun closeCustomButton() {
        ratingView.visibility = View.GONE
        binding.tipDriverLayout.doneButtonTipRate.visibility = View.VISIBLE

    }

    private fun removeCustomRating() {
        val rating = viewModel.uiState.value.customRating
        viewModel.updateRateComment(rating)
        binding.tipDriverLayout.other2.visibility = View.GONE
        binding.tipDriverLayout.other.visibility = View.VISIBLE
        binding.tipDriverLayout.otherWord.visibility = View.GONE
    }

    override fun ratingDone() {
        val rating = viewModel.uiState.value.customRating
        viewModel.updateRateComment(rating)
        ratingView.visibility = View.GONE
        binding.tipDriverLayout.doneButtonTipRate.visibility = View.VISIBLE
        binding.tipDriverLayout.other2.visibility = View.VISIBLE
        binding.tipDriverLayout.other.visibility = View.GONE
        binding.tipDriverLayout.otherWord.visibility = View.VISIBLE
    }

    override fun addRating(rating: String) {
        viewModel.updateRating(rating)
        binding.tipDriverLayout.customRating.text = rating
    }

    override fun skipAction() {
    }

    override fun addFundDone() {
        bottomView.visibility = View.GONE
        binding.tipDriverLayout.doneButtonTipRate.visibility = View.VISIBLE
        //   val action = BalanceFragmentDirections.actionGlobalCashOutCardsFragment(viewModel.state.value.addFundAmount)
        // findNavController().navigate(R.id.action_global_cashOutCardsFragment)
        //  findNavController().navigate(action)
    }

    override fun cashOutDone() {

    }

    override fun cashOutAmount(amount: String) {

    }

    override fun addFundAmount(amount: String) {
       // bottomView.visibility = View.GONE
        setAlphaForButtons(binding.tipDriverLayout.fourth, (amount.toDoubleOrNull() ?: 0.0).toInt())
    }

}