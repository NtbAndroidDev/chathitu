package vn.hitu.ntb.chat.ui.handle

import vn.hitu.ntb.chat.databinding.MessageNotificationBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.MessageAdapter

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 05/06/2023
 */
class NotificationHandle(
    private val binding: MessageNotificationBinding,
    private val data: ChatMessage,
    private var adapter: MessageAdapter
) {

    fun setData() {
        binding.tvMessage.text = data.message
    }


}