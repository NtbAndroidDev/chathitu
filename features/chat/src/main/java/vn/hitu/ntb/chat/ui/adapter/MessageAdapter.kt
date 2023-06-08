package vn.hitu.ntb.chat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.*
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.handle.*

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class MessageAdapter constructor(context: Context) : AppAdapter<ChatMessage>(context) {

    private var mAuth: FirebaseAuth? = null
    private var chatHandle: ChatHandle? = null
    private var onAudioChatMessage: OnAudioChatMessage? = null


    fun setHandleMessage(context: ChatHandle){
        this.chatHandle = context
    }
    fun setOnAudioChatMessage(onAudioChatMessage: OnAudioChatMessage) {
        this.onAudioChatMessage = onAudioChatMessage
    }
    companion object {

        const val TEXT_RIGHT = 1
        const val TEXT_LEFT = 2

        const val IMAGE_RIGHT = 3
        const val IMAGE_LEFT = 4

        const val VIDEO_RIGHT = 5
        const val VIDEO_LEFT = 6

        const val REPLY_RIGHT = 7
        const val REPLY_LEFT = 8

        const val STICKER_RIGHT = 9
        const val STICKER_LEFT = 10

        const val LINK_RIGHT = 11
        const val LINK_LEFT = 12

        const val FILE_RIGHT = 13
        const val FILE_LEFT = 14

        const val REVOKE_RIGHT = 15
        const val REVOKE_LEFT = 16

        const val CALL_RIGHT = 17
        const val CALL_LEFT = 18

        const val CONTACT_RIGHT = 19
        const val CONTACT_LEFT = 20

        const val AUDIO_RIGHT = 21
        const val AUDIO_LEFT = 22

        const val PINNED = 23
        const val UPDATE_GROUP = 24
        const val NOTIFICATION = 25
        const val KICK = 26
    }
    override fun getItemViewType(position: Int): Int {
        val item = getData()[position]
        mAuth = FirebaseAuth.getInstance()
        return if (item.sendBy == Auth.getAuth()) {
            when(item.typeMessage){
                MessageChatConstants.TEXT -> TEXT_RIGHT
                MessageChatConstants.IMAGE -> IMAGE_RIGHT
                MessageChatConstants.VIDEO -> VIDEO_RIGHT
                MessageChatConstants.FILE -> FILE_RIGHT
                MessageChatConstants.AUDIO -> AUDIO_RIGHT
                MessageChatConstants.NOTIFICATION -> NOTIFICATION
                else -> 0
            }
        } else {
            when(item.typeMessage){
                MessageChatConstants.TEXT -> TEXT_LEFT
                MessageChatConstants.IMAGE -> IMAGE_LEFT
                MessageChatConstants.VIDEO -> VIDEO_LEFT
                MessageChatConstants.FILE -> FILE_LEFT
                MessageChatConstants.AUDIO -> AUDIO_LEFT
                MessageChatConstants.NOTIFICATION -> NOTIFICATION

                else -> 0
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {

        return when(viewType){
            TEXT_LEFT -> TextLeftHolder(MessageTextLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TEXT_RIGHT -> TextRightHolder(MessageTextRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            IMAGE_LEFT -> ImageLeftHolder(MessageImageLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            IMAGE_RIGHT -> ImageRightHolder(MessageImageRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIDEO_LEFT -> VideoLeftHolder(MessageVideoLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIDEO_RIGHT -> VideoRightHolder(MessageVideoRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            FILE_LEFT -> FileLeftHolder(MessageFileLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            FILE_RIGHT -> FileRightHolder(MessageFileRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            AUDIO_LEFT -> AudioLeftHolder(MessageAudioLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            AUDIO_RIGHT -> AudioRightHolder(MessageAudioRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            NOTIFICATION -> NotificationHolder(MessageNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            else -> TextLeftHolder(MessageTextLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }



    inner class TextLeftHolder(private val binding: MessageTextLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

            TextLeftHandle(binding, getItem(position),position,  chatHandle!!,this@MessageAdapter).setData()
        }
    }

    inner class TextRightHolder(private val binding: MessageTextRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            TextRightHandle(binding, getItem(position),position,  chatHandle!!,this@MessageAdapter).setData()

        }
    }
    inner class ImageLeftHolder(private val binding: MessageImageLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            ImageLeftHandle(binding, getItem(position),position,  chatHandle!!,this@MessageAdapter).setData()

        }
    }
    inner class ImageRightHolder(private val binding: MessageImageRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            ImageRightHandle(binding, getItem(position),position,  chatHandle!!,this@MessageAdapter).setData()

        }
    }
    inner class VideoLeftHolder(private val binding: MessageVideoLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            VideoLeftHandle(binding, getItem(position),position, chatHandle!!,this@MessageAdapter).setData()

        }
    }
    inner class VideoRightHolder(private val binding: MessageVideoRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            VideoRightHandle(binding, getItem(position),position,  chatHandle!!,this@MessageAdapter).setData()

        }
    }

    inner class FileLeftHolder(private val binding: MessageFileLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

            FileLeftHandle(binding, getItem(position), position,  chatHandle!!,this@MessageAdapter).setData()
        }
    }
    inner class FileRightHolder(private val binding: MessageFileRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            FileRightHandle(binding, getItem(position), position,  chatHandle!!,this@MessageAdapter).setData()

        }
    }
    inner class AudioLeftHolder(private val binding: MessageAudioLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

            AudioLeftHandle(binding, getItem(position), position,  chatHandle!!,onAudioChatMessage!!,this@MessageAdapter).setData()
        }
    }
    inner class AudioRightHolder(private val binding: MessageAudioRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

            AudioRightHandle(binding, getItem(position), position,  chatHandle!!,onAudioChatMessage!!,this@MessageAdapter).setData()
        }
    }

    inner class NotificationHolder(private val binding: MessageNotificationBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

            NotificationHandle(binding, getItem(position), this@MessageAdapter).setData()
        }
    }

    interface ChatHandle{
        fun onClickItem(item : ChatMessage)
        fun  onLongClickItem(item : ChatMessage)
    }
    interface OnAudioChatMessage {
        fun onRunning(position: Int, audioFilePath: String)
    }
}