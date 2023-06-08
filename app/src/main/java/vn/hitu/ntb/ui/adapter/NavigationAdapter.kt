package vn.hitu.ntb.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.HomeNavigationItemBinding
import vn.hitu.ntb.model.entity.Title

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 */
class NavigationAdapter constructor(context: Context) :
    AppAdapter<NavigationAdapter.MenuItem>(context),
    vn.hitu.base.BaseAdapter.OnItemClickListener {

    /** Điểm đến hiện được chọn */
    private var selectedPosition: Int = 0

    /** Thanh điều hướng nhấp vào trình nghe */
    private var listener: OnNavigationListener? = null

    init {
        setOnItemClickListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomeNavigationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun generateDefaultLayoutManager(context: Context): RecyclerView.LayoutManager {
        return GridLayoutManager(context, getCount(), RecyclerView.VERTICAL, false)
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    /**
     * Thiết lập trình nghe thanh điều hướng
     */
    fun setOnNavigationListener(listener: OnNavigationListener?) {
        this.listener = listener
    }

    /**
     * [BaseAdapter.OnItemClickListener]
     */
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        if (selectedPosition == position) {
            return
        }
        if (listener == null) {
            selectedPosition = position
            notifyDataSetChanged()
            return
        }
        if (listener!!.onNavigationItemSelected(position)) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(val binding: HomeNavigationItemBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation", "ResourceAsColor")
        override fun onBindView(position: Int) {
            getItem(position).apply {

                binding.imgIcon.setImageDrawable(getDrawable())

                if (position == 0)
                {
                    binding.tvAmount.visibility =  View.VISIBLE
                    if (getTitle()!!.amount > 99)
                        binding.tvAmount.text = String.format("%s%s", 99, getString(vn.hitu.ntb.R.string.max))
                    else
                        binding.tvAmount.text = getTitle()!!.amount.toString()
                    if (getTitle()!!.amount == 0)
                        binding.tvAmount.visibility =  View.GONE
                }
                if (getTitle()!!.title == "") {
                    binding.txtTitle.visibility = View.GONE
                    // Set lại kích cỡ cho imageView
                    binding.imgIcon.layoutParams.width = getResources().getDimensionPixelSize(R.dimen.dp_48)
                    binding.imgIcon.layoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_48)
                } else
                    binding.txtTitle.visibility = View.VISIBLE
                    binding.txtTitle.text = getTitle()!!.title

                binding.imgIcon.isSelected = (selectedPosition == position)
                binding.txtTitle.isSelected = (selectedPosition == position)

            }
        }
    }

    class MenuItem constructor(private val title: Title?, private val drawable: Drawable?) {

        fun getTitle(): Title? {
            return title
        }

        fun getDrawable(): Drawable? {
            return drawable
        }
    }



    interface OnNavigationListener {
        fun onNavigationItemSelected(position: Int): Boolean
    }
}