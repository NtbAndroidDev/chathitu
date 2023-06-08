package vn.hitu.ntb.action

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import vn.hitu.ntb.R
import vn.hitu.ntb.widget.StatusLayout

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/08
 *    desc   : 状态布局意图
 */
interface StatusAction {

    /**
     * 获取状态布局
     */
    fun getStatusLayout(): StatusLayout?

    /**
     * 显示加载中
     */
    fun showLoading(@RawRes id: Int = R.raw.loading) {
        getStatusLayout()?.let {
            it.show()
            it.setAnimResource(id)
            it.setHint("")
            it.setOnRetryListener(null)
        }
    }

    /**
     * 显示加载完成
     */
    fun showComplete() {
        getStatusLayout()?.let {
            if (!it.isShow()) {
                return
            }
            it.hide()
        }
    }

    /**
     * 显示空提示
     */
    fun showEmpty() {
        showLayout(R.drawable.status_empty_ic, R.string.status_layout_no_data, null)
    }

    /**
     * 显示错误提示
     */
    @SuppressLint("MissingPermission")
    fun showError(listener: StatusLayout.OnRetryListener?) {
        getStatusLayout()?.let {
            val manager: ConnectivityManager? = ContextCompat.getSystemService(it.context, ConnectivityManager::class.java)
            if (manager != null) {
                val info: NetworkInfo? = manager.activeNetworkInfo
                // Xác định xem mạng đã được kết nối chưa
                if (info == null || !info.isConnected) {
                    showLayout(R.drawable.status_empty_ic, R.string.status_layout_error_network, listener)
                    return
                }
            }
            showLayout(R.drawable.status_empty_ic, R.string.status_layout_error_request, listener)
        }
    }

    /**
     * 显示自定义提示
     */
    fun showLayout(@DrawableRes drawableId: Int, @StringRes stringId: Int, listener: StatusLayout.OnRetryListener?) {
        getStatusLayout()?.let {
            showLayout(ContextCompat.getDrawable(it.context, drawableId), it.context.getString(stringId), listener)
        }
    }

    fun showLayout(drawable: Drawable?, hint: CharSequence?, listener: StatusLayout.OnRetryListener?) {
        getStatusLayout()?.let {
            it.show()
            it.setIcon(drawable)
            it.setHint(hint)
            it.setOnRetryListener(listener)
        }
    }
}