package vn.hitu.ntb.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import vn.hitu.ntb.R
import androidx.appcompat.widget.AppCompatTextView
/**
 *    author : Bùi Hửu Thắng
 *    time   : 2022/09/17
 *    desc   : AppTextView
 */
class AppTextView : AppCompatTextView {
    var typeFont = ResourcesCompat.getFont(context, R.font.roboto_regular)

    constructor(context: Context?) : super(context!!) {
        setFontsTextView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        setFontsTextView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        setFontsTextView()
    }

    private fun setFontsTextView() {
        typeface = typeFont
    }
}