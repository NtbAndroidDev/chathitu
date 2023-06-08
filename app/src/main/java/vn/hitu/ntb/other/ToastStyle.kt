package vn.hitu.ntb.other

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import com.hjq.toast.style.BlackToastStyle
import vn.hitu.ntb.R

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2021/02/27
 *    desc   : Toast 样式配置
 */
class ToastStyle : BlackToastStyle() {

    override fun getBackgroundDrawable(context: Context): Drawable {
        val drawable = GradientDrawable()
        // 设置颜色
        drawable.setColor(-0x78000000)
        // 设置圆角
        drawable.cornerRadius = context.resources.getDimension(vn.hitu.base.R.dimen.dp_8)
        return drawable
    }

    override fun getTextSize(context: Context): Float {
        return context.resources.getDimension(R.dimen.sp_14)
    }

    override fun getHorizontalPadding(context: Context): Int {
        return context.resources.getDimension(R.dimen.sp_16).toInt()
    }

    override fun getVerticalPadding(context: Context): Int {
        return context.resources.getDimension(R.dimen.sp_8).toInt()
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }
}