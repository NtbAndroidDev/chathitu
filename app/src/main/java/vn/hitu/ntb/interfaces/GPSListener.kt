package vn.hitu.ntb.interfaces

/**
 * @Author: NGUYỄN THANH BÌNH
 * @Date: 11/19/22
 */
interface GPSListener {

    fun onSuccess(check : Boolean)
    fun onFailure(check : Boolean)
}