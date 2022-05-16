package com.project.ta.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.ta.R
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.util.getDistanceInKms

class LocationListAdapter(var nearestLocations: ArrayList<NearestLocationDetails>): RecyclerView.Adapter<LocationListAdapter.LocationViewHolder>() {

    fun updateLocations(nearestLocationDetails: List<NearestLocationDetails>){

        nearestLocations.clear()
        nearestLocations.addAll(nearestLocationDetails)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LocationViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_location,parent,false)
    )

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(nearestLocations[position])
    }

    override fun getItemCount(): Int = nearestLocations.size

    class LocationViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val locationName =  view.findViewById<TextView>(R.id.locationName)
        private val distanceInKms = view.findViewById<TextView>(R.id.distanceInKms)
        private val locationImg = view.findViewById<ImageView>(R.id.locationImg)
//        private val progressDrawable = getProgressDrawable(view.context)
        fun bind(nearestLocationDetails: NearestLocationDetails){
            locationName.text = nearestLocationDetails.placeName

            var distance: Double = getDistanceInKms(-33.8670522,151.1957362,nearestLocationDetails.geometry.locationCoordinates.lat,nearestLocationDetails.geometry.locationCoordinates.lng)
            distanceInKms.text =String.format("%.2f", distance);
            distanceInKms.append(" KM")
        //            locationImg.loadImage(nearestLocationDetails.flag,progressDrawable)
        }
    }
}