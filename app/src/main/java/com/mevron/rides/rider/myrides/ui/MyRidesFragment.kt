package com.mevron.rides.rider.myrides.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.MyRidesFragmentBinding
import com.mevron.rides.rider.myrides.ui.adapter.ViewPagerAdapter

class MyRidesFragment : Fragment() {

    companion object {
        fun newInstance() = MyRidesFragment()
    }
    private val titleArray = arrayOf(
        "Past",
        "Upcoming"
    )
    private lateinit var binding: MyRidesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_rides_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = activity?.supportFragmentManager?.let { ViewPagerAdapter(it, lifecycle) }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titleArray[position]
        }.attach()

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

    }

}