package vn.hitu.ntb.ui.adapter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.hitu.base.BaseAdapter
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.TabItemDesignBinding
import vn.hitu.ntb.databinding.TabItemSlidingBinding

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 */
class TabAdapter @JvmOverloads constructor(
    context: Context,
    /** Tab style */
    private val tabMode: Int = TAB_MODE_DESIGN,
    /** Tab Chiều rộng có cố định không */
    private val fixed: Boolean = true
) : AppAdapter<String?>(context), BaseAdapter.OnItemClickListener {

    companion object {
        const val TAB_MODE_DESIGN: Int = 1
        const val TAB_MODE_SLIDING: Int = 2
    }

    /** Vị trí mục hiện được chọn */
    private var selectedPosition: Int = 0

    /** Đối tượng trình nghe thanh điều hướng */
    private var listener: OnTabListener? = null

    init {
        setOnItemClickListener(this)
        registerAdapterDataObserver(TabAdapterDataObserver())
    }

    override fun getItemViewType(position: Int): Int {
        return tabMode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val bindingDesign = TabItemDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingSliding = TabItemSlidingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            TAB_MODE_DESIGN -> DesignViewHolder(bindingDesign)
            TAB_MODE_SLIDING -> SlidingViewHolder(bindingSliding)
            else -> throw IllegalArgumentException("are you ok?")
        }
    }

    override fun generateDefaultLayoutManager(context: Context): RecyclerView.LayoutManager {
        return if (fixed) {
            var count: Int = getCount()
            if (count < 1) {
                count = 1
            }
            GridLayoutManager(context, count, RecyclerView.VERTICAL, false)
        } else {
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Tắt RecyclerView hoạt ảnh của mục
        recyclerView.itemAnimator = null
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun setSelectedPosition(position: Int) {
        if (selectedPosition == position) {
            return
        }
        notifyItemChanged(selectedPosition)
        selectedPosition = position
        notifyItemChanged(position)
    }

    /**
     * Thiết lập trình nghe thanh điều hướng
     */
    fun setOnTabListener(listener: OnTabListener?) {
        this.listener = listener
    }

    /**
     * [BaseAdapter.OnItemClickListener]
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        if (selectedPosition == position) {
            return
        }
        if (listener == null) {
            selectedPosition = position
            notifyDataSetChanged()
            return
        }
        if (listener!!.onTabSelected(recyclerView, position)) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    inner class DesignViewHolder(val binding: TabItemDesignBinding) : AppViewHolder(binding.root) {

        init {
            if (fixed) {
                val itemView: View = getItemView()
                val layoutParams: ViewGroup.LayoutParams = itemView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams = layoutParams
            }
        }

        override fun onBindView(position: Int) {
            binding.txtTitle.text = getItem(position)
            binding.txtTitle.isSelected = (selectedPosition == position)
            binding.vLine.visibility = if (selectedPosition == position) View.VISIBLE else View.INVISIBLE
        }
    }

    inner class SlidingViewHolder(val binding: TabItemSlidingBinding) : AppViewHolder(binding.root),
        ValueAnimator.AnimatorUpdateListener {

        private val mDefaultTextSize: Int by lazy { getResources().getDimension(R.dimen.sp_14).toInt() }
        private val mSelectedTextSize: Int by lazy { getResources().getDimension(R.dimen.sp_15).toInt() }

        init {
            binding.txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDefaultTextSize.toFloat())
            if (fixed) {
                val itemView: View = getItemView()
                val layoutParams: ViewGroup.LayoutParams = itemView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams = layoutParams
            }
        }

        override fun onBindView(position: Int) {
            binding.vSliding.visibility = if (selectedPosition == position) View.VISIBLE else View.INVISIBLE
            binding.txtTitle.let {
                it.text = getItem(position)
                it.isSelected = (selectedPosition == position)
                val textSize: Int = it.textSize.toInt()
                if (selectedPosition == position) {
                    if (textSize != mSelectedTextSize) {
                        startAnimator(mDefaultTextSize, mSelectedTextSize)
                    }
                    return
                }
                if (textSize != mDefaultTextSize) {
                    startAnimator(mSelectedTextSize, mDefaultTextSize)
                }
            }
        }

        private fun startAnimator(start: Int, end: Int) {
            val valueAnimator: ValueAnimator = ValueAnimator.ofInt(start, end)
            valueAnimator.addUpdateListener(this)
            valueAnimator.duration = 100
            valueAnimator.start()
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            binding.txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (animation.animatedValue as Int).toFloat())
        }
    }

    /**
     * Trình nghe thay đổi dữ liệu
     */
    private inner class TabAdapterDataObserver : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            refreshLayoutManager()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {}

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {}

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            refreshLayoutManager()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            refreshLayoutManager()
            if (getSelectedPosition() > positionStart - itemCount) {
                setSelectedPosition(positionStart - itemCount)
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {}

        private fun refreshLayoutManager() {
            if (!fixed) {
                return
            }
            getRecyclerView()?.layoutManager = generateDefaultLayoutManager(getContext())
        }
    }

    /**
     * Tab lắng nghe
     */
    interface OnTabListener {

        /**
         * Tab đã được lựa chọn
         */
        fun onTabSelected(recyclerView: RecyclerView?, position: Int): Boolean
    }
}