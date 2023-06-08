package vn.hitu.ntb.contact.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.hitu.base.BaseAdapter
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.model.entity.Title
import vn.hitu.ntb.ui.adapter.TabAdapter
import vn.techres.line.contact.R
import vn.techres.line.contact.databinding.TabItemBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 11/30/22
 */
class TabRequestFriendAdapter @JvmOverloads constructor(
    context: Context,
    private val fixed: Boolean = true
) : AppAdapter<Title?>(context), BaseAdapter.OnItemClickListener {

    private var selectedPosition: Int = 0

    private var listener: TabAdapter.OnTabListener? = null

    init {
        setOnItemClickListener(this)
        registerAdapterDataObserver(TabAdapterDataObserver())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val bindingDesign =
            TabItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DesignViewHolder(bindingDesign)
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


    inner class DesignViewHolder(private val binding: TabItemBinding) :
        AppViewHolder(binding.root) {

        init {
            if (fixed) {
                val itemView: View = getItemView()
                val layoutParams: ViewGroup.LayoutParams = itemView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams = layoutParams
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            binding.txtTitle.text = getItem(position)!!.title
            if (getItem(position)!!.amount > 99)
                binding.tvAmount.text = getItem(position)!!.amount.toString()
            else
                binding.tvAmount.text = getItem(position)!!.amount.toString()
            binding.txtTitle.isSelected = (selectedPosition == position)

            if (selectedPosition == position) {
                binding.txtTitle.setTextColor(getColor(vn.hitu.ntb.R.color.white))
                binding.txtTitle.setTypeface(null, Typeface.BOLD)
                binding.tvAmount.setTextColor(getColor(vn.hitu.ntb.R.color.main_bg))
                binding.tvAmount.setBackgroundResource(R.drawable.ic_bg_amount_on)

            } else {
                binding.txtTitle.setTextColor(getColor(vn.hitu.ntb.R.color.orange_chat_view_page))
                binding.txtTitle.setTypeface(null, Typeface.NORMAL)
                binding.tvAmount.setTextColor(getColor(vn.hitu.ntb.R.color.main_bg))
                binding.tvAmount.setBackgroundResource(R.drawable.ic_bg_amount_off)

            }


        }
    }

    /**
     * Trình nghe thay đổi dữ liệu
     */
    private inner class TabAdapterDataObserver : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            refreshLayoutManager()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            refreshLayoutManager()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            refreshLayoutManager()
            if (getSelectedPosition() > positionStart - itemCount) {
                setSelectedPosition(positionStart - itemCount)
            }
        }

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
    interface OnTabListener : TabAdapter.OnTabListener {
        /**
         * Tab đã được lựa chọn
         */
        override fun onTabSelected(recyclerView: RecyclerView?, position: Int): Boolean
    }
}
