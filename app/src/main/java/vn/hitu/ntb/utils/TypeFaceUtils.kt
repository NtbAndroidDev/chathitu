package vn.hitu.ntb.utils

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import vn.hitu.ntb.R

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
object TypeFaceUtils {
    private var robotoBoldTypeface: Typeface? = null
    private var robotoItalicTypeface: Typeface? = null
    private var robotoLightTypeface: Typeface? = null
    private var robotoMediumTypeface: Typeface? = null
    private var robotoRegularTypeface: Typeface? = null
    private var robotoThinTypeface: Typeface? = null

    fun getRobotoBoldTypeface(context: Context?): Typeface? {
        if (robotoBoldTypeface == null) {
            robotoBoldTypeface = ResourcesCompat.getFont(
                context!!, R.font.roboto_bold
            )
        }
        return robotoBoldTypeface
    }

    fun getRobotoItalicTypeface(context: Context?): Typeface? {
        if (robotoItalicTypeface == null) {
            robotoItalicTypeface = ResourcesCompat.getFont(
                context!!, R.font.roboto_italic
            )
        }
        return robotoItalicTypeface
    }

    fun getRobotoLightTypeface(context: Context?): Typeface? {
        if (robotoLightTypeface == null) {
            robotoLightTypeface = ResourcesCompat.getFont(
                context!!, R.font.roboto_light
            )
        }
        return robotoLightTypeface
    }

    fun getRobotoMediumTypeface(context: Context?): Typeface? {
        if (robotoMediumTypeface == null) {
            robotoMediumTypeface = ResourcesCompat.getFont(
                context!!, R.font.roboto_medium
            )
        }
        return robotoMediumTypeface
    }

    fun getRobotoRegularTypeface(context: Context?): Typeface? {
        if (robotoRegularTypeface == null) {
            robotoRegularTypeface = ResourcesCompat.getFont(
                context!!, R.font.roboto_regular
            )
        }
        return robotoRegularTypeface
    }

    fun getRobotoThinTypeface(context: Context): Typeface? {
        if (robotoThinTypeface == null) {
            robotoThinTypeface = ResourcesCompat.getFont(context, R.font.roboto_thin)
        }
        return robotoThinTypeface
    }
}