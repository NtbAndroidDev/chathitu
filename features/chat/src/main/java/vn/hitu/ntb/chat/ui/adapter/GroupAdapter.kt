package vn.hitu.ntb.chat.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.ItemGroupBinding
import vn.hitu.ntb.chat.interfaces.GroupChatListener
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 03/10/2022
 */
class GroupAdapter constructor(context: Context) : AppAdapter<GroupData>(context) {

    private var onClickGroupChat : GroupChatListener? = null



    fun setGroupChatListener(listener: GroupChatListener)
    {
        this.onClickGroupChat = listener
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).gid.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    inner class ViewHolder(private val binding: ItemGroupBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindView(position: Int) {

            setIsRecyclable(false)

            val item = getItem(position)

            binding.tvName.text = item.name




            AppUtils.loadAvatarGroup(binding.civImage, item.imageId)
            binding.tvName.text = item.name
            binding.tvMessage.text = item.lastMessage


            //sau này bỏ cái if
            binding.tvTime.text = AppUtils.getLastTime(item.lastTime)
            if (item.isOnline) {
                binding.civOnlineCircle.setImageResource(vn.hitu.ntb.R.color.green_circle)
            } else {
                binding.civOnlineCircle.setImageResource(vn.hitu.ntb.R.color.yellow_circle)
            }

            when(item.lastMessage){
                MessageChatConstants.TEXT ->{
                    binding.tvMessage.text = item.lastMessage
                    binding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(vn.hitu.ntb.R.drawable.ic_text, 0, 0, 0)
                    binding.tvMessage.compoundDrawablePadding = 5

                }
                MessageChatConstants.IMAGE ->{
                    binding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(vn.hitu.ntb.R.drawable.ic_image_fill, 0, 0, 0)
                    binding.tvMessage.compoundDrawablePadding = 5
                    binding.tvMessage.text = getString(vn.hitu.ntb.chat.R.string.type_image)

                }
                MessageChatConstants.VIDEO -> {
                    binding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(vn.hitu.ntb.R.drawable.ic_image_fill, 0, 0, 0)
                    binding.tvMessage.compoundDrawablePadding = 5
                    binding.tvMessage.text = getString(vn.hitu.ntb.chat.R.string.type_video)
                }
                MessageChatConstants.FILE -> {
                    binding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(vn.hitu.ntb.R.drawable.ic_attachment_line_on, 0, 0, 0)
                    binding.tvMessage.compoundDrawablePadding = 5
                    binding.tvMessage.text = getString(vn.hitu.ntb.chat.R.string.type_file)
                }
                MessageChatConstants.AUDIO -> {
                    binding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(vn.hitu.ntb.R.drawable.ic_attachment_line_on, 0, 0, 0)
                    binding.tvMessage.compoundDrawablePadding = 5
                    binding.tvMessage.text = getString(vn.hitu.ntb.chat.R.string.type_audio)
                }
                else ->{
                    binding.tvMessage.text = item.lastMessage
                    binding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(vn.hitu.ntb.R.drawable.ic_text, 0, 0, 0)
                    binding.tvMessage.compoundDrawablePadding = 5

                }
            }


            itemView.setOnClickListener {
                onClickGroupChat!!.clickGroup(position)
            }

        }

    }


}