package vn.hitu.ntb.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.ItemMyFriendBinding
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/3/22
 */
class MyFriendAdapter constructor(context: Context) : AppAdapter<Friend>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val bindingSuggestFriend =
            ItemMyFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(bindingSuggestFriend)
    }

    inner class ViewHolder(private val binding: ItemMyFriendBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            AppUtils.loadImageUser(
                binding.imgAvatar, item.avatar
            )
            binding.tvFullName.text = item.fullName
        }
    }


}
