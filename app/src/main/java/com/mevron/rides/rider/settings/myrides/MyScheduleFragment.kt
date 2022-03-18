package com.mevron.rides.rider.settings.myrides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.MyScheduleFragmentBinding

class MyScheduleFragment : Fragment() {

    companion object {
        fun newInstance() = MyScheduleFragment()
    }

    private lateinit var viewModel: MyScheduleViewModel
    private lateinit var binding: MyScheduleFragmentBinding
    var adapter = ScheduleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_schedule_fragment, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
    }

}