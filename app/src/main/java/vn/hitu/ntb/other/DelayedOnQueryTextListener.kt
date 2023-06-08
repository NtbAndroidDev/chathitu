package vn.hitu.ntb.other

import androidx.appcompat.widget.SearchView
import java.util.*

/**
 * @Author: Bùi Hữu Thắng
 * @Date: 30/08/2022
 */
abstract class DelayedOnQueryTextListener : SearchView.OnQueryTextListener {
    private var runnable: Runnable? = null

    private var timer: Timer = Timer()
    private val DELAY: Long = 500 // Milliseconds


    override fun onQueryTextSubmit(s: String): Boolean {
        return false
    }

    override fun onQueryTextChange(s: String): Boolean {
        timer.cancel()
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    runnable = Runnable { onDelayerQueryTextChange(s) }
                }
            },
            DELAY
        )

        return true
    }

    abstract fun onDelayerQueryTextChange(s: String?)
}