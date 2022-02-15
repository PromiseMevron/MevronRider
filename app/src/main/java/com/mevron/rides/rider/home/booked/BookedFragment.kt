package com.mevron.rides.rider.home.booked

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mevron.rides.rider.R

class BookedFragment : Fragment() {

    companion object {
        fun newInstance() = BookedFragment()
    }

    private lateinit var viewModel: BookedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.booked_fragment, container, false)
    }



}