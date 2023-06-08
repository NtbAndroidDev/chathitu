package vn.hitu.ntb.chat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.databinding.ItemChangeImageBinding
import vn.hitu.ntb.chat.databinding.ItemListImageBackgroundBinding
import vn.hitu.ntb.utils.AppUtils


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/03/2023
 */
class ChangeBackgroundAdapter constructor(context: Context) :
    AppAdapter<String>(context) {

    private var onClickChange: ChangeBackgroundListener? = null
    fun setChangeBackground(onClickChange: ChangeBackgroundListener) {
        this.onClickChange = onClickChange
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 1
            else -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return if (viewType == 2) {
            val binding = ItemListImageBackgroundBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ViewImageHolder(binding)
        } else {
            val binding = ItemChangeImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ViewHolder(binding)
        }
    }


    inner class ViewHolder(private val binding: ItemChangeImageBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

            val item = getItem(position)
            itemView.setOnClickListener {
                onClickChange!!.clickChangeImageHandle(position, item, item)

            }
            binding.ibCamera.setOnClickListener {


                onClickChange!!.clickChangeImageHandle(position, item, item)

            }

        }
    }

    inner class ViewImageHolder(private val binding: ItemListImageBackgroundBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            val item = getItem(position)

            FirebaseStorage.getInstance().reference.child("images/$item")
                .downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(binding.ivBackground)

                }.addOnFailureListener { }
            itemView.setOnClickListener {
                onClickChange!!.clickChangeImageHandle(position, item, item)
            }
        }

    }

    interface ChangeBackgroundListener {
        fun clickChangeImageHandle(position: Int, image: String, background: String)
    }

}


