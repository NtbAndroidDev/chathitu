package vn.hitu.ntb.widget

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import vn.hitu.ntb.R

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class AppButtonBold : AppCompatButton {
    var typeFont = ResourcesCompat.getFont(context, R.font.roboto_medium)

    constructor(context: Context?) : super(context!!) {
        typeface = typeFont
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        typeface = typeFont
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        typeface = typeFont
    }
}