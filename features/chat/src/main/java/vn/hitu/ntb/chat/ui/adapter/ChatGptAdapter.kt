package vn.hitu.ntb.chat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.databinding.ChatGptTextLeftBinding
import vn.hitu.ntb.chat.databinding.ChatGptTypingBinding
import vn.hitu.ntb.chat.databinding.MessageTextRightBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.handle.TextLeftChatGptHandle
import vn.hitu.ntb.chat.ui.handle.TextRightChatGptHandle
import vn.hitu.ntb.chat.ui.handle.TypingChatGptHandle
import vn.hitu.ntb.model.entity.ChatGpt

class ChatGptAdapter constructor(context: Context) : AppAdapter<ChatGpt>(context) {

    private var chatHandle: ChatHandle? = null


    fun setHandleMessage(context: ChatHandle){
        this.chatHandle = context
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).message.hashCode().toLong()
    }
    companion object {

        const val TEXT_LEFT = 0
        const val TEXT_RIGHT = 1
        const val TYPING = 2


    }
    override fun getItemViewType(position: Int): Int {
        val item = getData()[position]
        return if (item.type == "chatgpt")
            TEXT_LEFT
        else if (item.message == "")
            TYPING
        else
            TEXT_RIGHT
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {

        return when(viewType){
            TEXT_LEFT -> TextLeftHolder(ChatGptTextLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TEXT_RIGHT -> TextRightHolder(MessageTextRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPING -> TypingHolder(ChatGptTypingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> TextRightHolder(MessageTextRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }



    inner class TextLeftHolder(private val binding: ChatGptTextLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

            TextLeftChatGptHandle(binding, getItem(position),position,  chatHandle!!,this@ChatGptAdapter).setData()
        }
    }

    inner class TextRightHolder(private val binding: MessageTextRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            TextRightChatGptHandle(binding, getItem(position),position,  chatHandle!!,this@ChatGptAdapter).setData()

        }
    }
    inner class TypingHolder(private val binding: ChatGptTypingBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            TypingChatGptHandle(binding, getItem(position),position,  chatHandle!!,this@ChatGptAdapter).setData()

        }
    }
    interface ChatHandle{
        fun onClickItem(item : ChatMessage)
        fun  onLongClickItem(item : ChatMessage)
    }
}