package com.mevron.rides.rider.home.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.mevron.rides.rider.socket.domain.CONNECTED
import com.mevron.rides.rider.socket.domain.Connected
import com.mevron.rides.rider.socket.domain.ISocketManager
import com.mevron.rides.rider.socket.domain.SEARCH_DRIVERS
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Screen
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.URISyntaxException

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
        socketManager.connect {  }
       /* var mSocket: Socket? = null

        try {
            mSocket = IO.socket("http://staging.mevron.com:8086")
            Log.d("TAG", "Connecting socket")

        } catch (e: URISyntaxException) {
            Log.d("TAG", "Error Connecting socket $e")
        }

        mSocket?.on("event") {
            Log.d("TAG", "abcd $it")
            val type = "rider"
            mSocket.emit(CONNECTED, "{\"uuid\":\"c83be6ac-9b7e-4033-b0d7-1e7ea2e21a9c\", \"type\": \"rider\"}")
        } ?:  Log.d("TAG", "abcd 333")
        mSocket?.on("search_drivers") {
            Log.d("TAG", "abcd 2 $it")
        } ?:  Log.d("TAG", "abcd 4444")
        //  mSocket?.connect()
        mSocket?.open()*/

     /*   socketManager.connect {
            socketManager.socketInstance()?.on(SEARCH_DRIVERS){
                Log.d("SOCKET", "The data is $it")
            }
            Log.d("SOCKET", "The socket bool is ${socketManager.socketInstance()?.connected()}")
         //   socketManager.socketInstance()?.listeners(SEARCH_DRIVERS)
        }*/



        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val widthOfNav = (Screen.width) * 0.7
        navView.layoutParams.width = widthOfNav.toInt()
        navView.layoutParams.height = Screen.height
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
        image.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
            navController.navigateUp()
            navController.navigate(R.id.profileFragment)
        }

        lifecycleScope.launch {
            viewModel.uiState.collect {
                name.text = it.profileData.firstName
                phone.text = it.profileData.phoneNumber
                rating.rating = it.profileData.rating?.toFloat() ?: 1f
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