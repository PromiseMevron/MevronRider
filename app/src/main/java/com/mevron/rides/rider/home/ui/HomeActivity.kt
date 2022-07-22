package com.mevron.rides.rider.home.ui

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.mevron.rides.rider.App
import com.mevron.rides.rider.R
import com.mevron.rides.rider.socket.domain.ISocketManager
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Screen
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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

    @Inject
    lateinit var socketManager: ISocketManager

    private val viewModel: HomeViewModel by viewModels()
    private val sPref =
        App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        socketManager.connect()

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val widthOfNav = (Screen.width) * 0.7
        navView.layoutParams.width = widthOfNav.toInt()
        navView.requestLayout()

        navController = findNavController(R.id.nav_host_fragment)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.my_rides_fragment,
                R.id.notificationFragment, R.id.savedpaymentFragment, R.id.helpFragment,
                R.id.settingsFragment
            ), drawerLayout
        )
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

        lifecycleScope.launch {
            viewModel.uiState.collect {
                name.text = it.profileData.firstName
                phone.text = it.profileData.lastName
                it.profileData.profilePicture?.let { url ->
                    if (url.isNotEmpty()) {
                        Picasso.get().load(url)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo).into(image)
                    } else {
                        Picasso.get().load(R.drawable.ic_logo).into(image)
                    }
                } ?: Picasso.get().load(R.drawable.ic_logo).into(image)
            }
        }

        viewModel.setEvent(HomeEvent.LoadProfile)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}