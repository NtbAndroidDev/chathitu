package vn.hitu.ntb.other

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher

/**
 * @Author: Bùi Hữu Thắng
 * @Date: 30/08/2022
 */
abstract class DelayedOnTextChanged : TextWatcher {
    private val handler = Handler()
    private var runnable: Runnable? = null

    abstract fun onDelayerTextChange(s: Editable?)

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        handler.removeCallbacks(runnable!!)
        runnable = Runnable { onDelayerTextChange(s) }
        handler.postDelayed(runnable!!, 2000)
    }
}