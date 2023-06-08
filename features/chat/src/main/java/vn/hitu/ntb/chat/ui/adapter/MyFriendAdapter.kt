package vn.hitu.ntb.chat.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.databinding.ItemUserCreateGroupChatBinding
import vn.hitu.ntb.chat.interfaces.ChooseItemListener
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 23/12/2022
 */
class MyFriendAdapter constructor(context: Context, type: Int) : AppAdapter<UserData>(context) {

    private var clickChooseItem: ChooseItemListener? = null
    private var checkType = type

    fun setClickChooseItem(clickChooseItem: ChooseItemListener) {
        this.clickChooseItem = clickChooseItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemUserCreateGroupChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)

    }

    inner class ViewHolder(private val binding: ItemUserCreateGroupChatBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            setIsRecyclable(false)
            val item = getItem(position)

            binding.tvName.text = item.name
//                binding.tvTime.text = TimeFormat.timeAgoString(getContext(), item.timestamp)
            AppUtils.loadAvatarGroup(binding.ivAvatar, item.image)






            binding.cbChooseUser.setOnCheckedChangeListener(null)

            binding.cbChooseUser.isChecked = item.checked != 0
            itemView.setOnClickListener {
                binding.cbChooseUser.isChecked = !binding.cbChooseUser.isChecked
            }

            binding.cbChooseUser.setOnCheckedChangeListener { _, b ->
                clickChooseItem!!.clickChooseItem(position, b, Friend())
            }

        }
    }
}