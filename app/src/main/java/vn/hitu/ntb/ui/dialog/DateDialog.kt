package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.hitu.base.BaseDialog
import vn.hitu.ntb.R.*
import vn.hitu.ntb.aop.SingleClick
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.DateDialogBinding
import vn.hitu.ntb.manager.PickerLayoutManager
import vn.hitu.ntb.manager.PickerLayoutManager.OnPickerListener
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: Phạm Văn Nhân
 * @Date: 27/09/2022
 */
class DateDialog { class Builder @JvmOverloads constructor(
        context: Context, private val
        startYear: Int = Calendar.getInstance(Locale.getDefault())[Calendar.YEAR] - 100,
        endYear: Int = Calendar.getInstance(Locale.getDefault())[Calendar.YEAR]) : CommonDialog.Builder<Builder>(context), Runnable, OnPickerListener {
        private var binding: DateDialogBinding = DateDialogBinding.inflate(LayoutInflater.from(context))

        private val yearManager: PickerLayoutManager
        private val monthManager: PickerLayoutManager
        private val dayManager: PickerLayoutManager
        private val yearAdapter: PickerAdapter
        private val monthAdapter: PickerAdapter
        private val dayAdapter: PickerAdapter
        private var listener: OnListener? = null

        init {

            setCustomView(binding.root)
            setTitle(string.time_title)
            yearAdapter = PickerAdapter(context)
            monthAdapter = PickerAdapter(context)
            dayAdapter = PickerAdapter(context)

            // Năm
            val yearData = ArrayList<String>(10)
            for (i in startYear..endYear) {
                yearData.add(i.toString())
            }
            // Tháng
            val monthData = ArrayList<String>(12)
            for (i in 1..12) {
                monthData.add(i.toString())
            }
            val calendar = Calendar.getInstance(Locale.CHINA)
            val day = calendar.getActualMaximum(Calendar.DATE)
            // Ngày
            val dayData = ArrayList<String>(day)
            for (i in 1..day) {
                dayData.add(i.toString())
            }
            yearAdapter.setData(yearData)
            monthAdapter.setData(monthData)
            dayAdapter.setData(dayData)
            yearManager = PickerLayoutManager.Builder(context)
                .build()
            monthManager = PickerLayoutManager.Builder(context)
                .build()
            dayManager = PickerLayoutManager.Builder(context)
                .build()
            binding.yearView.layoutManager = yearManager
            binding.monthView.layoutManager = monthManager
            binding.dayView.layoutManager = dayManager
            binding.yearView.adapter = yearAdapter
            binding.monthView.adapter = monthAdapter
            binding.dayView.adapter = dayAdapter
            setDay(calendar[Calendar.DAY_OF_MONTH])
            setMonth(calendar[Calendar.MONTH] + 1)
            setYear(calendar[Calendar.YEAR])
            yearManager.setOnPickerListener(this)
            monthManager.setOnPickerListener(this)
        }

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        /**
         * Không chọn ngày
         */
        fun setIgnoreDay(): Builder = apply {
            binding.dayView.visibility = View.GONE
        }

        fun setDate(date: Long): Builder = apply {
            if (date > 0) {
                setDate(SimpleDateFormat("ddMMyy", Locale.getDefault()).format(Date(date)))
            }
        }

        private fun setDate(date: String): Builder = apply {
            if (date.matches(Regex("\\d{8}"))) {
                // 20190519
                setDay(date.substring(6, 8))
                setMonth(date.substring(4, 6))
                setYear(date.substring(0, 4))
            } else if (date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                // 2019-05-19
                setYear(date.substring(0, 4))
                setMonth(date.substring(5, 7))
                setDay(date.substring(8, 10))
            }
        }

        fun setYear(year: String): Builder = apply {
            return setYear(year.toInt())
        }

        fun setYear(year: Int): Builder = apply {
            var index = year - startYear
            if (index < 0) {
                index = 0
            } else if (index > yearAdapter.getCount() - 1) {
                index = yearAdapter.getCount() - 1
            }
            binding.yearView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        private fun setMonth(month: String): Builder = apply {
            setMonth(month.toInt())
        }

        private fun setMonth(month: Int): Builder = apply {
            var index = month - 1
            if (index < 0) {
                index = 0
            } else if (index > monthAdapter.getCount() - 1) {
                index = monthAdapter.getCount() - 1
            }
            binding.monthView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        fun setDay(day: String): Builder = apply {
            setDay(day.toInt())
        }

        fun setDay(day: Int): Builder = apply {
            var index = day - 1
            if (index < 0) {
                index = 0
            } else if (index > dayAdapter.getCount() - 1) {
                index = dayAdapter.getCount() - 1
            }
            binding.dayView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        @SingleClick
        override fun onClick(view: View) {
            when (view.id) {
                id.tv_ui_confirm -> {
                    autoDismiss()
                    listener!!.onSelected(getDialog()!!, startYear + yearManager.getPickedPosition(),
                        monthManager.getPickedPosition() + 1, dayManager.getPickedPosition() + 1)
                }
                id.tv_ui_cancel -> {
                    autoDismiss()
                    listener!!.onCancel(getDialog()!!)
                }
            }
        }

        /**
         * [PickerLayoutManager.OnPickerListener]
         *
         * @param recyclerView              RecyclerView
         */
        override fun onPicked(recyclerView: RecyclerView, position: Int) {
            refreshMonthMaximumDay()
        }

        override fun run() {
            // Nhận số ngày tối đa trong tháng này
            val calendar = Calendar.getInstance(Locale.CHINA)
            calendar[startYear + yearManager.getPickedPosition(), monthManager.getPickedPosition()] =
                1
            val day = calendar.getActualMaximum(Calendar.DATE)
            if (dayAdapter.getCount() != day) {
                val dayData = ArrayList<String>(day)
                for (i in 1..day) {
                    dayData.add(i.toString())
                }
                dayAdapter.setData(dayData)
            }
        }

        /**
         * Làm mới số ngày tối đa mỗi tháng
         */
        private fun refreshMonthMaximumDay() {
            binding.yearView.removeCallbacks(this)
            binding.yearView.post(this)
        }

        class PickerAdapter constructor(context: Context) : AppAdapter<String>(context) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder()
            }

            inner class ViewHolder : AppViewHolder(layout.picker_item) {

                private val pickerView: TextView? by lazy { findViewById(id.tv_picker_name) }

                override fun onBindView(position: Int) {
                    pickerView!!.text = getItem(position)
                }
            }
        }
    }

    interface OnListener {

        /**
         * Gọi lại sau khi chọn ngày
         *
         * @param day
         * @param month
         * @param year
         */
        fun onSelected(dialog: BaseDialog, year: Int, month: Int, day: Int)

        /**
         * Gọi lại khi nhấp vào hủy
         */
        fun onCancel(dialog: BaseDialog) {}
    }
}