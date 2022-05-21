package com.project.ta.presentation.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng
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
import java.util.ArrayList
import com.google.android.gms.maps.model.PolylineOptions

import com.project.ta.presentation.ui.DirectionPointListener


import com.project.ta.presentation.ui.GetPathFromLocation
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), EasyPermissions.PermissionCallbacks {


    companion object {
        private var LOCATION_PERMISSION_GRANTED = false
    }

    private val mapViewModel: MapViewModel by viewModels()
    private var getMapDataJob: Job? = null
    private var getImgDataJob: Job? = null
    private var map: GoogleMap? = null

    private val photoReferenceList: MutableList<String> = arrayListOf()
    private val markerList: ArrayList<LatLng> = arrayListOf()
    private val markerImages: MutableList<String> = arrayListOf()
    var source: LatLng = LatLng(0.0, 0.0)
    var destination: LatLng = LatLng(0.0, 0.0)
    private var nearestLocationDetails: List<NearestLocationDetails>? = null
    private val waypoints: ArrayList<LatLng> = arrayListOf()

    //    private lateinit var b: Bundle
    var photoURLList: MutableList<String> = mutableListOf()
    var photoURL: String? = null
    private lateinit var photoList: List<LocationPhoto>
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var activityContext: Context
    var fragmentContext: Context? = null
    private var currLocLatLng: LatLng? = null
    private val locationListAdapter =
        LocationListAdapter(arrayListOf(), map, currLocLatLng, fragmentContext)
    private var getLocationUpdateJob: Job? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
        fragmentContext = context
        activity?.toolBar?.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activityContext)
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
        }
        if (LOCATION_PERMISSION_GRANTED) {
            populateData()
        } else {
            requestPermissions()
        }



        btnShowAttracion.setOnClickListener(View.OnClickListener {
            populateData()
        })



        recyclerViewLocation.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationListAdapter
        }

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
            LOCATION_PERMISSION_GRANTED = true
            populateData()

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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        lifecycleScope.launch {
            LOCATION_PERMISSION_GRANTED = true
            getCurrentLocation()

        }

    }

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

    private fun populateData() {
        getMapDataJob?.cancel()
        getMapDataJob = lifecycleScope.launch {
            getCurrentLocation()
//          moveCameratoCurrentLocation()
//          observeData(currLocLatLng!!.latitude,currLocLatLng!!.longitude)
//         setImages()
        }
    }


    @SuppressLint("LongLogTag", "MissingPermission")
    private suspend fun observeData(lat: Double, lng: Double) {
        var c = 0;
//        mapViewModel
        getMapDataJob?.cancel()
        getMapDataJob = lifecycleScope.launch {
//           mapViewModel.upd
            progress_circular.visibility = View.VISIBLE
            mapViewModel.updateCurrentAttractions(lat, lng).collectLatest { it ->
                progress_circular.visibility = View.GONE
                c++
                Log.d("--insideUpdateCurrentServices call: ", c.toString())
                nearestLocationDetails = it
//                locationListAdapter.updateLocations(it)
                nearestLocationDetails?.forEach { item ->
                    if (item.photos != null)
                        if (item.photos[0].photoReference !== null) {
                            photoReferenceList.add(item.photos!![0].photoReference!!)
                        } else {
                            photoReferenceList.add("")

                        }

                    markerList.add(
                        LatLng(
                            item.geometry!!.locationCoordinates.lat,
                            item.geometry!!.locationCoordinates.lng
                        )
                    )
                    map?.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    item.geometry!!.locationCoordinates.lat,
                                    item.geometry!!.locationCoordinates.lng
                                )
                            )
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    resizeMarker(
                                        70,
                                        70,
                                        R.drawable.pin_32
                                    )
                                )
                            )
                    )!!.title = item.placeName
//                        .tag = item.placeName +"\n"+
//                                  item.geometry.locationCoordinates.toString()+""

                    Log.d("--NearestLocDetails: ", item.toString())

                }
//                createRoute(markerList.first(),markerList.get(markerList.size - 1),waypoints)
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        markerList.get(markerList.size - 1),
                        12.0F
                    )
                )
                map?.isMyLocationEnabled = true
                Log.d("--photoReferenceList: ", photoReferenceList.toString())

                locationListAdapter.updateLocations(
                    nearestLocationDetails!!,
                    map,
                    currLocLatLng,
                    fragmentContext
                )

            }
//            photoReferenceList.forEach { it ->
//
//                photoURL =  mapViewModel.getPhoto(it)
//                photoURLList.add(photoURL!!)
//
//            }
        }
        getMapDataJob?.join()

    }


    private suspend fun setImages() {
        getImgDataJob?.cancel()
        getImgDataJob = lifecycleScope.launch {

            photoReferenceList.forEach { it ->
                photoURL = mapViewModel.getPhoto(it).toString()
                photoURLList.add(photoURL!!)

            }
            for (i in 0..nearestLocationDetails!!.size) {
                nearestLocationDetails!![i].photoURL = photoURLList.get(i)
            }
            locationListAdapter.updateLocations(
                nearestLocationDetails!!,
                map,
                currLocLatLng,
                fragmentContext
            )
        }
    }

    private suspend fun moveCameratoCurrentLocation() {
        lifecycleScope.launch {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocLatLng, 20.0F))

        }

    }

    private fun createRoute(source: LatLng, destination: LatLng, wayPoint: ArrayList<LatLng>) {
        map?.let {
            GetPathFromLocation(
                requireContext(),
                source,
                destination,
                wayPoint,
                it,
                true,
                false,
                object : DirectionPointListener {
                    override fun onPath(polyLine: PolylineOptions?) {

                    }
                }).execute()
        }
    }

    fun resizeMarker(width: Int, height: Int, drawable: Int): Bitmap? {
        val bitmapdraw = resources.getDrawable(drawable) as BitmapDrawable
        val b = bitmapdraw.bitmap
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation() {
        val locationManager = activity
            ?.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager

        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            var location: Location? = it.result

            if (location != null) {
                currLocLatLng = LatLng(location!!.latitude, location!!.longitude)
                Log.d("--location!!: ", currLocLatLng.toString())
                lifecycleScope.launch {
                    observeData(currLocLatLng!!.latitude, currLocLatLng!!.longitude)
                }
            } else {
                lifecycleScope.launch {
                    getLocationUpdates()
                }

            }


        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLocationUpdates() {
        val context = this
        getLocationUpdateJob?.cancel()
        getLocationUpdateJob = lifecycleScope.launch {
            if (PermissionUtility.hasLocationPermissions(activityContext)) {
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
            currLocLatLng =
                LatLng(result?.lastLocation!!.latitude, result?.lastLocation!!.longitude)
            Log.d("--locationUpdates!!: ", currLocLatLng.toString())
            lifecycleScope.launch {
                observeData(currLocLatLng!!.latitude, currLocLatLng!!.longitude)
            }
        }
    }

}

