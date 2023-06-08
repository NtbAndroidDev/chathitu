package vn.hitu.ntb.chat.ui.handle

import vn.hitu.ntb.chat.databinding.ChatGptTextLeftBinding
import vn.hitu.ntb.chat.ui.adapter.ChatGptAdapter
import vn.hitu.ntb.model.entity.ChatGpt

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class TextLeftChatGptHandle(
    private val binding: ChatGptTextLeftBinding,
    private val data: ChatGpt,
    private val position: Int,
    private val chatHandle: ChatGptAdapter.ChatHandle,
    private var adapter: ChatGptAdapter) {

    fun setData(){

        binding.tvShowMessage.text = data.message
//        binding.itemView.setOnLongClickListener {
//            chatHandle.onLongClickItem(data)
//            true
//        }
    }


}