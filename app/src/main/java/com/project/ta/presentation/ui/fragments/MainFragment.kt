package com.project.ta.presentation.ui.fragments

import android.Manifest
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.project.ta.R
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.data.datasource.remote.LocationPhoto
import com.project.ta.domain.MapViewModel
import com.project.ta.presentation.ui.adapters.LocationListAdapter
import com.project.ta.util.PermissionUtility
import com.project.ta.util.REQUEST_CODE_LOCATION_PERMISSION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainFragment: Fragment(R.layout.fragment_main), EasyPermissions.PermissionCallbacks {

    private val mapViewModel: MapViewModel by viewModels()
    private var getMapDataJob: Job? = null
    private var getImgDataJob: Job? = null
    private var map: GoogleMap? = null
    private val locationListAdapter = LocationListAdapter(arrayListOf())
    private val photoReferenceList: MutableList<String> = arrayListOf()
    private var nearestLocationDetails: List<NearestLocationDetails>? = null
    var photoURLList: MutableList<String> = mutableListOf()
    var photoURL: String? = null
    private lateinit var photoList: List<LocationPhoto>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map = it }

        requestPermissions()
        recyclerViewLocation.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationListAdapter
        }
        populateData()
//        observeData()
//        setImages()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }


    private fun requestPermissions() {
        if (PermissionUtility.hasLocationPermissions(requireContext())) {


        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                EasyPermissions.requestPermissions(
                    this,
                    "you need to accept location permissions to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "you need to accept location permissions to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }

        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
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

    private fun populateData(){
      getMapDataJob?.cancel()
      getMapDataJob = lifecycleScope.launch {
          observeData()
//          setImages()
      }
    }



    private suspend fun observeData() {
//        mapViewModel
//        getMapDataJob?.cancel()
//        getMapDataJob = lifecycleScope.launch {
            mapViewModel.updateCurrentServices().collectLatest { it ->
//
//                it.filter { value ->
//                    photoReferenceList.add(photoList[0].photoReference) }
//                it.forEach { value ->
//
//                    photoReferenceList.add(value.photos[0].photoReference!!)
////                        it.bmp = mapViewModel.getPhoto(it.photos[0].photoReference)
//                }

                nearestLocationDetails = it
//                locationListAdapter.updateLocations(it)
                nearestLocationDetails?.forEach { value ->
                    photoReferenceList.add(value.photos[0].photoReference!!)

                Log.d("--NearestLocDetails: ", it.toString())

            }

                Log.d("--photoReferenceList: ", photoReferenceList.toString())
                locationListAdapter.updateLocations(nearestLocationDetails!!)

            }


//            photoReferenceList.forEach { it ->
//                bmp = mapViewModel.getPhoto(it)
//                bmpList.add(bmp!!)
//            }
//            for (  i in 0..nearestLocationDetails!!.size){
//                nearestLocationDetails!![i].bmp = bmpList.get(i)
//            }
//            locationListAdapter.updateLocations(nearestLocationDetails!!)
//            getMapDataJob?.join()
//        }





    }
   private suspend fun setImages(){
//       getImgDataJob?.cancel()
//       getImgDataJob = lifecycleScope.launch{

           photoReferenceList.forEach { it ->
               photoURL =  mapViewModel.getPhoto(it)
               photoURLList.add(photoURL!!)

           }
           for (  i in 0..nearestLocationDetails!!.size){
               nearestLocationDetails!![i].photoURL = photoURLList.get(i)
           }
           locationListAdapter.updateLocations(nearestLocationDetails!!)

//           }



   }
}
