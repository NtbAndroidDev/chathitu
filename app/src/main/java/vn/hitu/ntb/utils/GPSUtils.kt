package vn.hitu.ntb.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.interfaces.GPSListener

/**
 * @Author: Nguyễn Thanh Bình
 * @Date: 24/10/2022
 */
@SuppressLint("WrongConstant")
class GPSUtils(context: Context, checkGPS : GPSListener) {
    private val mContext : Context = context
    private val REQUEST_CHECK_SETTINGS = 0x1
    private var locationManager : LocationManager? = null

    init {
        locationManager = mContext.getSystemService("android.content.Context.LOCATION_SERVICE") as LocationManager?
        turnOnGPS(checkGPS)
    }
    private fun turnOnGPS(checkGPS : GPSListener) {
        val locationRequest : LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000/2

        val builder : LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val settingClient : SettingsClient = LocationServices.getSettingsClient(mContext)


        val task : Task<LocationSettingsResponse> = settingClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            checkGPS.onSuccess(true)
            val user = UserCache.getUser()
            user.isGPS = 0
            UserCache.saveUser(user)

        }
        task.addOnFailureListener {
            checkGPS.onFailure( false)
            val user = UserCache.getUser()
            user.isGPS = 1
            UserCache.saveUser(user)
            if (it is ResolvableApiException){
                try {
                    val resolvableApiException : ResolvableApiException = (it as ResolvableApiException)
                    resolvableApiException.startResolutionForResult(mContext as Activity, REQUEST_CHECK_SETTINGS)
                }catch (e : Exception){
                    e.printStackTrace()
                }

            }
        }
    }


}