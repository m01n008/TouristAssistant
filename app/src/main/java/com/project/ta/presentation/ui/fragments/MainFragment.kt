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




@AndroidEntryPoint
class MainFragment: Fragment(R.layout.fragment_main), EasyPermissions.PermissionCallbacks {

    private val mapViewModel: MapViewModel by viewModels()
    private var getMapDataJob: Job? = null
    private var getImgDataJob: Job? = null
    private var map: GoogleMap? = null

    private val photoReferenceList: MutableList<String> = arrayListOf()
    private val markerList: ArrayList<LatLng> = arrayListOf()
    private val markerImages: MutableList<String> = arrayListOf()
    var source: LatLng = LatLng(0.0,0.0)
    var destination:LatLng = LatLng(0.0,0.0)
    private var nearestLocationDetails: List<NearestLocationDetails>? = null
    private val waypoints: ArrayList<LatLng> = arrayListOf()
//    private lateinit var b: Bundle
    var photoURLList: MutableList<String> = mutableListOf()
    var photoURL: String? = null
    private lateinit var photoList: List<LocationPhoto>
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var activityContext: Context
    private var currLocLatLng: LatLng? = null
    private val locationListAdapter = LocationListAdapter(arrayListOf(),map,currLocLatLng)
    private var getLocationUpdateJob: Job? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient  = LocationServices.getFusedLocationProviderClient(activityContext)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        b = savedInstanceState!!
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
       }

        btnShowAttracion.setOnClickListener(View.OnClickListener {
            populateData()
        })
