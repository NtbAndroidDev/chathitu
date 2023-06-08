package vn.hitu.ntb.ui.dialog

import android.content.*
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.hitu.base.BaseDialog
import vn.hitu.ntb.R
import vn.hitu.ntb.aop.SingleClick
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.manager.PickerLayoutManager
import java.util.*

/**
 * @Author: HỒ QUANG TÙNG
 * @Date: 19/10/2022
 */
class TimeDialog {

    class Builder(context: Context) : CommonDialog.Builder<Builder>(context) {

        private val hourView: RecyclerView? by lazy { findViewById(R.id.rvTimeHour) }
        private val minuteView: RecyclerView? by lazy { findViewById(R.id.rvTimeMinute) }

        private val hourManager: PickerLayoutManager
        private val minuteManager: PickerLayoutManager
        private val secondManager: PickerLayoutManager

        private val hourAdapter: PickerAdapter
        private val minuteAdapter: PickerAdapter
        private val secondAdapter: PickerAdapter

        private var listener: OnListener? = null

        init {
            setCustomView(R.layout.time_dialog)
            setTitle(R.string.time_title)
            hourAdapter = PickerAdapter(context)
            minuteAdapter = PickerAdapter(context)
            secondAdapter = PickerAdapter(context)

            val hourData = ArrayList<String?>(24)
            for (i in 0..23) {
                hourData.add((if (i < 10) "0" else "") + i + " " + getString(R.string.common_hour))
            }

            val minuteData = ArrayList<String?>(60)
            for (i in 0..59) {
                minuteData.add((if (i < 10) "0" else "") + i + " " + getString(R.string.common_minute))
            }

            val secondData = ArrayList<String?>(60)
            for (i in 0..59) {
                secondData.add((if (i < 10) "0" else "") + i + " " + getString(R.string.common_second))
            }

            hourAdapter.setData(hourData)
            minuteAdapter.setData(minuteData)
            secondAdapter.setData(secondData)

            hourManager = PickerLayoutManager.Builder(context).build()
            minuteManager = PickerLayoutManager.Builder(context).build()
            secondManager = PickerLayoutManager.Builder(context).build()

            hourView?.layoutManager = hourManager
            minuteView?.layoutManager = minuteManager

            hourView?.adapter = hourAdapter
            minuteView?.adapter = minuteAdapter

            val calendar = Calendar.getInstance()
            setHour(calendar[Calendar.HOUR_OF_DAY])
            setMinute(calendar[Calendar.MINUTE])
        }

        fun setListener(listener: OnListener?): Builder = apply {
            this.listener = listener
        }


        fun setTime(time: String?): Builder = apply {
            if (time == null) {
                return@apply
            }
            // 102030
            if (time.matches(Regex("\\d{6}"))) {
                setHour(time.substring(0, 2))
                setMinute(time.substring(2, 4))

                // 10:20:30
            } else if (time.matches(Regex("\\d{2}:\\d{2}:\\d{2}"))) {
                setHour(time.substring(0, 2))
                setMinute(time.substring(3, 5))
            }
        }

        private fun setHour(hour: String?): Builder = apply {
            if (hour == null) {
                return@apply
            }
            setHour(hour.toInt())
        }

        private fun setHour(hour: Int): Builder = apply {
            var index = hour
            if (index < 0 || hour == 24) {
                index = 0
            } else if (index > hourAdapter.getCount() - 1) {
                index = hourAdapter.getCount() - 1
            }
            hourView?.scrollToPosition(index)
        }

        private fun setMinute(minute: String?): Builder = apply {
            if (minute == null) {
                return@apply
            }
            setMinute(minute.toInt())
        }

        private fun setMinute(minute: Int): Builder = apply {
            var index = minute
            if (index < 0) {
                index = 0
            } else if (index > minuteAdapter.getCount() - 1) {
                index = minuteAdapter.getCount() - 1
            }
            minuteView?.scrollToPosition(index)
        }


        @SingleClick
        override fun onClick(view: View) {
            when (view.id) {
                R.id.tv_ui_confirm -> {
                    autoDismiss()
                    listener?.onSelected(
                        getDialog(), hourManager.getPickedPosition(),
                        minuteManager.getPickedPosition(), secondManager.getPickedPosition()
                    )
                }
                R.id.tv_ui_cancel -> {
                    autoDismiss()
                    listener?.onCancel(getDialog())
                }
            }
        }
    }

    private class PickerAdapter(context: Context) : AppAdapter<String?>(context) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder()
        }

        private inner class ViewHolder : AppViewHolder(R.layout.picker_item) {

            private val pickerView: TextView? by lazy { findViewById(R.id.tv_picker_name) }

            override fun onBindView(position: Int) {
                pickerView?.text = getItem(position)
            }
        }
    }

    interface OnListener {
        fun onSelected(dialog: BaseDialog?, hour: Int, minute: Int, second: Int)
        fun onCancel(dialog: BaseDialog?) {}
    }
}