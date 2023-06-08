package vn.hitu.ntb.chat.ui.handle

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import vn.hitu.ntb.chat.databinding.MessageFileLeftBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.MessageAdapter
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.FileTypeUtils

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class FileLeftHandle(
    private val binding: MessageFileLeftBinding,
    private val data: ChatMessage,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter) {
    fun setData(){
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(data.sendBy)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: UserData = snapshot.getValue(UserData::class.java)!!
                AppUtils.loadImageUser(binding.civShowAvatar, user.image)
                binding.tvShowUsername.text = user.name
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.tvShowTimeMessage.text = AppUtils.getLastTime(data.messageTime)
        binding.tvShowMessage.text = getFileName(data.message)
        binding.tvShowMessage.setTypeface(null, Typeface.ITALIC)
        binding.tvShowMessage.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvShowMessage.setOnClickListener{
            downloadFile(data.message)
        }
        FileTypeUtils.setLogoImageToFile(
            adapter.getContext(),
            binding.ivTypeFile,
            FileTypeUtils.getMimeType(data.message)
        )
        binding.itemView.setOnLongClickListener {
            chatHandle.onLongClickItem(data)
            true
        }
    }

    private fun downloadFile(path: String) {
        val mStorage = FirebaseStorage.getInstance().reference
        val fileRef: StorageReference = mStorage.child("files/$path")
        fileRef.downloadUrl.addOnCompleteListener { task ->
            val intent = Intent(Intent.ACTION_VIEW, task.result)
            adapter.getContext().startActivity(intent)

        }
    }
    private fun getFileName(path: String): String? {
        var filename: String? = ""
        val cut = path.lastIndexOf('|')
        if (cut != -1) {
            filename = path.substring(cut + 1)
        }
        return filename
    }
}