//        b = Bundle()
//        b.putDouble("lat",map?.myLocation!!.latitude)
//        b.putDouble("lng",map?.myLocation!!.longitude)
//          populateData()
//        mapView.getMapAsync

        requestPermissions()
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
        lifecycleScope.launch{
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

    private fun populateData(){
      getMapDataJob?.cancel()
      getMapDataJob = lifecycleScope.launch {
          getCurrentLocation()
//          observeData(currLocLatLng!!.latitude,currLocLatLng!!.longitude)
//          setImages()
      }
    }




    @SuppressLint("LongLogTag", "MissingPermission")
    private suspend fun observeData(lat: Double,lng: Double) {
         var c = 0;
//        mapViewModel
//        getMapDataJob?.cancel()
//        getMapDataJob = lifecycleScope.launch {
//           mapViewModel.upd
            startPostponedEnterTransition()
            mapViewModel.updateCurrentServices(lat,lng).collectLatest { it ->

                c++
                Log.d("--insideUpdateCurrentServices call: ",c.toString())
                nearestLocationDetails = it
//                locationListAdapter.updateLocations(it)
                nearestLocationDetails?.forEach { item ->
                    if(item.photos != null)
                        if(item.photos[0].photoReference !== null){
                            photoReferenceList.add(item.photos!![0].photoReference!!)
                        }

                    markerList.add(LatLng(item.geometry!!.locationCoordinates.lat,item.geometry!!.locationCoordinates.lng))
//                    markerImages.add(item.icon)
                    map?.addMarker(MarkerOptions().position(LatLng(item.geometry!!.locationCoordinates.lat,item.geometry!!.locationCoordinates.lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(70,70, R.drawable.pin_32))))
                    Log.d("--NearestLocDetails: ", item.toString())

            }
//                createRoute(markerList.first(),markerList.get(markerList.size - 1),waypoints)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(markerList.get(markerList.size - 1),12.0F))
                map?.isMyLocationEnabled = true
//                GoogleMap.OnCircleClickListener {
//                    map?.addMarker(MarkerOptions().
//                    position(it.center)
//                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(70,70, R.drawable.stop_point))))
//
//                }

//                map?.addPolyline(PolylineOptions().addAll(markerList))
//                markerList.forEach{ it ->
//                }
                Log.d("--photoReferenceList: ", photoReferenceList.toString())

                    locationListAdapter.updateLocations(nearestLocationDetails!!,map,currLocLatLng)

                }


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
           locationListAdapter.updateLocations(nearestLocationDetails!!, map, currLocLatLng)

//           }



   }

  private  fun createRoute(source: LatLng,destination: LatLng,wayPoint: ArrayList<LatLng>){
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
    private suspend fun getCurrentLocation(){
        val locationManager = activity
            ?.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager

        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            var location: Location? = it.result

            if(location != null){
                currLocLatLng = LatLng(location!!.latitude,location!!.longitude)
                Log.d("--location!!: ",currLocLatLng.toString())
                lifecycleScope.launch {
                    observeData(currLocLatLng!!.latitude,currLocLatLng!!.longitude)
                }


            }
            else{
                lifecycleScope.launch{
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
            currLocLatLng = LatLng(result?.lastLocation!!.latitude,result?.lastLocation!!.longitude)
            Log.d("--locationUpdates!!: ",currLocLatLng.toString())
            lifecycleScope.launch {
                observeData(currLocLatLng!!.latitude,currLocLatLng!!.longitude)
            }
        }
    }
//    private fun createRoute(orgLat: Double, orgLong: Double, destLat: Double, destLng: Double) {
//        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
//
//        //Toast.makeText(getContext(), "I am called", Toast.LENGTH_SHORT).show();
//        val context = GeoApiContext.Builder()
//            .apiKey(BuildConfig.MAP_API_KEY)
//            .build()
//        val path: MutableList<LatLng?> = arrayListOf()
//
//        // DirectionsApiRequest req = DirectionsApi.getDirections(context, "" + orgLat + "," + orgLong, "" + destLat + "," + destLng);
//        try {
//
////            req.waypoints(waypoints());
////            req.optimizeWaypoints(true);
////            DirectionsResult res = req.await();
//            val req: DirectionsApiRequest =
//                DirectionsApi.newRequest(context).origin(com.google.maps.model.LatLng(orgLat, orgLong))
//                    .destination(com.google.maps.model.LatLng(destLat, destLng))
//            req.waypoints(waypoints())
//            //Loop through legs and steps to get encoded polylines of each step
//            // req.waypoints(waypoints());
//            val res: DirectionsResult = req.await()
//            if (res.routes != null && res.routes.size > 0) {
//                val route: DirectionsRoute = res.routes.get(0)
//                // Toast.makeText(getContext(),""+route,Toast.LENGTH_LONG).show();
//                if (route.legs != null) {
//                    for (i in 0 until route.legs.size) {
//                        val leg: DirectionsLeg = route.legs.get(i)
//                        if (leg.steps != null) {
//                            for (j in 0 until leg.steps.size) {
//                                val step: DirectionsStep = leg.steps.get(j)
//                                if (step.steps != null && step.steps.size > 0) {
//                                    for (k in 0 until step.steps.size) {
//                                        val step1: DirectionsStep = step.steps.get(k)
//                                        val points1: EncodedPolyline = step1.polyline
//                                        if (points1 != null) {
//                                            //Decode polyline and add points to list of route coordinates
//                                            val coords1: MutableList<com.google.maps.model.LatLng>? = points1.decodePath()
//                                            if (coords1 != null) {
//                                                for (coord1 in coords1) {
//                                                    path.add(LatLng(coord1.lat, coord1.lng))
//                                                }
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    val points: EncodedPolyline = step.polyline
//                                    if (points != null) {
//                                        //Decode polyline and add points to list of route coordinates
//                                        val coords: MutableList<com.google.maps.model.LatLng>? = points.decodePath()
//                                        if (coords != null) {
//                                            for (coord in coords) {
//                                                path.add(LatLng(coord.lat, coord.lng))
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (ex: Exception) {
//            Log.e("exception", ex.localizedMessage)
//            Toast.makeText(getContext(), "" + ex.localizedMessage, Toast.LENGTH_LONG).show()
//        }
//
////        if(mMap != null){
////            mMap.clear();
////        }
//
//
//        //Draw the polyline
//           val opts = PolylineOptions().addAll(path).color(Color.BLUE)
//                .width(0.2F)
//            map?.addPolyline(opts)
//            //  zoomRoute(mMap,path);
//        }
////        for (k in stops.indices) {
////            try {
////                val stoplat: Double = stops.get(k).latitude
////                val stoplng: Double = stops.get(k).longitude
////                // mMap.addMarker(new MarkerOptions().position(new LatLng(stoplat, stoplng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)).title("Stop"));
////            } catch (ex: Exception) {
////            }
////        }
//        fromPosition = LatLng(orgLat, orgLong)
//        toPosition = LatLng(destLat, destLng)
//        mMap.addMarker(MarkerOptions().position(fromPosition).title("Start"))
//        mMap.addMarker(MarkerOptions().position(toPosition).title("End"))
//
////        for(int k=0;k<routepath.size();k++){
////            marker = mMap.addMarker(new MarkerOptions().position(routepath.get(k)).icon(BitmapDescriptorFactory.fromBitmap(resizeMarker(100, 100, R.drawable.tanker))));
////        }
//        if (toPosition != null) {
//            val boundsBuilder = LatLngBounds.Builder()
//            boundsBuilder.include(fromPosition)
//            boundsBuilder.include(toPosition)
//            NetworkConsume.getInstance().hideProgress()
//            val bounds = boundsBuilder.build()
//            //    viewRoute(mMap);
//            mMap.setOnMapLoadedCallback(OnMapLoadedCallback {
//                mMap.moveCamera(
//                    CameraUpdateFactory.newLatLngBounds(
//                        bounds,
//                        16
//                    )
//                )
//            })
//        }
//        mMap.setOnMapClickListener(OnMapClickListener {
//            //                Intent i = new Intent(getContext(), MapDetailActivity.class);
////                i.putExtra("routeid", RoutesAdapter.currentRouteClicked.getRouteID());
////                startActivity(i);
//        })
//        mMap.getUiSettings().setZoomControlsEnabled(true)
//    }
//    fun waypoints(): String {
//        var points = ""
//        for (i in waypoints.indices) {
////            if(i==0)
////            {
////                points=points+""+stops.get(i);
////            }
////            else {
////                points=points+","+stops.get(i);
////            }
//            points = points + "" + waypoints[i]
//            points = "$points|"
//        }
//        return points
//    }
//
//    private fun getBearing(begin: LatLng, end: LatLng): Float {
//        val lat = Math.abs(begin.latitude - end.latitude)
//        val lng = Math.abs(begin.longitude - end.longitude)
//        if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
//            Math.atan(lng / lat)
//        )
//            .toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
//            Math.atan(lng / lat)
//        ) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
//            Math.atan(lng / lat)
//        ) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
//            Math.atan(lng / lat)
//        ) + 270).toFloat()
//        return (-1).toFloat()
//    }



}

