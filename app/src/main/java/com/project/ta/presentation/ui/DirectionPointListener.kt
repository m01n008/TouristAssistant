package com.project.ta.presentation.ui

import com.google.android.gms.maps.model.PolylineOptions


interface DirectionPointListener {
    fun onPath(polyLine: PolylineOptions?)
}