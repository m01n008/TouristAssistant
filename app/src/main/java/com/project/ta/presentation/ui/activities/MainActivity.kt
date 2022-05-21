package com.project.ta.presentation.ui.activities

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.project.ta.R
import com.project.ta.domain.MapViewModel
import com.project.ta.presentation.ui.fragments.MainFragment
import com.project.ta.util.PermissionUtility
import dagger.hilt.android.AndroidEntryPoint
import hilt_aggregated_deps._com_project_ta_presentation_ui_fragments_MainFragment_GeneratedInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.wait

@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {

    private val mapViewModel: MapViewModel by viewModels()
    private var defaultJob: Job? = null
    private var getLocationUpdateJob: Job? = null
    private var navigateToMainFragment: Job? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private  var currLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        setContentView(R.layout.activity_main)
        toolBar.visibility = View.GONE
//        performDefaultFunctions()

    }

    private fun performDefaultFunctions() {
        defaultJob?.cancel()
        defaultJob = lifecycleScope.launch {
            getLocationUpdates()
            navigateToMainFragment()
        }

    }

    @SuppressLint("MissingPermission")
   private suspend fun getLocationUpdates() {
        val context = this
        getLocationUpdateJob?.cancel()
        getLocationUpdateJob = lifecycleScope.launch {
            if (PermissionUtility.hasLocationPermissions(context)) {
                var request = LocationRequest().apply {
                    interval = 5000L
                    fastestInterval = 2000L
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )

            }
            getLocationUpdateJob?.join()

        }

    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            currLocation = result?.lastLocation!!


        }
    }

    private suspend fun navigateToMainFragment() {
        if (currLocation !== null) {
            var lat = currLocation!!.latitude
            var lng = currLocation!!.longitude
            var b = Bundle()
            b.putDouble("lat", lat)
            b.putDouble("lng", lng)
            nav_host_fragment_container.findNavController().navigate(R.id.logoFragment, b)
        } else {
            //     wait()
        }
    }
//    fun changeFragment(fragment: Fragment, b: Bundle?) {
//        fragment.arguments = b
//        val fragmentManager: FragmentManager = supportFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//        transaction.replace(R.id.flFragment, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//
//    }


}


