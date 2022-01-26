package com.mevron.rides.rider.home.selectmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.SelectOnMapFragmentBinding

class SelectOnMapFragment : Fragment() {

    companion object {
        fun newInstance() = SelectOnMapFragment()
    }

    private lateinit var viewModel: SelectOnMapViewModel
    private lateinit var binding: SelectOnMapFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.select_on_map_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bqckButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.pickMe.setOnClickListener {
            binding.pickMeLqyout.visibility = View.GONE
            binding.dropMeLqyout.visibility = View.VISIBLE
        }
    }



}