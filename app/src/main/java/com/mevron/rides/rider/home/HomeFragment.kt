package com.mevron.rides.rider.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mevron.rides.rider.R
import com.mevron.rides.rider.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawer: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        return  binding.root
       // return inflater.inflate()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


                binding.mevronHomeBottom.destAddressField.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchLocationFragment)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mevronHomeBottom.bottomSheet)


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
               when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> binding.mevronHomeBottom.allSavedLayout.visibility = View.VISIBLE
                    BottomSheetBehavior.STATE_COLLAPSED -> binding.mevronHomeBottom.allSavedLayout.visibility = View.GONE
                    else -> binding.mevronHomeBottom.allSavedLayout.visibility = View.GONE
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        drawerLayout = activity?.findViewById(R.id.drawer_layout)!!
        drawer = binding.drawerButton
        drawer.setOnClickListener {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START)
            } else {

                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }



}