package com.project.ta.presentation.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.project.ta.R
import com.project.ta.domain.MapViewModel
import com.project.ta.presentation.ui.LocationListAdapter
import com.project.ta.util.PermissionUtility
import com.project.ta.util.REQUEST_CODE_LOCATION_PERMISSION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainFragment: Fragment(R.layout.fragment_main), EasyPermissions.PermissionCallbacks {

    private val mapViewModel: MapViewModel by viewModels()
    private var getMapDataJob: Job? = null
    private var map: GoogleMap? = null
    private val locationListAdapter = LocationListAdapter(arrayListOf())
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map = it }
        requestPermissions()
        recyclerViewLocation.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = locationListAdapter
        }

        observeData()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()

        mapView.onStart()    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    


    private fun requestPermissions(){
        if(PermissionUtility.hasLocationPermissions(requireContext())){


        }
        else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
              EasyPermissions.requestPermissions(this,
                  "you need to accept location permissions to use this app",
              REQUEST_CODE_LOCATION_PERMISSION,
              Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else{
                EasyPermissions.requestPermissions(this,
                    "you need to accept location permissions to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }

            }
        }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
        else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
       EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    private fun observeData(){
        getMapDataJob?.cancel()
        getMapDataJob = lifecycleScope.launch {
            mapViewModel.updateCurrentServices().collectLatest {
                locationListAdapter.updateLocations(it)
                Log.d("--NearestLocDetails: ",it.toString())
            }
        }


    }




}