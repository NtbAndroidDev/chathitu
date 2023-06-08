package vn.hitu.ntb.app

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import vn.hitu.base.BaseAdapter
import vn.hitu.ntb.utils.AppUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 */
abstract class AppAdapter<T> constructor(context: Context) :
    BaseAdapter<AppAdapter<T>.AppViewHolder>(context) {

    /** Liệt kê dữ liệu */
    private var dataSet: MutableList<T> = ArrayList()

    /** Số trang của danh sách hiện tại, mặc định là trang đầu tiên, được sử dụng cho chức năng tải phân trang */
    private var pageNumber = 1

    /** Cho dù đó là trang cuối cùng, mặc định là false, được sử dụng cho chức năng tải phân trang */
    private var lastPage = false

    /** Thẻ đối tượng */
    private var tag: Any? = null

    override fun getItemCount(): Int {
        return getCount()
    }

    /**
     * Nhận tổng số dữ liệu
     */
    open fun getCount(): Int {
        return dataSet.size
    }

    /**
     * Thiết lập dữ liệu mới
     */
    @SuppressLint("NotifyDataSetChanged")
    open fun setData(data: MutableList<T>?) {
        if (data == null) {
            dataSet.clear()
        } else {
            dataSet = data
        }
        notifyDataSetChanged()
    }

    /**
     * Lấy dữ liệu hiện tại
     */
    open fun getData(): MutableList<T> {
        return dataSet
    }

    /**
     * nối một số dữ liệu
     */
    open fun addData(data: MutableList<T>?) {
        if (data == null || data.isEmpty()) {
            return
        }
        dataSet.addAll(data)
        notifyItemRangeInserted(dataSet.size - data.size, data.size)
    }

    /**
     * Xóa dữ liệu hiện tại
     */
    open fun clearData() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    /**
     * Có bao gồm dữ liệu nhập tại một vị trí hay không
     */
    open fun containsItem(@IntRange(from = 0) position: Int): Boolean {
        return containsItem(getItem(position))
    }

    /**
     * Có bao gồm dữ liệu mục hay không
     */
    open fun containsItem(item: T?): Boolean {
        return if (item == null) {
            false
        } else dataSet.contains(item)
    }

    /**
     * Nhận dữ liệu tại một vị trí
     */
    open fun getItem(@IntRange(from = 0) position: Int): T {
        return dataSet[position]
    }

    /**
     * Cập nhật dữ liệu tại một vị trí
     */
    open fun setItem(@IntRange(from = 0) position: Int, item: T) {
        dataSet[position] = item
        notifyItemChanged(position)
    }

    /**
     * Thêm một phần dữ liệu duy nhất
     */
    open fun addItem(item: T) {
        addItem(dataSet.size, item)
    }

    open fun addItem(@IntRange(from = 0) position: Int, item: T) {
        var finalPosition = position
        if (finalPosition < dataSet.size) {
            dataSet.add(finalPosition, item)
        } else {
            dataSet.add(item)
            finalPosition = dataSet.size - 1
        }
        notifyItemInserted(finalPosition)
    }

    /**
     * Xóa một phần dữ liệu
     */
    open fun removeItem(item: T) {
        val index = dataSet.indexOf(item)
        if (index != -1) {
            removeItem(index)
        }
    }

    open fun removeItem(@IntRange(from = 0) position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Lấy số trang hiện tại
     */
    open fun getPageNumber(): Int {
        return pageNumber
    }

    /**
     * Đặt số trang hiện tại
     */
    open fun setPageNumber(@IntRange(from = 0) number: Int) {
        pageNumber = number
    }

    /**
     * Hiện tại có phải là trang cuối cùng không
     */
    open fun isLastPage(): Boolean {
        return lastPage
    }

    /**
     * Đặt xem đó có phải là trang cuối cùng hay không
     */
    open fun setLastPage(last: Boolean) {
        lastPage = last
    }

    /**
     * Lấy điểm đánh dấu
     */
    open fun getTag(): Any? {
        return tag
    }

    /**
     * Đặt điểm đánh dấu
     */
    open fun setTag(tag: Any) {
        this.tag = tag
    }

    abstract inner class AppViewHolder : BaseViewHolder {

        constructor(@LayoutRes id: Int) : super(id)

        constructor(itemView: View) : super(itemView)
    }

    inner class SimpleViewHolder : AppViewHolder {

        constructor(@LayoutRes id: Int) : super(id)

        constructor(itemView: View) : super(itemView)

        override fun onBindView(position: Int) {}
    }

    /**
     * Khởi tạo instance AppUtils
     */
    fun AppViewHolder.AppUtilsInstance(): AppUtils {
        return AppUtils
    }
}