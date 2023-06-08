package vn.hitu.ntb.other

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import timber.log.Timber
import vn.hitu.ntb.interfaces.PermissionResultListener
import vn.hitu.ntb.interfaces.RequestPermissionListener

/**
 * @Author: Phạm Văn Nhân
 * @Date: 29/09/2022
 */
object MultiplePermission {
    /**
     * handle request permission result with listener in activity
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun <T : Activity> T.handleOnRequestPermissionResult(
        requestPermissionCode: Int,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        permissionResultListener: PermissionResultListener
    ) {
        if (requestPermissionCode == requestCode) {
            if (isGrantedGrantResults(grantResults)) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                permissionResultListener.onPermissionGranted()
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                if (shouldShowRequestPermissionsRationale(permissions)) {
                    // permission denied
                    permissionResultListener.onPermissionRationaleShouldBeShown(getNamePermissionDeny(permissions)){
                        requestPermissions(permissions, requestCode)
                    }
                } else {
                    // permission disabled or never ask again
                    permissionResultListener.onPermissionPermanentlyDenied(getNamePermissionDeny(permissions)){
                        openAppDetailSettings()
                    }
                }
            }
        }
    }
    /**
     * handle request permission result with listener in fragment
     */
    fun <T : Fragment> T.handleOnRequestPermissionResult(
        requestPermissionCode: Int,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        permissionResultListener: PermissionResultListener
    ) {
        if (requestPermissionCode == requestCode) {
            if (isGrantedGrantResults(grantResults)) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                permissionResultListener.onPermissionGranted()
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                if (shouldShowRequestPermissionsRationale(permissions)) {
                    // permission denied
                    permissionResultListener.onPermissionRationaleShouldBeShown(context?.getNamePermissionDeny(permissions) ?: ""){
                        requestPermissions(permissions, requestCode)
                    }
                } else {
                    // permission disabled or never ask again
                    permissionResultListener.onPermissionPermanentlyDenied(context?.getNamePermissionDeny(permissions) ?: ""){
                        context?.openAppDetailSettings()
                    }
                }
            }
        }
    }
    /**
     * check grandResults are granted
     */
    private fun isGrantedGrantResults(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) return false
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    /**
     * check if should show request permissions rationale in activity
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun <T : Activity> T.shouldShowRequestPermissionsRationale(permissions: Array<out String>): Boolean {
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }
    /**
     * check if should show request permissions rationale in fragment
     */
    fun <T : Fragment> T.shouldShowRequestPermissionsRationale(permissions: Array<out String>): Boolean {
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }
    /**
     * request permissions in activity
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun <T : Activity> T.requestPermissions(
        permissions: Array<String>,
        permissionRequestCode: Int,
        requestPermissionListener: RequestPermissionListener
    ) {
        // permissions is not granted
        if (shouldAskPermissions(permissions)) {
            // permissions denied previously
            if (shouldShowRequestPermissionsRationale(permissions)) {
                requestPermissionListener.onPermissionRationaleShouldBeShown(getNamePermissionDeny(permissions)) {
                    requestPermissions(permissions, permissionRequestCode)
                }
            } else {
                // Permission denied or first time requested
                if (isFirstTimeAskingPermissions(permissions)) {
                    requestPermissionListener.onCallPermissionFirst(getNamePermissionDeny(permissions)){
                        firstTimeAskingPermissions(permissions, false)
                        // request permissions
                        requestPermissions(permissions, permissionRequestCode)
                    }
                } else {
                    // permission disabled
                    // Handle the feature without permission or ask user to manually allow permission
                    requestPermissionListener.onPermissionPermanentlyDenied(getNamePermissionDeny(permissions)) {
                        openAppDetailSettings()
                    }
                }
            }
        } else {
            // permission granted
            requestPermissionListener.onPermissionGranted()
        }
    }
    /**
     * request permissions in fragment
     */
    fun <T : Fragment> T.requestPermissions(
        permissions: Array<String>,
        permissionRequestCode: Int,
        requestPermissionListener: RequestPermissionListener
    ) {
        val context = context ?: return

        // permissions is not granted
        if (context.shouldAskPermissions(permissions)) {
            // permissions denied previously
            if (shouldShowRequestPermissionsRationale(permissions)) {
                requestPermissionListener.onPermissionRationaleShouldBeShown(context.getNamePermissionDeny(permissions)) {
                    requestPermissions(permissions, permissionRequestCode)
                }
            } else {
                // Permission denied or first time requested
                if (context.isFirstTimeAskingPermissions(permissions)) {
                    requestPermissionListener.onCallPermissionFirst(context.getNamePermissionDeny(permissions)) {
                        context.firstTimeAskingPermissions(permissions, false)
                        // request permissions
                        requestPermissions(permissions, permissionRequestCode)
                    }
                } else {
                    // permission disabled
                    // Handle the feature without permission or ask user to manually allow permission
                    requestPermissionListener.onPermissionPermanentlyDenied(context.getNamePermissionDeny(permissions)) {
                        context.openAppDetailSettings()
                    }
                }
            }
        } else {
            // permission granted
            requestPermissionListener.onPermissionGranted()
        }
    }
    /**
     * check if multiple permissions are granted or not
     */
    fun Context.shouldAskPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            val checkVal: Int = PermissionChecker.checkCallingOrSelfPermission(this, permission)
            if (checkVal != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }
    /**
     * check if multiple permissions are granted or not in fragment
     * - Note : return name permission
     */
    fun <T : Fragment> T.shouldAskNamePermissions(permissions: Array<String>): Array<String> {
        val array : Array<String> = arrayOf()
        val context = context ?: return array
        for (permission in permissions) {
            val checkVal: Int = PermissionChecker.checkCallingOrSelfPermission(context, permission)
            if (checkVal != PackageManager.PERMISSION_GRANTED) {
                Timber.d("shouldAskNamePermissions$permission")
                array.plus(permission)
            }
        }
        return array
    }
    /**
     * check if multiple permissions are granted or not in activity
     * - Note : return name permission
     */
    fun <T : Activity> T.shouldAskNamePermissions(permissions: Array<String>): Array<String> {
        val array : Array<String> = arrayOf()
        for (permission in permissions) {
            val checkVal: Int = PermissionChecker.checkCallingOrSelfPermission(this, permission)
            if (checkVal != PackageManager.PERMISSION_GRANTED) {
                array.plus(permission)
            }
        }
        return array
    }

    /**
     * Get name permission
     * */
    private fun Context.getNamePermission(permission: String) : String{
        val packageManager = applicationContext.packageManager
        val permissionInfo: PermissionInfo =
            packageManager.getPermissionInfo(permission, 0)
        return packageManager.getPermissionGroupInfo(permissionInfo.group ?: "", 0).loadLabel(packageManager).toString()
    }
    /**
     * Get name permission deny
     * */
    private fun Context.getNamePermissionDeny(permissions: Array<out String>) : String{
        val permissionsDeny : ArrayList<String> = ArrayList()
        var string = ""
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ){
                permissionsDeny.add(permission)
                string = if(string.isEmpty()){
                    getNamePermission(permission)
                }else{
                    if(!string.contains(getNamePermission(permission))){
                        string + ", " + getNamePermission(permission)
                    }else{
                        string
                    }
                }
            }
        }
        return string
    }
    /**
     * set first time asking multiple permissions
     */
    private fun Context.firstTimeAskingPermissions(permissions: Array<String>, isFirstTime: Boolean) {
        val sharedPreference: SharedPreferences? = getSharedPreferences(packageName,
            Context.MODE_PRIVATE
        )
        for (permission in permissions) {
            sharedPreference?.edit()?.putBoolean(permission, isFirstTime)?.apply()
        }
    }

    /**
     * check if first time asking multiple permissions
     */
    private fun Context.isFirstTimeAskingPermissions(permissions: Array<String>): Boolean {
        val sharedPreference: SharedPreferences? = getSharedPreferences(packageName,
            Context.MODE_PRIVATE
        )
        for (permission in permissions) {
            if (sharedPreference?.getBoolean(permission, true) == true) {
                return true
            }
        }
        return false
    }

    /**
     * open app details setting
     */
    fun Context.openAppDetailSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}