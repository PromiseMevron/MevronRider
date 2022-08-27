package com.mevron.rides.rider.settings.referal.ui

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
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
        bottomSheetBehavior = BottomSheetBehavior.from(binding.mevronReferalBottom.bottomSheet)
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
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.mevronReferalBottom.bottomSheet.visibility = View.VISIBLE
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.mevronReferalBottom.bottomSheet.visibility = View.GONE
                }

                if (state.error.isNotEmpty()) {
                    Log.d("Failure", state.error)
                    Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                }
            }

        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.mevronReferalBottom.bottomSheet.visibility = View.VISIBLE
                        viewModel.updateState(setReferal = true)
                        //     binding.mevronHomeBottom.scheduleButton.visibility = View.GONE
                        //    binding.mevronHomeBottom.myLocation.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.mevronReferalBottom.bottomSheet.visibility = View.GONE
                        viewModel.updateState(setReferal = false)
                    }
                    else -> {

                    }
                }
            }
        })

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
          shareAction()
        }

        binding.shareCode.setOnClickListener {
            shareAction()
        }

        binding.copyCode.setOnClickListener {
            val myClipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("ref", "${binding.referalName.text.toString()}")
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(requireContext(), "Code Copied", Toast.LENGTH_LONG).show()
        }

        binding.shareToContact.setOnClickListener {
            showDialog()
        }


    }

    private fun shareAction(){
        val sharingIntent = Intent(Intent.ACTION_SEND)

        // type of the content to be shared

        // type of the content to be shared
        sharingIntent.type = "text/plain"

        // Body of the content

        // Body of the content
        val shareBody = "Join me in using Mevron Ride using my referral code: ${binding.referalName.text.toString()}"

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

    private fun showDialog() {
        val dialog = activity?.let { Dialog(it) }!!
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.share_dialog)
        // val body = dialog.findViewById(R.id.body) as TextView
        //  body.text = title
        val whatsapp = dialog.findViewById(R.id.whatsapp) as LinearLayout
        val telegram = dialog.findViewById(R.id.telegram) as LinearLayout
        val signal = dialog.findViewById(R.id.signal) as LinearLayout
        val cancel = dialog.findViewById(R.id.dont) as MaterialButton
        whatsapp.setOnClickListener {
            dialog.dismiss()
            openSharesApp("com.whatsapp")
        }
        telegram.setOnClickListener {
            dialog.dismiss()
            openSharesApp("org.telegram.messenger")}
        signal.setOnClickListener {
            dialog.dismiss()
            openSharesApp("org.thoughtcrime.securesms")}
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            requireContext().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openSharesApp(packageName: String){
        if (isPackageInstalled(packageName)){
            // Creating intent with action send
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage(packageName)

            val shareBody = "Join me in using Mevron Ride"
            val shareSubject =
                "Ride with mevron and enjoy the beauty of riding \nRegister using my code ${binding.referalName.text.toString()}"

            intent.putExtra(Intent.EXTRA_TEXT, shareBody)

            intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)

            startActivity(intent)

        }else{
            Toast.makeText(requireContext(), "Application not installed", Toast.LENGTH_LONG).show()
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