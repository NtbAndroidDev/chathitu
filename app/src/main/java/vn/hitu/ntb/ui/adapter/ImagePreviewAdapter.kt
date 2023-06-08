package vn.hitu.ntb.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.databinding.ItemImagePreviewBinding
import vn.hitu.ntb.utils.AppUtils


class ImagePreviewAdapter constructor(context: Context) : AppAdapter<String?>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val bindingImagePreview =
            ItemImagePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(bindingImagePreview)
    }


    inner class ViewHolder(private val binding: ItemImagePreviewBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            if (item != null) {
                AppUtils.loadMediaNewsFeed(binding.photoView, item)
            }
        }
    }
}