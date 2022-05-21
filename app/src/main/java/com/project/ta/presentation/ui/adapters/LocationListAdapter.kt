package com.project.ta.presentation.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.project.ta.R
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.util.getDistanceInKms
import com.project.ta.util.getProgressDrawable
import com.project.ta.util.loadImage

class LocationListAdapter(private var nearestLocations: ArrayList<NearestLocationDetails>,
private var map: GoogleMap?, private var currLocLatLng: LatLng?,private var  fragmentContext: Context? ): RecyclerView.Adapter<LocationListAdapter.LocationViewHolder>() {


    fun updateLocations(
        nearestLocationDetails: List<NearestLocationDetails>,
        mMap: GoogleMap?,
        currLocLatLng: LatLng?,
        fragmentContext: Context?
    ){
        this.fragmentContext = fragmentContext
        this.currLocLatLng = currLocLatLng
        this.map = mMap
        nearestLocations.clear()
        nearestLocations.addAll(nearestLocationDetails)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LocationViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_location,parent,false)
    )

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(nearestLocations[position],map,currLocLatLng,fragmentContext)
    }

    override fun getItemCount(): Int = nearestLocations.size

    class LocationViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val locationName =  view.findViewById<TextView>(R.id.locationName)
        private val distanceInKms = view.findViewById<TextView>(R.id.distanceInKms)
        private val locationImg = view.findViewById<ImageView>(R.id.locationImg)
        private val progressDrawable = getProgressDrawable(view.context)
        private  var v = view;
        fun bind(
            nearestLocationDetails: NearestLocationDetails,
            map: GoogleMap?,
            currLocLatLng: LatLng?,
            fragmentContext: Context?
        ){
            locationName.text = nearestLocationDetails.placeName

            var distance: Double = getDistanceInKms(currLocLatLng!!.latitude,currLocLatLng!!.longitude,nearestLocationDetails.geometry.locationCoordinates.lat,nearestLocationDetails.geometry.locationCoordinates.lng)
            distanceInKms.text =String.format("%.2f", distance);
            distanceInKms.append(" KM")
            locationImg.loadImage("https://lh3.googleusercontent.com/places/AAcXr8rRpe_CMob4PUBIsNvLl9sZICQaU3klJhEIM_z2miU3yWlqkr1RoUJmlyMOF7yvZvvdwTaoNj9cY7LEL7TF_QVZhFHIp_v_ROg=s1600-w400",progressDrawable)

//            locationImg.loadImage(" https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference="+nearestLocationDetails.photos.get(0).photoReference+"&key=${BuildConfig.MAP_API_KEY}",progressDrawable)

        //            locationImg.loadImage(nearestLocationDetails.photoURL,progressDrawable)
            v.setOnClickListener(View.OnClickListener {

                map?.animateCamera(CameraUpdateFactory.
                newLatLngZoom(LatLng(nearestLocationDetails.geometry.locationCoordinates.lat,
                    nearestLocationDetails.geometry.locationCoordinates.lng),20.0F))


            })
            v.setOnLongClickListener(View.OnLongClickListener {
               val gmmIntentUri   = Uri.parse("google.navigation:q="
                       +nearestLocationDetails.geometry.locationCoordinates.lat+
                       ","+nearestLocationDetails.geometry.locationCoordinates.lng)
               val mapIntent: Intent  = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps");
                var b: Bundle? = Bundle()
                startActivity(fragmentContext!!,mapIntent,b)
                return@OnLongClickListener true
            })
        }
    }
}