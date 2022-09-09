package com.mevron.rides.rider

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mevron.rides.rider.home.ui.HomeActivity
import com.mevron.rides.rider.util.Constants
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 10000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sPref = App.ApplicationContext.getSharedPreferences(
            Constants.SHARED_PREF_KEY,
            Context.MODE_PRIVATE
        )
        val token = sPref.getString(Constants.TOKEN, null)
        val uuid = sPref.getString(Constants.UUID, null)
        if (token.isNullOrEmpty() || uuid.isNullOrEmpty()) {
            startActivity(Intent(this, IntroScreenActivity::class.java))
            finish()
        } else {
            openHomeActivity()
        }

    }

    private fun openHomeActivity(){
        if (hasPermission()){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }else{
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    private fun hasPermission(): Boolean{
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) || EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun requestPermission(){
        EasyPermissions.requestPermissions(this, "We need access to the location to be able to serve you properly", MY_PERMISSIONS_REQUEST_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
       if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
           SettingsDialog.Builder(this).build().show()
       }else{
           requestPermission()
       }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        openHomeActivity()
    }
}