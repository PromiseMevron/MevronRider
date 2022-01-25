package com.mevron.rides.rider.home.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mevron.rides.rider.R

class SearchLocationFragment : Fragment() {

    companion object {
        fun newInstance() = SearchLocationFragment()
    }

    private lateinit var viewModel: SearchLocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_location_fragment, container, false)
    }



}