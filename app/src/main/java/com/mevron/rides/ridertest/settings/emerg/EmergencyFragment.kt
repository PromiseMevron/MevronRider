package com.mevron.rides.ridertest.settings.emerg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.EmergencyFragmentBinding

class EmergencyFragment : Fragment() {

    companion object {
        fun newInstance() = EmergencyFragment()
    }

    private lateinit var viewModel: EmergencyViewModel
    private lateinit var binding: EmergencyFragmentBinding
    private var adapter = EmergencyAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.emergency_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.recyclerView.adapter = adapter

        binding.addContact.setOnClickListener {

        }
    }


}