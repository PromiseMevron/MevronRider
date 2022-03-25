package com.mevron.rides.ridertest.home.ride

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mevron.rides.ridertest.R

class ScheduleConfirmFragment : Fragment() {

    companion object {
        fun newInstance() = ScheduleConfirmFragment()
    }

    private lateinit var viewModel: ScheduleConfirmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.schedule_confirm_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScheduleConfirmViewModel::class.java)
        // TODO: Use the ViewModel
    }

}