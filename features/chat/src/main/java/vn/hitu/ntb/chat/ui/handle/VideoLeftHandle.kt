package vn.hitu.ntb.chat.ui.handle

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import cn.jzvd.JZUtils
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.database.*
import timber.log.Timber
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.chat.databinding.MessageVideoLeftBinding
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
class VideoLeftHandle(
    private val binding: MessageVideoLeftBinding,
    private val data: ChatMessage,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter) {


    private var jzvdStd = JzvdStd(adapter.getContext())

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

        binding.ivShowMessage.setOnClickListener{
            val intent = Intent(adapter.getContext(), ImageClickActivity::class.java)
            val bundleSent = Bundle()
            bundleSent.putString("imgSrc", data.message)
            intent.putExtras(bundleSent)
            adapter.getContext().startActivity(intent)
        }
        binding.itemView.setOnLongClickListener {
            chatHandle.onLongClickItem(data)
            true
        }
        getSizeBitmap(data.message, binding.ivShowMessage)
    }
    private fun initVideo(){
        JZUtils.clearSavedProgress(adapter.getContext(), null)
        jzvdStd = binding.ivShowMessage
        jzvdStd.setUp(AppUtils.getLinkPhoto(data.message, "videos"), "")
        Glide.with(adapter.getContext()).load(AppUtils.getLinkPhoto(data.message, "videos"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(jzvdStd.posterImageView)
        jzvdStd.posterImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    private fun getSizeBitmap(url: String, imageView: JzvdStd) {
        try {
            //Link live
            if (!url.contains("/")) {
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(AppUtils.getLinkPhoto(url, "videos"))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = ChatUtils.resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap : %s", resource.width)
                            Timber.d("height Bitmap : %s", resource.height)
                            imageView.layoutParams.height = bitmap.height
                            imageView.layoutParams.width = bitmap.width
                            val vto = imageView.viewTreeObserver
                            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // Làm gì đó với chiều cao
                                    val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
                                    layoutParams.height = bitmap.height
                                    layoutParams.width = bitmap.width
                                    imageView.layoutParams = layoutParams


                                    imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    initVideo()
                                }
                            })
                        }
                    })
            } else {
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = ChatUtils.resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap local: %s", resource.width)
                            Timber.d("height Bitmap local: %s", resource.height)
                            imageView.layoutParams.height = bitmap.height
                            imageView.layoutParams.width = bitmap.width
                        }
                    })
            }
        } catch (ignored: Exception) {
        }
    }


}