package vn.hitu.ntb.interfaces

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
interface RequestPermissionListener {
    /**
     * first time asking multiple permissions
     * */
    fun onCallPermissionFirst(namePermission : String, requestPermission: () -> Unit)
    /**
     * Callback on permission previously denied
     * should show permission rationale and continue permission request
     */
    fun onPermissionRationaleShouldBeShown(namePermission : String, requestPermission: () -> Unit)

    /**
     * Callback on permission "Never show again" checked and denied
     * should show message and open app setting
     */
    fun onPermissionPermanentlyDenied(namePermission : String, openAppSetting: () -> Unit)

    /**
     * Callback on permission granted
     */
    fun onPermissionGranted()
}