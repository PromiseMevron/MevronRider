package com.mevron.rides.rider.home.selectmap

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mevron.rides.rider.R

class SelectOnMapFragment : Fragment() {

    companion object {
        fun newInstance() = SelectOnMapFragment()
    }

    private lateinit var viewModel: SelectOnMapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_on_map_fragment, container, false)
    }



}