package vn.hitu.ntb.contact.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.interfaces.FriendRequestInterface
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils
import vn.techres.line.contact.databinding.ItemFriendRequestBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 04/10/2022
 */
class FriendRequestAdapter constructor(context: Context) : AppAdapter<UserData>(context) {

    private var friendRequestInterface: FriendRequestInterface? = null
    private var clickProfile: FriendRequestInterface? = null

    fun setClickFriendRequest(friendRequestInterface: FriendRequestInterface){
        this.friendRequestInterface = friendRequestInterface
        this.clickProfile = friendRequestInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemFriendRequestBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            AppUtils.loadImageUser(
                binding.imgAvatar,
                item.image
            )
            binding.txtFullName.text = item.name

            binding.btnAgree.setOnClickListener { //đồng ý kết bạn
                friendRequestInterface!!.clickAgree(position)
            }

            binding.btnDenied.setOnClickListener {
                friendRequestInterface!!.clickClose(position)
            }

            binding.imgClose.setOnClickListener {
                friendRequestInterface!!.clickClose(position)
            }

            itemView.setOnClickListener {
                clickProfile!!.clickProfile(position)
            }
        }
    }
}