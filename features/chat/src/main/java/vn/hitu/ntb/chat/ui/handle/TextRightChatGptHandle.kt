package vn.hitu.ntb.chat.ui.handle

import vn.hitu.ntb.chat.databinding.MessageTextRightBinding
import vn.hitu.ntb.chat.ui.adapter.ChatGptAdapter
import vn.hitu.ntb.model.entity.ChatGpt

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class TextRightChatGptHandle(
    private val binding: MessageTextRightBinding,
    private val data: ChatGpt,
    private val position: Int,
    private val chatHandle: ChatGptAdapter.ChatHandle,
    private var adapter: ChatGptAdapter) {

    fun setData(){
        binding.tvShowMessage.text = data.message

    }


}