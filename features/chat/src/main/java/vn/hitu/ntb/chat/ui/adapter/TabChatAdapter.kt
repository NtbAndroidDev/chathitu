package vn.hitu.ntb.chat.ui.adapter

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
import vn.hitu.ntb.chat.databinding.TabItemChatBinding
import vn.hitu.ntb.model.entity.Title

/**
 * @Author: Phạm Văn Nhân
 * @Date: 03/10/2022
 */
class TabChatAdapter @JvmOverloads constructor(
    context: Context,
    private val fixed: Boolean = true
) : AppAdapter<Title?>(context), BaseAdapter.OnItemClickListener {

    private var selectedPosition: Int = 0

    private var listener: OnTabListener? = null

    init {
        setOnItemClickListener(this)
        registerAdapterDataObserver(TabAdapterDataObserver())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val bindingDesign = TabItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class DesignViewHolder(private val binding: TabItemChatBinding) : AppViewHolder(binding.root) {

        init {
            if (fixed) {
                val itemView: View = getItemView()
                val layoutParams: ViewGroup.LayoutParams = itemView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams = layoutParams
            }
        }

        override fun onBindView(position: Int) {
            val item = getItem(position)
            if (position == 0 || item!!.amount == 0)
            {
                binding.tvAmount.visibility = View.GONE
            }

            if (item!!.amount > 99)
                binding.tvAmount.text = String.format("%s%s", 99, getString(vn.hitu.ntb.R.string.max))
            else
                binding.tvAmount.text = item.amount.toString()



            binding.tvTitle.text = getItem(position)!!.title

            if (selectedPosition == position){
                binding.tvTitle.setTextColor(getColor(vn.hitu.ntb.R.color.white))
                binding.tvTitle.setTypeface(null, Typeface.BOLD)
                if (position == 0){
                    binding.ivIcon.setImageResource(vn.hitu.ntb.chat.R.drawable.ic_chat_off)
                    binding.ivActive.setImageResource(vn.hitu.ntb.chat.R.drawable.ic_chat_active)
                    binding.ivActive.visibility = View.GONE
                    item.haveMessage = 0

                }else{
                    binding.ivIcon.setImageResource(vn.hitu.ntb.chat.R.drawable.ic_contact_on)

                }
            }else{

                if (getItem(position)!!.haveMessage == 1)
                {
                    binding.ivActive.visibility = View.VISIBLE
                }


                binding.tvTitle.setTextColor(getColor(vn.hitu.ntb.R.color.gray_400))
                binding.tvTitle.setTypeface(null, Typeface.NORMAL)
                if (position == 0){
                    binding.ivIcon.setImageResource(vn.hitu.ntb.chat.R.drawable.ic_chat_on)
                }else{
                    binding.ivIcon.setImageResource(vn.hitu.ntb.chat.R.drawable.ic_contact_off)
                }
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
    interface OnTabListener {
        /**
         * Tab đã được lựa chọn
         */
        fun onTabSelected(recyclerView: RecyclerView?, position: Int): Boolean
    }
}