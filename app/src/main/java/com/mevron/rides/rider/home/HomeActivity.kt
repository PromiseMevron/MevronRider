package com.mevron.rides.rider.home

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.mevron.rides.rider.App
import com.mevron.rides.rider.R
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.model.getprofile.Data
import com.mevron.rides.rider.util.Constants
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawer: ImageButton
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var image: CircleImageView
    private lateinit var rating: RatingBar

    private val viewModel: HomeViewModel by viewModels()
    val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        appBarConfiguration = AppBarConfiguration(setOf(R.id.my_rides_fragment,
            R.id.notificationFragment, R.id.savedpaymentFragment, R.id.helpFragment,
            R.id.settingsFragment), drawerLayout)
       // NavigationUI.setupWithNavController(navigationView, navController)
        navView.setupWithNavController(navController)
        val headerView = navView.getHeaderView(0)
        name = findViewById(R.id.full_name)
        phone = findViewById(R.id.phone_number)
        image = findViewById(R.id.profile_image)
        rating = findViewById(R.id.rating)
        drawer = headerView.findViewById(R.id.close_drawer)
        drawer.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        val gson = Gson()
        val json = sPref.getString(Constants.PROFILE, null)
        json?.let {
            val user = gson.fromJson(it, Data::class.java)
            name.text = user.firstName.toString()
            phone.text = user.phoneNumber
            rating.rating = user.rating.toString().toFloat()
            Picasso.get().load(user.profilePicture.toString()).placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).into(image)
            // name.text = user.firstName.toString()
        }

        viewModel.getProfile().observe(this, Observer {
            it.let {  res ->
                when(res){

                    is  GenericStatus.Success ->{
                        val user = res.data?.success?.data
                        name.text = user?.firstName.toString()
                        phone.text = user?.phoneNumber
                        rating.rating = user?.rating.toString().toFloat()
                        Picasso.get().load(user?.profilePicture.toString()).placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).into(image)
                    }

                    is  GenericStatus.Error ->{
                        // toggleBusyDialog(false)
                    }

                    is GenericStatus.Unaunthenticated -> {
                        // toggleBusyDialog(false)
                    }
                }
            }
        })

    }

  /*  override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_drawer, menu)
        return true
    }*/


    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }






}