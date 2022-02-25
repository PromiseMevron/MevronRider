package com.mevron.rides.rider.payment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.PaymentMethodFragmentBinding
import com.mevron.rides.rider.home.model.AddCard
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentMethodFragment()
    }

    private val viewModel: PaymentMethodViewModel by viewModels()
    private lateinit var binding: PaymentMethodFragmentBinding
    var ref = "txRef 1111"
    private var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.payment_method_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.credit.setOnClickListener {
            RaveUiManager(this).setAmount(100.0)
                .setCurrency("NGN")
                .setEmail("occ@dd.com")
                .setfName("Promise")
                .setlName("Ochornma")
                .setPublicKey("FLWPUBK_TEST-51e5d0f6c6a58ee544f762c74dc0a3ad-X")
                .setEncryptionKey("FLWSECK_TESTae2b068106c0")
                .setTxRef(ref)
                .acceptAccountPayments(false)
                .acceptCardPayments(true)
                .acceptMpesaPayments(false)
                .acceptAchPayments(false)
                .acceptGHMobileMoneyPayments(false)
                .acceptUgMobileMoneyPayments(false)
                .acceptZmMobileMoneyPayments(false)
                .acceptRwfMobileMoneyPayments(false)
                .acceptSaBankPayments(false)
                .acceptUkPayments(false)
                .acceptBankTransferPayments(false)
                .acceptUssdPayments(false)
                .acceptBarterPayments(false)
                .allowSaveCardFeature(false)
                .acceptFrancMobileMoneyPayments(false, "NG")
                .onStagingEnv(true)
                .showStagingLabel(false)
                .withTheme(R.style.MyCustomTheme)
                .initialize()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message = data.getStringExtra("response")
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
               addCard(ref)
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(context, "ERROR $message", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(context, "CANCELLED $message", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun addCard(ref: String){

          toggleBusyDialog(true,"Adding card...")
        val data = AddCard(ref)

        viewModel.getAddresses(data).observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it.let {  res ->
                when(res){

                    is  GenericStatus.Success ->{
                          toggleBusyDialog(false)
                        Toast.makeText(context, res.data?.success?.message, Toast.LENGTH_LONG).show()
                    }

                    is  GenericStatus.Error ->{
                         toggleBusyDialog(false)
                        Toast.makeText(context, res.error?.error?.message, Toast.LENGTH_LONG).show()
                    }

                    is GenericStatus.Unaunthenticated -> {
                         toggleBusyDialog(false)
                    }
                }
            }
        })

    }


    private fun toggleBusyDialog(busy: Boolean, desc: String? = null){
        if(busy){
            if(mDialog == null){
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_busy_layout,null)
                mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
            }else{
                if(!desc.isNullOrBlank()){
                    val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_busy_layout,null)
                    mDialog = LauncherUtil.showPopUp(requireContext(),view,desc)
                }
            }
            mDialog?.show()
        }else{
            mDialog?.dismiss()
        }
    }


}