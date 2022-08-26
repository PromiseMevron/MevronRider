package com.mevron.rides.rider.settings.referal.ui

import android.app.DatePickerDialog
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
import com.mevron.rides.rider.databinding.ReferalDetailFragmentBinding
import com.mevron.rides.rider.settings.referal.ui.event.ReferalEvent
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReferalDetailFragment : Fragment() {
    companion object {
        fun newInstance() = ReferalDetailFragment()
    }

    private val viewModel: ReferalDetailViewModel by viewModels()
    private lateinit var binding: ReferalDetailFragmentBinding
    var startDate = ""
    var endDate = ""
    var id = ""
    private var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.referal_detail_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        id = ReferalDetailFragmentArgs.fromBundle(
            requireArguments()
        ).id
        viewModel.updateState(referalID = id)

        lifecycleScope.launchWhenResumed {
            viewModel.state.collect { state ->
                toggleBusyDialog(
                    state.isLoading,
                    desc = if (state.isLoading) "Processing..." else null
                )

                if (state.error.isNotEmpty()) {
                    Log.d("Failure", state.error)
                    Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                }
                if (state.startDate.isNotEmpty() && state.endDate.isNotEmpty()) {
                    viewModel.handleEvent(ReferalEvent.GetReferalDetail)
                }
                binding.rideNumber.text = state.numberOfRides
            }
        }

        binding.selectStart.setOnClickListener {
            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, i, i2, i3 ->
                        startDate = "${i}-${i2}-${i3}"
                        viewModel.updateState(startDate = startDate)
                        binding.startDate.text = "${returnMonth(i2)} ${i}"
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                )
            }
            datePickerDialog?.show()
        }

        binding.selectEnd.setOnClickListener {
            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, i, i2, i3 ->
                        endDate = "${i}-${i2}-${i3}"
                        viewModel.updateState(endDate = endDate)
                        binding.endDate.text = "${returnMonth(i2)} ${i}"
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                )
            }
            datePickerDialog?.show()
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

    fun returnMonth(i: Int): String {
        var mth = ""
        mth = when (i) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Niv"
            else -> "Dec"
        }

        return mth
    }


}