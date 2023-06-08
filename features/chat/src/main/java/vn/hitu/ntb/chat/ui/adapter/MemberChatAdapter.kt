package vn.hitu.ntb.chat.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.databinding.MemberChatItemBinding
import vn.hitu.ntb.databinding.SearchUsersItemBinding
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 24/04/2023
 */
class MemberChatAdapter constructor(context: Context, var isSwipe : Boolean) : AppAdapter<UserData>(context) {


    private var onClick : ClickUser? = null
    fun setClickUser(clickUser : ClickUser){
        this.onClick = clickUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MemberChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }



    inner class ViewHolder(private val binding: MemberChatItemBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            binding.slView.isSwipeEnabled = isSwipe




            binding.tvSearchName.text = item.name
            AppUtils.loadAvatarGroup(binding.civSearchAvatar, item.image)



//            binding.slView.setOnClickListener {
//                onClick!!.listenerUser(item, position)
//            }
            binding.civSearchAvatar.setOnClickListener {
                onClick!!.listenerUser(item, position)
            }
            binding.tvSearchName.setOnClickListener {
                onClick!!.listenerUser(item, position)

            }

            binding.llRemove.setOnClickListener {
                onClick!!.onRemove(item, position)
            }
        }
    }


    interface ClickUser{
        fun listenerUser(item : UserData, position: Int)
        fun onRemove(item: UserData, position: Int)
    }

}