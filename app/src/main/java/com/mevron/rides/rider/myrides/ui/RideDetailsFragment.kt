package com.mevron.rides.rider.myrides.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.RideDetailsFragmentBinding
import com.mevron.rides.rider.myrides.ui.event.MyRidesEvents
import com.mevron.rides.rider.payment.PaymentAdapter
import com.mevron.rides.rider.util.LauncherUtil
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideDetailsFragment : Fragment() {
    companion object {
        fun newInstance() = RideDetailsFragment()
    }
    private var mDialog: Dialog? = null
    private val viewModel: RideDetailsViewModel by viewModels()
    private lateinit var binding: RideDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.ride_details_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.let { RideDetailsFragmentArgs.fromBundle(it).id }!!
        viewModel.updateState(rideId = id)
        viewModel.handleEvent(MyRidesEvents.GetAddress)

        lifecycleScope.launchWhenResumed {

            viewModel.state.collect { state ->
                toggleBusyDialog(
                    state.isLoading,
                    desc = if (state.isLoading) "Processing..." else null
                )
                state.rideDetail.apply {
                    binding.rideDate.text = this.dateAndTime
                    binding.driverCar.text = this.carNumber
                    binding.pickAddress.text = this.departureAddress
                    binding.dropAddress.text = this.arrivalAddress
                    binding.driverName.text = this.driverName
                    binding.rating.rating = this.driverRating.toFloatOrNull() ?:0F
                    binding.cardNumber.text = if (this.paymentType == "cash"){
                        this.paymentType
                    }else{
                        "Card"
                    }
                    binding.total.text = this.total
                    if (!this.driverProfile.isNullOrEmpty()) {
                        Picasso.get().load(this.driverProfile).error(R.drawable.profile)
                            .placeholder(R.drawable.profile).into(binding.driverImage)
                    }
                    val mapImage = "https://maps.googleapis.com/maps/api/staticmap?size=300x300&path=color:0xFF9B04|weight:3|${this.startLat},${this.startLng}|${this.endLat},${this.endLng}&markers=icon:https://firebasestorage.googleapis.com/v0/b/mevron-1330b.appspot.com/o/MapMarkerImage%2FEllipse%203%20(1).png?alt=media&token=43a50b97-b6a1-475d-bacc-fd30a2d22446|${this.startLat},${this.startLng}&markers=icon:https://firebasestorage.googleapis.com/v0/b/mevron-1330b.appspot.com/o/MapMarkerImage%2FEllipse%203.png?alt=media&token=65f90d1d-0e93-4636-acbf-9ab01e006e4f|${this.endLat},${this.endLng}&sensor=false&key=AIzaSyACHmEwJsDug1l3_IDU_E4WEN4Qo_i_NoE"
                    Picasso.get().load(mapImage).error(R.drawable.street_map).placeholder(R.drawable.street_map).into(binding.googleMap)
                }
                if (state.error.isNotEmpty()) {
                    Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                    viewModel.updateState(errorMessage = "")
                }

            }

        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
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

}