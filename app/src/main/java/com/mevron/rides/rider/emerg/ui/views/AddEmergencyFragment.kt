package com.mevron.rides.rider.emerg.ui.views

import android.Manifest
import android.app.Dialog
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.AddEmergencyFragmentBinding
import com.mevron.rides.rider.emerg.data.model.Contact
import com.mevron.rides.rider.emerg.data.model.Set
import com.mevron.rides.rider.emerg.ui.EmergencyEvent
import com.mevron.rides.rider.emerg.ui.adapter.AddEmergencyAdapter
import com.mevron.rides.rider.emerg.ui.adapter.SaveNumber
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddEmergencyFragment : Fragment(), SaveNumber {
    companion object {
        fun newInstance() = AddEmergencyFragment()
    }
    private val viewModel: AddEmergencyViewModel by viewModels()
    private lateinit var binding: AddEmergencyFragmentBinding

    private lateinit var adapter: AddEmergencyAdapter
    var mDialog: Dialog? = null
    var contactList: ArrayList<Contact> = ArrayList()
    var contactListToSend: ArrayList<Contact> = ArrayList()
    var contactListSend: ArrayList<Set> = ArrayList()
    private val PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_emergency_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
                viewModel.state.collect { state ->
                    if (state.backButton) {
                        activity?.onBackPressed()
                    }

                    if (state.openNextPage) {
                        viewModel.handleEvent(EmergencyEvent.MakeAPICall)
                    }

                    if (state.data.isNotEmpty()) {
                        viewModel.updateState(updateAddress = true)
                    }
                    if (state.isSuccess) {
                        activity?.onBackPressed()
                    }
            }
        }

        binding.close.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.okay.setOnClickListener {
            for (cnt in contactListToSend) {
                val set = Set(name = cnt.name ?: "", phoneNumber = cnt.phoneNumber ?: "")
                contactListSend.add(set)
            }
            viewModel.updateState(savedAddresses = contactListSend)
        }

        getContactList()
    }

    override fun addRemoveContact(cnt: Contact) {
        if (cnt.isSelected) {
            contactListToSend.add(cnt)
        } else {
            for (i in 0 until (contactListToSend.size - 1)) {
                if (cnt.id == contactListToSend[i].id) {
                    contactListToSend.removeAt(i)
                }
            }
        }
    }

    private fun getContactList() {

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                )
            }
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                Constants.LOCATION_REQUEST_CODE
            )
            return
        }
        var id = 0
        val cr: ContentResolver = activity?.contentResolver!!
        val cursor: Cursor? = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            val mobileNoSet = HashSet<String>()
            cursor.use { cursor ->
                val nameIndex: Int = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex: Int =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var name: String
                var number: String
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex)
                    number = cursor.getString(numberIndex)
                    number = number.replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        id += 1
                        contactList.add(Contact(name, number, id = id))
                        mobileNoSet.add(number)
                        Log.d(
                            "hvy", "onCreaterrView  Phone Number: name = " + name
                                    + " No = " + number
                        )
                    }
                }
                adapter = AddEmergencyAdapter(this)
                adapter.submitList(contactList)
                binding.contactsRecyclerView.adapter = adapter
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContactList()
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