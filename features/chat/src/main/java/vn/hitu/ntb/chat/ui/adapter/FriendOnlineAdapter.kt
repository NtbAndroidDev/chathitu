package vn.hitu.ntb.chat.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.R
import vn.hitu.ntb.chat.databinding.ItemCreateChatGroupBinding
import vn.hitu.ntb.chat.databinding.ItemFriendOnlineChatBinding
import vn.hitu.ntb.chat.interfaces.ManagerMemberListener
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Update: NGUYEN THANH BINH
 * @Date: 03/10/2022
 */
class FriendOnlineAdapter constructor(context: Context) : AppAdapter<GroupData>(context) {
    private var userListener: ManagerMemberListener? = null
    fun setUserListener(userListener: ManagerMemberListener) {
        this.userListener = userListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        if (viewType == 0) {
            val binding = ItemCreateChatGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return CreateViewHolder(binding)
        }
        val binding =
            ItemFriendOnlineChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return 0
        return 1
    }

    inner class FriendViewHolder(private val binding: ItemFriendOnlineChatBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            binding.tvOnlineUser.text = item.name
            AppUtils.loadAvatarGroup(binding.civImage, item.imageId)

            if (!item.isOnline) {
                binding.civOnlineCircle.setImageResource(vn.hitu.ntb.R.color.yellow_circle)
            } else {
                binding.civOnlineCircle.setImageResource(vn.hitu.ntb.R.color.green_circle)
            }
            itemView.setOnClickListener {
                userListener!!.clickMember(position)
            }
        }
    }


    inner class CreateViewHolder(private val binding: ItemCreateChatGroupBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            binding.lnCreate.setOnClickListener {
                try {
                    val intent = Intent(
                        getContext(), Class.forName(ModuleClassConstants.CREATE_GROUP_CHAT)
                    )
                    getContext().startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}