package com.project.ta.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.room.TypeConverter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.project.ta.R
import kotlinx.coroutines.NonCancellable.start
import java.io.ByteArrayOutputStream
import java.io.File


fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }


    fun fromBitmap(bmp: Bitmap): ByteArray{
        val outputStream  = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        return outputStream.toByteArray()
    }
    fun filetoBitmap(file: File): Bitmap? {
        val filePath: String = file.getPath()
        val bitmap = BitmapFactory.decodeFile(filePath)
         return bitmap
    }

fun getProgressDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 10f
            centerRadius = 50f
            start()
        }
    }

    fun ImageView.loadImage(photoURL: String?, progressDrawable: CircularProgressDrawable) {
        val requestOptions = RequestOptions()
            .placeholder(progressDrawable)
            .error(R.mipmap.ic_launcher_round)
        Glide.with(this.context).setDefaultRequestOptions(requestOptions)
            .load(photoURL)
            .into(this)
    }


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
