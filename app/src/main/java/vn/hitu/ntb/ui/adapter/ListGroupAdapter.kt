package vn.hitu.ntb.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.GroupItemBinding
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.utils.AppUtils

class ListGroupAdapter constructor(context: Context) : AppAdapter<GroupData>(context) {


    private var listener: OnListener? = null
    fun setOnListener(onListener: OnListener) {
        this.listener = onListener
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).gid.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {

        val binding = GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    inner class ViewHolder(private val binding: GroupItemBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            binding.tvNameGroup.text = item.name





            AppUtils.loadAvatarGroup(binding.ivAvt, item.imageId)

            itemView.setOnClickListener {
                listener!!.clickGroup(position)

            }
        }


    }

    interface OnListener {
        fun clickGroup(position: Int)

    }


}