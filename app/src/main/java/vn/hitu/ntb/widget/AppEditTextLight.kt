package vn.hitu.ntb.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import vn.hitu.ntb.R
import androidx.appcompat.widget.AppCompatEditText

/**
 *    author : Bùi Hửu Thắng
 *    time   : 2022/09/17
 *    desc   : AppEditTextLight
 */
class AppEditTextLight : AppCompatEditText {
    var typeFont = ResourcesCompat.getFont(context, R.font.roboto_light)

    constructor(context: Context?) : super(context!!) {
        setFonts()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        setFonts()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        setFonts()
    }

    private fun setFonts() {
        typeface = typeFont
    }
}