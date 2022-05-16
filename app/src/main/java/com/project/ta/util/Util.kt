package com.project.ta.util



   fun getDistanceInKms(
    originLat: Double,
    originLong: Double,
    destLat: Double,
    destLng: Double): Double
    {
        return distance(originLat,
            originLong,
            destLat,
            destLng) * 1.609344
    }



    private fun distance(
        originLat: Double,
        originLong: Double,
        destLat: Double,
        destLng: Double
    ): Double {
        val theta = originLong - destLng
        var dist =
            Math.sin(deg2rad(originLat)) * Math.sin(deg2rad(destLat)) + Math.cos(deg2rad(originLat)) * Math.cos(
                deg2rad(destLat)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
