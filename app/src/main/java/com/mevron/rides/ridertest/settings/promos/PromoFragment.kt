package com.mevron.rides.ridertest.settings.promos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mevron.rides.ridertest.R
import com.mevron.rides.ridertest.databinding.PromoFragmentBinding

class PromoFragment : Fragment() {

    companion object {
        fun newInstance() = PromoFragment()
    }

    private val viewModel: PromoViewModel by viewModels()

    private lateinit var binding: PromoFragmentBinding
    var adapter = PromoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.promo_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.recyclerView.adapter = adapter
    }


}