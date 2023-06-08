package vn.hitu.ntb.qr_code.ui.adapter

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
import vn.hitu.ntb.ui.adapter.TabAdapter
import vn.techres.line.qr_code.R
import vn.techres.line.qr_code.databinding.ItemTabBinding

/**
 * @Author: Nguyễn Thanh Bình
 * @Date: 6/10/2022
 */
class TabQrCodeAdapter @JvmOverloads constructor(
    context: Context,
    private val fixed: Boolean = true
): AppAdapter<String?>(context), BaseAdapter.OnItemClickListener{

    private var selectedPosition: Int = 0

    private var listener: TabAdapter.OnTabListener? = null

    init {
        setOnItemClickListener(this)
        registerAdapterDataObserver(TabAdapterDataObserver())
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val bindingDesign = ItemTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


    inner class DesignViewHolder(private val binding: ItemTabBinding) : AppViewHolder(binding.root) {

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

            if (selectedPosition == position){
                binding.txtTitle.setTextColor(getColor(vn.hitu.ntb.R.color.main_bg))

                if (position == 0){
                    binding.imgIcon.setImageResource(R.drawable.ic_scan_qr_bold)
                }else{
                    binding.imgIcon.setImageResource(R.drawable.ic_my_qr_bold)
                }
            }else{
                binding.txtTitle.setTextColor(getColor(vn.hitu.ntb.R.color.gray_main))
                binding.txtTitle.setTypeface(null, Typeface.NORMAL)
                if (position == 0){
                    binding.imgIcon.setImageResource(vn.hitu.ntb.R.drawable.ic_scan_qr)
                }else{
                    binding.imgIcon.setImageResource(R.drawable.ic_my_qr)
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
    interface OnTabListener : TabAdapter.OnTabListener {
        /**
         * Tab đã được lựa chọn
         */
        override fun onTabSelected(recyclerView: RecyclerView?, position: Int): Boolean
    }

}