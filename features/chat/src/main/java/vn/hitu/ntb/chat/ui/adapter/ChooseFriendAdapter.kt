package vn.hitu.ntb.chat.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.databinding.ItemUserBottomCreateGroupBinding
import vn.hitu.ntb.chat.interfaces.RemoveItemListener
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 23/12/2022
 */
class ChooseFriendAdapter constructor(context: Context) : AppAdapter<UserData>(context) {

    private var clickRemoveItem: RemoveItemListener? = null
    fun setClickRemove(clickRemoveItem: RemoveItemListener) {
        this.clickRemoveItem = clickRemoveItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemUserBottomCreateGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)

    }

    inner class ViewHolder(private val binding: ItemUserBottomCreateGroupBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            AppUtils.loadAvatarGroup(binding.ivAvatar, item.image)

            itemView.setOnClickListener {
                clickRemoveItem!!.removeItem(position)
            }

            binding.ibClose.setOnClickListener {
                clickRemoveItem!!.removeItem(position)
            }

        }
    }
}