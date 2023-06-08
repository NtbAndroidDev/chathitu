package vn.hitu.ntb.chat.ui.handle

import android.content.Intent
import android.os.Bundle
import com.google.firebase.database.*
import vn.hitu.ntb.chat.databinding.MessageImageLeftBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.activity.ImageClickActivity
import vn.hitu.ntb.chat.ui.adapter.MessageAdapter
import vn.hitu.ntb.chat.utils.ChatUtils
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class ImageLeftHandle(
    private val binding: MessageImageLeftBinding,
    private val data: ChatMessage,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter) {
    fun setData(){
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(data.sendBy)
        binding.tvShowTimeMessage.text = AppUtils.getLastTime(data.messageTime)
        ChatUtils.resizeImage(data.message, binding.ivShowMessage)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: UserData = snapshot.getValue(UserData::class.java)!!
                AppUtils.loadImageUser(binding.civShowAvatar, user.image)
                binding.tvShowUsername.text = user.name
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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