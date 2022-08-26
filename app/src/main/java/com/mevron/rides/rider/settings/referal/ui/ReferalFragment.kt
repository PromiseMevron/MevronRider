package com.mevron.rides.rider.settings.referal.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.ReferalFragmentBinding
import com.mevron.rides.rider.settings.referal.ui.event.ReferalEvent
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReferalFragment : Fragment(), SelectedReferal {

    companion object {
        fun newInstance() = ReferalFragment()
    }

    private val viewModel: ReferalViewModel by viewModels()
    private lateinit var binding: ReferalFragmentBinding
    private lateinit var adapter: ReferalAdapter
    private var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.referal_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.handleEvent(ReferalEvent.GetReferalDetail)
        viewModel.handleEvent(ReferalEvent.GetReferalPrefDetail)
        adapter = ReferalAdapter(this)
        binding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenResumed {

            viewModel.state.collect { state ->
                toggleBusyDialog(
                    state.isLoading,
                    desc = if (state.isLoading) "Processing..." else null
                )
                adapter = ReferalAdapter(this@ReferalFragment)
                binding.recyclerView.adapter = adapter
                adapter.submitList(state.refData)

                binding.referalName.text = state.referralCode

                if (state.referralStatus == 1) {
                    binding.enterCode.visibility = View.GONE
                } else {
                    binding.enterCode.visibility = View.VISIBLE
                }

                if (state.setReferal) {
                    binding.mevronReferalBottom.bottomSheet.visibility = View.VISIBLE
                } else {
                    binding.mevronReferalBottom.bottomSheet.visibility = View.GONE
                }

                if (state.error.isNotEmpty()) {
                    Log.d("Failure", state.error)
                    Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                }
            }

        }

        binding.enterCode.setOnClickListener {
            viewModel.updateState(setReferal = true)
        }

        binding.mevronReferalBottom.updateReferral.setOnClickListener {
            if (binding.mevronReferalBottom.riderCode.text.toString().isEmpty()) {
                Toast.makeText(context, "Enter Referral Code", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.updateState(setCode = binding.mevronReferalBottom.riderCode.text.toString())
            viewModel.handleEvent(ReferalEvent.SetReferalDetail)
        }

        binding.shareReferal.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)

            // type of the content to be shared

            // type of the content to be shared
            sharingIntent.type = "text/plain"

            // Body of the content

            // Body of the content
            val shareBody = "Join me in using Mevron Ride"

            // subject of the content. you can share anything

            // subject of the content. you can share anything
            val shareSubject = "Ride with mevron and enjoy the beauty of riding"

            // passing body of the content

            // passing body of the content
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)

            // passing subject of the content

            // passing subject of the content
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
            startActivity(Intent.createChooser(sharingIntent, "Share using"))
        }


    }

    private fun toggleBusyDialog(busy: Boolean, desc: String? = null) {
        if (busy) {
            if (mDialog == null) {
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_busy_layout, null)
                mDialog = LauncherUtil.showPopUp(requireContext(), view, desc)
            } else {
                if (!desc.isNullOrBlank()) {
                    val view = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_busy_layout, null)
                    mDialog = LauncherUtil.showPopUp(requireContext(), view, desc)
                }
            }
            mDialog?.show()
        } else {
            mDialog?.dismiss()
        }
    }

    override fun select(id: String) {
        val action =
            ReferalFragmentDirections.actionReferalFragmentToReferalDetailFragment(
                id
            )
        findNavController().navigate(action)
    }


}