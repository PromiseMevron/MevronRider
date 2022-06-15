package com.mevron.rides.rider.settings.referal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.ReferalDetailFragmentBinding
import com.mevron.rides.rider.settings.referal.model.ReferalReport
import com.mevron.rides.rider.util.toggleBusyDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class ReferalDetailFragment : Fragment(){

    companion object {
        fun newInstance() = ReferalDetailFragment()
    }

    private val viewModel: ReferalDetailViewModel by viewModels()
    private lateinit var binding: ReferalDetailFragmentBinding
    var startDate = ""
    var endDate = ""
    var id = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.referal_detail_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        id = ReferalDetailFragmentArgs.fromBundle(requireArguments()).id
        binding.selectStart.setOnClickListener {
            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, i, i2, i3 ->
                        startDate = "${i}-${i2}-${i3}"
                        binding.startDate.text = "${returnMonth(i2)} i"
                        if (startDate.isNotEmpty() && endDate.isNotEmpty()){
                            getReport()
                        }
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
                        binding.endDate.text = "${returnMonth(i2)} i"
                        if (startDate.isNotEmpty() && endDate.isNotEmpty()){
                            getReport()
                        }
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                )
            }
            datePickerDialog?.show()
        }
    }

    fun getReport(){
        toggleBusyDialog(true)
        val data = ReferalReport(from = startDate, to = endDate, referral_id = id)
        viewModel.getReferal(data).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            toggleBusyDialog(false)
        })
    }


    fun returnMonth(i: Int): String{
        var mth = ""
        mth = when(i) {
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