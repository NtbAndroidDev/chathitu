package vn.hitu.ntb.chat.ui.handle

import android.content.Intent
import android.os.Bundle
import vn.hitu.ntb.chat.databinding.MessageImageRightBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.activity.ImageClickActivity
import vn.hitu.ntb.chat.ui.adapter.MessageAdapter
import vn.hitu.ntb.chat.utils.ChatUtils
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class ImageRightHandle(
    private val binding: MessageImageRightBinding,
    private val data: ChatMessage,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter) {
    fun setData(){

        binding.tvShowTimeMessage.text = AppUtils.getLastTime(data.messageTime)

        ChatUtils.resizeImage(data.message, binding.ivShowMessage)
        binding.ivShowMessage.setOnClickListener{
            val intent: Intent = Intent(adapter.getContext(), ImageClickActivity::class.java)
            val bundleSent = Bundle()
            bundleSent.putString("imgSrc", data.message)
            intent.putExtras(bundleSent)
            adapter.getContext().startActivity(intent)
        }
        binding.itemView.setOnLongClickListener {
            chatHandle.onLongClickItem(data)
            true
        }
    }

}