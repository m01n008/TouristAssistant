package com.project.ta.presentation.ui

import android.R
import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ThemedSpinnerAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.project.ta.BuildConfig
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class GetPathFromLocation(
    private val context: Context,
    private val source: LatLng,
    private val destination: LatLng,
    private val wayPoint: ArrayList<LatLng>,
    private val mMap: GoogleMap,
    private val animatePath: Boolean,
    private val repeatDrawingPath: Boolean,
    resultCallback: DirectionPointListener?
) :
    AsyncTask<String?, Void?, PolylineOptions?>() {
    private val TAG = "GetPathFromLocation"
    private val resultCallback: DirectionPointListener?
    @Synchronized
    fun getUrl(source: LatLng, dest: LatLng, wayPoint: ArrayList<LatLng>): String {
        var url =
            ("https://maps.googleapis.com/maps/api/directions/json?sensor=false&mode=driving&origin="
                    + source.latitude + "," + source.longitude + "&destination=" + dest.latitude + "," + dest.longitude)
        for (centerPoint in wayPoint.indices) {
            url = if (centerPoint == 0) {
                url + "&waypoints=optimize:true|" + wayPoint[centerPoint].latitude + "," + wayPoint[centerPoint].longitude
            } else {
                url + "|" + wayPoint[centerPoint].latitude + "," + wayPoint[centerPoint].longitude
            }
        }
        url = url + "&key=" + BuildConfig.MAP_API_KEY
        ThemedSpinnerAdapter.Helper(context).dropDownViewTheme?.dump(1,"ThemedSpinnerAdapter","\"Direction_URL: $url\"")
        return url
    }

    val randomColor: Int
        get() {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }


    override fun doInBackground(vararg p0: String?): PolylineOptions? {
        val data: String
        return try {
            var inputStream: InputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val directionUrl = URL(
                    getUrl(
                        source,
                        destination,
                        wayPoint
                    )
                )
                connection = directionUrl.openConnection() as HttpURLConnection
                connection.connect()
                inputStream = connection!!.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuffer = StringBuffer()
                var line: String? = ""
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuffer.append(line)
                }
                data = stringBuffer.toString()
                bufferedReader.close()
            } catch (e: Exception) {
                Log.e(TAG, "Exception : $e")
                return null
            } finally {
                inputStream!!.close()
                connection!!.disconnect()
            }
            Log.e(TAG, "Background Task data : $data")

            //Second AsyncTask
            val jsonObject: JSONObject
            var routes: ArrayList<ArrayList<HashMap<String, String>>> = arrayListOf()
            try {
                jsonObject = JSONObject(data)
                // Starts parsing data
                val helper = DirectionHelper()
                routes = helper.parse(jsonObject)
                Log.e(TAG, "Executing Routes : " /*, routes.toString()*/)

                //Third AsyncTask
                var points: ArrayList<LatLng?>
                var lineOptions: PolylineOptions? = null

                // Traversing through all the routes
                if (routes != null) {
                    for (i in routes.indices) {
                        points = ArrayList()
                        lineOptions = PolylineOptions()

                        // Fetching i-th route
                        val path = routes[i]

                        // Fetching all the points in i-th route
                        for (j in path.indices) {
                            val point = path[j]
                            val lat = point["lat"]!!.toDouble()
                            val lng = point["lng"]!!.toDouble()
                            val position = LatLng(lat, lng)
                            points.add(position)
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points)
                        lineOptions.width(8f)
                        lineOptions.color(Color.BLACK)
                        //lineOptions.color(getRandomColor());
                        if (animatePath) {
                            val finalPoints = points
                            (context as AppCompatActivity).runOnUiThread {
                                var polylineOptions: PolylineOptions
                                val greyPolyLine: Polyline
                                val blackPolyline: Polyline
                                val polylineAnimator: ValueAnimator
                                val builder = LatLngBounds.Builder()
                                for (latLng in finalPoints) {
                                    builder.include(latLng)
                                }
                                polylineOptions = PolylineOptions()
                                polylineOptions.color(Color.GRAY)
                                polylineOptions.width(8f)
                                polylineOptions.startCap(SquareCap())
                                polylineOptions.endCap(SquareCap())
                                polylineOptions.jointType(JointType.ROUND)
                                polylineOptions.addAll(finalPoints)
                                greyPolyLine = mMap.addPolyline(polylineOptions)
                                polylineOptions = PolylineOptions()
                                polylineOptions.width(8f)
                                polylineOptions.color(Color.BLACK)
                                polylineOptions.startCap(SquareCap())
                                polylineOptions.endCap(SquareCap())
                                polylineOptions.zIndex(5f)
                                polylineOptions.jointType(JointType.ROUND)
                                blackPolyline = mMap.addPolyline(polylineOptions)
                                polylineAnimator = ValueAnimator.ofInt(0, 100)
                                polylineAnimator.duration = 2000
                                polylineAnimator.interpolator = LinearInterpolator()
                                polylineAnimator.addUpdateListener { valueAnimator ->
                                    val points = greyPolyLine.points
                                    val percentValue = valueAnimator.animatedValue as Int
                                    val size = points.size
                                    val newPoints = (size * (percentValue / 100.0f)).toInt()
                                    val p: List<LatLng> =
                                        points.subList(0, newPoints)
                                    blackPolyline.points = p
                                }
                                polylineAnimator.addListener(object :
                                    Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {}
                                    override fun onAnimationEnd(animation: Animator) {
                                        if (repeatDrawingPath) {
                                            val greyLatLng = greyPolyLine.points
                                            greyLatLng?.clear()
                                            polylineAnimator.start()
                                        }
                                    }

                                    override fun onAnimationCancel(animation: Animator) {
                                        polylineAnimator.cancel()
                                    }

                                    override fun onAnimationRepeat(animation: Animator) {}
                                })
                                polylineAnimator.start()
                            }
                        }
                        Log.e(TAG, "PolylineOptions Decoded")
                    }
                }

                // Drawing polyline in the Google Map for the i-th route
                lineOptions
            } catch (e: Exception) {
                Log.e(TAG, "Exception in Executing Routes : $e")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Background Task Exception : $e")
            null
        }
    }

    override fun onPostExecute(polylineOptions: PolylineOptions?) {
        super.onPostExecute(polylineOptions)
        if (resultCallback != null && polylineOptions != null) resultCallback.onPath(polylineOptions)
    }

    //https://www.mytrendin.com/draw-route-two-locations-google-maps-android/
    //https://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
    init {
        this.resultCallback = resultCallback
    }


}