package com.mevron.rides.rider.settings.referal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.App
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.ReferalFragmentBinding
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.settings.referal.model.SetReferal
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.REFERRAL_STATUS
import com.mevron.rides.rider.util.toggleBusyDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReferalFragment : Fragment(), SelectedReferal {

    companion object {
        fun newInstance() = ReferalFragment()
    }

    private val viewModel: ReferalViewModel by viewModels()
    private lateinit var binding: ReferalFragmentBinding
    private lateinit var adapter: ReferalAdapter
    val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)


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
        val ref = sPref.getString(Constants.REFERRAL, "")
        val st = sPref.getInt(REFERRAL_STATUS, 0)
        binding.referalName.text = ref
        if (st == 1){
            binding.enterCode.visibility = View.GONE
        }
        getHistory()
        getHistoryFromApi()

        binding.enterCode.setOnClickListener {
            binding.mevronReferalBottom.bottomSheet.visibility = View.VISIBLE
        }

        binding.mevronReferalBottom.updateReferral.setOnClickListener {
            addReferral()
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

    fun getHistory(){
      /*  viewModel.getReferalFromDB().observe(viewLifecycleOwner, Observer {
            val adapter = ReferalAdapter(this, it)
            binding.recyclerView.adapter = adapter
        })*/
    }

    fun getHistoryFromApi(){
        viewModel.getReferal().observe(viewLifecycleOwner, Observer {
            it.let { res ->

                    when(res){

                        is  GenericStatus.Success ->{
                            getHistory()
                            val ref = sPref.getString(Constants.REFERRAL, "")
                            binding.referalName.text = ref
                        }

                        is  GenericStatus.Error ->{
                        }

                        is GenericStatus.Unaunthenticated -> {

                        }
                    }

            }
        })
    }

    fun addReferral(){
        if (binding.mevronReferalBottom.riderCode.text.toString().isEmpty()){
            Toast.makeText(context, "Enter Referral Code", Toast.LENGTH_LONG).show()
            return
        }
        toggleBusyDialog(true, "Adding Referral")
        val data = SetReferal(binding.mevronReferalBottom.riderCode.text.toString())
        viewModel.addReferal(data).observe(viewLifecycleOwner, Observer {
            toggleBusyDialog(false)
            it.let { res ->
                when(res){

                    is GenericStatus.Success ->{
                        toggleBusyDialog(false)
                        binding.enterCode.visibility = View.GONE
                        binding.mevronReferalBottom.bottomSheet.visibility = View.GONE
                    }


                    is  GenericStatus.Error ->{
                        toggleBusyDialog(false)
                        val mes = res.error?.error?.message
                        Toast.makeText(context, mes, Toast.LENGTH_LONG).show()
                    }

                    is GenericStatus.Unaunthenticated -> {
                        toggleBusyDialog(false)
                    }

            }

            }
        })

    }

    override fun select(id: String) {
        val action = ReferalFragmentDirections.actionReferalFragmentToReferalDetailFragment(id)
        findNavController().navigate(action)
    }


}