package vn.hitu.ntb.chat.ui.handle

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import vn.hitu.ntb.chat.databinding.MessageTextRightBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.MessageAdapter
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class TextRightHandle(
    private val binding: MessageTextRightBinding,
    private val data: ChatMessage,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter) {

    fun setData(){
        binding.tvShowMessage.text = data.message
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(data.sendBy)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: UserData = snapshot.getValue(UserData::class.java)!!
                FirebaseStorage.getInstance().reference.child("images/" + user.image)
                    .downloadUrl.addOnSuccessListener { uri ->
                        Picasso.get().load(uri).into(binding.civShowAvatar)
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                binding.tvShowUsername.text = user.name
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.tvShowTimeMessage.text = AppUtils.getLastTime(data.messageTime)
        binding.itemView.setOnLongClickListener {
            chatHandle.onLongClickItem(data)
            true
        }
    }


}