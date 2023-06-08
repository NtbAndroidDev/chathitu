package vn.hitu.ntb.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.SearchUsersItemBinding
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 24/04/2023
 */
class SearchUserAdapter constructor(context: Context) : AppAdapter<UserData>(context) {


    private var onClick : ClickUser? = null
    fun setClickUser(clickUser : ClickUser){
        this.onClick = clickUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchUsersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: SearchUsersItemBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            binding.tvSearchName.text = item.name
            AppUtils.loadAvatarGroup(binding.civSearchAvatar, item.image)


            itemView.setOnClickListener {
                onClick!!.listenerUser(item, position)
            }
        }
    }


    interface ClickUser{
        fun listenerUser(item : UserData, position: Int)
    }

}