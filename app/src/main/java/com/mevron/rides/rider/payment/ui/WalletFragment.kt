package com.mevron.rides.rider.payment.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.R
import com.mevron.rides.rider.payment.domain.PaymentBalanceDetailsDomainDatum
import com.mevron.rides.rider.util.LauncherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletFragment : Fragment(), OnBalanceDetailButtonClickListener,
    CashOutAddFundEventListener {

    companion object {
        fun newInstance() = WalletFragment()
    }

    private val viewModel: WalletViewModel by viewModels()
    private var mDialog: Dialog? = null
    private lateinit var topView: BalanceDetailsTopView
    private lateinit var detail: BalanceDetaillsDetails
    private lateinit var bottomView: CashOutAddFundLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)
        topView = view.findViewById(R.id.balance_top_layer)
        detail = view.findViewById(R.id.balance_details)
        bottomView = view.findViewById(R.id.bottom_view)
        bottomView = view.findViewById(R.id.bottom_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topView.setEventsClickListener(this)
        bottomView.setEventListener(this)
        lifecycleScope.launchWhenResumed {
                viewModel.state.collect { state ->
                    topView.setUpView(state.balance)

                    if (state.data.isNotEmpty()){
                        setUpAdapter(data = state.data)
                    }
                    toggleBusyDialog(
                        state.loading,
                        desc = if (state.loading) "Processing..." else null
                    )

                    if (state.success){
                        Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show()
                        viewModel.updateState(success = false)
                        viewModel.onEvent(WalletEvent.GetWalletDetail)
                    }

                }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(WalletEvent.GetWalletDetail)
    }

    private fun setUpAdapter(data: List<PaymentBalanceDetailsDomainDatum>){
        val adapter = BalanceAdapter(requireContext())
        detail.setUpAdapter(adapter, data)
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

    override fun onDetailOutClicked() {
        bottomView.setUpAddFund(requireContext())
        bottomView.visibility = View.VISIBLE
    }

    override fun backButtonClicked() {
        activity?.onBackPressed()
    }

    override fun cardDetailClicked() {
        findNavController().navigate(R.id.action_global_savedpaymentFragment2)
    }

    override fun closeButton() {
        bottomView.visibility = View.GONE
    }

    override fun skipAction() {
    }

    override fun addFundDone() {
        bottomView.visibility = View.GONE
        val action = WalletFragmentDirections.actionGlobalTopUpFragment(viewModel.state.value.addFundAmount)
           findNavController().navigate(action)
     //   val action = BalanceFragmentDirections.actionGlobalCashOutCardsFragment(viewModel.state.value.addFundAmount)
        // findNavController().navigate(R.id.action_global_cashOutCardsFragment)
      //  findNavController().navigate(action)
    }

    override fun cashOutDone() {

    }

    override fun cashOutAmount(amount: String) {

    }

    override fun addFundAmount(amount: String) {
        viewModel.updateState(addFund = amount)
    }

}