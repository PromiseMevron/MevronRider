package com.mevron.rides.rider.payment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.AddTipFragmentBinding
import com.mevron.rides.rider.payment.tipping.AddCustomTipDialogView
import com.mevron.rides.rider.payment.tipping.AddTipViewEvent
import com.mevron.rides.rider.payment.tipping.AddTipViewModel
import com.mevron.rides.rider.payment.tipping.Comment
import com.mevron.rides.rider.payment.tipping.TipPercent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddTipFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = AddTipFragment()
    }

    private lateinit var viewModel: AddTipViewModel
    private lateinit var binding: AddTipFragmentBinding

    private lateinit var dialog: AddCustomTipDialogView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_tip_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCommentClickListeners()
        setTipPercentClickListeners()
        binding.doneButton.setOnClickListener { viewModel.setEvent(AddTipViewEvent.AddTipClicked) }
        binding.rating.setOnRatingBarChangeListener { _, rating, _ ->
            viewModel.setEvent(AddTipViewEvent.RatingAdded(rating.toInt()))
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->

                uiState.shouldNavigateToHome.get {
                    findNavController().navigate(R.id.action_global_homeFragment)
                }

                uiState.isShowRawTipClicked.get {
                    showTipDialog(viewModel.getTipValue())
                }

                if (dialog.isShowing) {
                    dialog.setTipValue(viewModel.getTipValue(), "$")
                }

                if (uiState.error.isNotEmpty()) {
                    Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showTipDialog(currentTip: Int) {
        if (dialog.isShowing) {
            // TODO get correct currency
            dialog.setTipValue(currentTip, "$")
        }
        context?.let {
            dialog = AddCustomTipDialogView(
                it,
                decreaseTipClicked = { viewModel.setEvent(AddTipViewEvent.DecreaseTipClicked) },
                increaseTipClicked = { viewModel.setEvent(AddTipViewEvent.IncreaseTipClicked) },
                doneClicked = { viewModel.setEvent(AddTipViewEvent.AddTipClicked) }
            )
            dialog.show()
            dialog.setTipValue(currentTip, "$")
        }
    }

    private fun setCommentClickListeners() {
        binding.cleanliness.setOnClickListener(this)
        binding.navigation.setOnClickListener(this)
        binding.price.setOnClickListener(this)
        binding.service.setOnClickListener(this)
        binding.route.setOnClickListener(this)
        binding.driving.setOnClickListener(this)
        binding.other.setOnClickListener(this)
    }

    private fun setTipPercentClickListeners() {
        binding.first.setOnClickListener(this)
        binding.second.setOnClickListener(this)
        binding.third.setOnClickListener(this)
        binding.fourth.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTipViewModel::class.java)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cleanliness -> viewModel.setEvent(AddTipViewEvent.CommentAdded(Comment.CLEANLINESS))
            R.id.navigation -> viewModel.setEvent(AddTipViewEvent.CommentAdded(Comment.NAVIGATION))
            R.id.price -> viewModel.setEvent(AddTipViewEvent.CommentAdded(Comment.PRICE))
            R.id.service -> viewModel.setEvent(AddTipViewEvent.CommentAdded(Comment.SERVICE))
            R.id.route -> viewModel.setEvent(AddTipViewEvent.CommentAdded(Comment.ROUTE))
            R.id.driving -> viewModel.setEvent(AddTipViewEvent.CommentAdded(Comment.DRIVING))
            R.id.other -> viewModel.setEvent(AddTipViewEvent.CommentAdded(Comment.OTHER))

            R.id.first -> viewModel.setEvent(AddTipViewEvent.TipAdded(TipPercent.FourPercent))
            R.id.second -> viewModel.setEvent(AddTipViewEvent.TipAdded(TipPercent.SevenPercent))
            R.id.third -> viewModel.setEvent(AddTipViewEvent.TipAdded(TipPercent.TenPercent))
            R.id.fourth -> viewModel.setEvent(AddTipViewEvent.ShowRawTipDialog)
        }
    }
}