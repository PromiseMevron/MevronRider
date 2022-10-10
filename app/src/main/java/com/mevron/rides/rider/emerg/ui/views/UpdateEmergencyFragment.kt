package com.mevron.rides.rider.emerg.ui.views


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.UpdateEmergencyFragmentBinding
import com.mevron.rides.rider.emerg.domain.model.GetContactDomainData
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.emerg.data.model.UpdateEmergencyContact
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateEmergencyFragment : Fragment() {

    companion object {
        fun newInstance() = UpdateEmergencyFragment()
    }

    private val viewModel: UpdateEmergencyViewModel by viewModels()
    private lateinit var binding: UpdateEmergencyFragmentBinding
    private lateinit var data: GetContactDomainData
    private var before = false
    private var night = false
    private var manual = false


    var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.update_emergency_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = arguments?.let {
            UpdateEmergencyFragmentArgs.fromBundle(
                it
            ).data
        }!!
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        updateView(data.details)

        binding.name.text = data.name
        binding.number.text = data.phone
        binding.nameToShare.text = "${resources.getString(R.string.select_when_to_share_your_ride_status_with)} ${data.name}."

        binding.before.setOnClickListener {
            before = !before
            night = false
            manual = false
            if (before) {
                binding.before.setBackgroundResource(R.drawable.rounded_border_colored)
                binding.before.setTextColor(resources.getColor(R.color.primary))
            } else {
                binding.before.setBackgroundResource(R.drawable.rounded_border_cancel)
                binding.before.setTextColor(resources.getColor(R.color.field_color))
            }
            binding.manual.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.manual.setTextColor(resources.getColor(R.color.field_color))
            binding.night.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.night.setTextColor(resources.getColor(R.color.field_color))

        }

        binding.night.setOnClickListener {
            night = !night
            before = false
            manual = false
            if (night) {
                binding.night.setBackgroundResource(R.drawable.rounded_border_colored)
                binding.night.setTextColor(resources.getColor(R.color.primary))
            } else {
                binding.night.setBackgroundResource(R.drawable.rounded_border_cancel)
                binding.night.setTextColor(resources.getColor(R.color.field_color))
            }
            binding.manual.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.manual.setTextColor(resources.getColor(R.color.field_color))
            binding.before.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.before.setTextColor(resources.getColor(R.color.field_color))
        }

        binding.manual.setOnClickListener {
            manual = !manual
            before = false
            night = false
            if (manual) {
                binding.manual.setBackgroundResource(R.drawable.rounded_border_colored)
                binding.manual.setTextColor(resources.getColor(R.color.primary))
            } else {
                binding.manual.setBackgroundResource(R.drawable.rounded_border_cancel)
                binding.manual.setTextColor(resources.getColor(R.color.field_color))
            }
            binding.night.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.night.setTextColor(resources.getColor(R.color.field_color))
            binding.before.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.before.setTextColor(resources.getColor(R.color.field_color))
        }

        binding.delete.setOnClickListener {
            toggleBusyDialog(true, "Please Wait")
            viewModel.deleteEmergency(data.id).observe(viewLifecycleOwner, Observer {
                it?.let { res ->
                    when (res) {
                        is GenericStatus.Success -> {
                            toggleBusyDialog(false)
                            activity?.onBackPressed()
                        }

                        is GenericStatus.Error -> {
                            toggleBusyDialog(false)
                            Toast.makeText(context, res.error?.error?.message, Toast.LENGTH_LONG)
                                .show()
                        }

                        is GenericStatus.Unaunthenticated -> {
                            toggleBusyDialog(false)
                            Toast.makeText(context, res.error?.error?.message, Toast.LENGTH_LONG)
                                .show()

                        }
                    }

                }
            })
        }

        binding.doneButton.setOnClickListener {
            var i = mutableListOf<Int>()
            if (before) {
                i = arrayListOf(1)
            }
            if (night) {
                i = arrayListOf(2)
            }
            if (manual) {
                i = arrayListOf(3)
            }
            val dt = UpdateEmergencyContact(name = data.name, phoneNumber = data.phone, details = i)
            toggleBusyDialog(true, "Please Wait")
            viewModel.updateEmergency(id = data.id, data = dt)
                .observe(viewLifecycleOwner, Observer {
                    it?.let { res ->
                        when (res) {
                            is GenericStatus.Success -> {
                                toggleBusyDialog(false)
                                activity?.onBackPressed()
                            }

                            is GenericStatus.Error -> {
                                toggleBusyDialog(false)
                                Toast.makeText(
                                    context,
                                    "Error in updating contact",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is GenericStatus.Unaunthenticated -> {
                                toggleBusyDialog(false)
                                Toast.makeText(
                                    context,
                                    "Error in updating contact",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }

                    }
                })

        }
    }

    private fun updateView(data: List<Int>){
        if (data.contains(1)){
            before = true
            binding.before.setBackgroundResource(R.drawable.rounded_border_colored)
            binding.before.setTextColor(resources.getColor(R.color.primary))
        } else {
            before = false
            binding.before.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.before.setTextColor(resources.getColor(R.color.field_color))
        }

        if (data.contains(2)){
            night = true
            binding.night.setBackgroundResource(R.drawable.rounded_border_colored)
            binding.night.setTextColor(resources.getColor(R.color.primary))
        } else {
            night = false
            binding.night.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.night.setTextColor(resources.getColor(R.color.field_color))
        }

        if (data.contains(3)){
            manual = true
            binding.manual.setBackgroundResource(R.drawable.rounded_border_colored)
            binding.manual.setTextColor(resources.getColor(R.color.primary))
        } else {
            manual = false
            binding.manual.setBackgroundResource(R.drawable.rounded_border_cancel)
            binding.manual.setTextColor(resources.getColor(R.color.field_color))
        }
    }

    fun toggleBusyDialog(busy: Boolean, desc: String? = null) {

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


}