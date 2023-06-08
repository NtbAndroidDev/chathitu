package vn.hitu.ntb.contact.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.ItemAllRequestFriendBinding
import vn.hitu.ntb.interfaces.FriendRequestInterface
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.TimeFormat
import vn.techres.line.contact.R

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 11/30/22
 */
class AllRequestFriendAdapter constructor(context: Context) : AppAdapter<Friend>(context) {

    private var friendRequestInterface: FriendRequestInterface? = null

    fun setClickFriendRequest(friendRequestInterface: FriendRequestInterface){
        this.friendRequestInterface = friendRequestInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAllRequestFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemAllRequestFriendBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            AppUtils.loadImageUser(
                binding.imgAvatar,
                item.avatar
            )
            binding.txtFullName.text = item.fullName
            binding.tvMutualFriend.text = String.format("%s %s %s %s", "Có", item.mutualFriend, "bạn chung •", TimeFormat.timeAgoString(getContext(), item.timestamp))


            binding.btnAgree.setOnClickListener {
                friendRequestInterface!!.clickAgree(position)
                binding.tvToast.text = getString(R.string.text_agree)
                binding.tvToast.visibility = View.VISIBLE
                binding.llButton.visibility = View.GONE
                item.contactType = 4
            }
            binding.btnDenied.setOnClickListener {
                friendRequestInterface!!.clickClose(position)
                binding.tvToast.text = getString(R.string.text_refuse)
                binding.tvToast.setTextColor(getColor(vn.hitu.ntb.R.color.black))
                binding.tvToast.visibility = View.VISIBLE
                binding.llButton.visibility = View.GONE
                item.contactType = 1


            }
            if (item.contactType == 4)
            {
                binding.tvToast.text = getString(R.string.text_agree)
                binding.tvToast.visibility = View.VISIBLE
                binding.llButton.visibility = View.GONE
            }else if(item.contactType == 1)
            {
                binding.tvToast.text = getString(R.string.text_refuse)
                binding.tvToast.setTextColor(getColor(vn.hitu.ntb.R.color.black))
                binding.tvToast.visibility = View.VISIBLE
                binding.llButton.visibility = View.GONE
            }

        }
    }
}