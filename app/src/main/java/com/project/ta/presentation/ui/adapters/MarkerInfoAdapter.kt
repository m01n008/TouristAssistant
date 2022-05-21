package com.project.ta.presentation.ui.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker


class MarkerInfoAdapter(var context: Context, var sd: String) :
    InfoWindowAdapter {
    var tv_stopDetails: TextView? = null
    var stopdetails: String? = null
    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val v: View? = null
//            (context as Activity).layoutInflater.inflate(R.layout.stoppopup_design, null)
        sd = (marker.tag as String?)!!
        if (sd != null) {
//            tv_stopDetails = v.findViewById(R.id.tv_stopdetail)
//            tv_stopDetails.setText(sd)
        }
        if (marker.isInfoWindowShown && marker != null) {
            marker.hideInfoWindow()
            marker.showInfoWindow()
        }
        return v!!
    }
}