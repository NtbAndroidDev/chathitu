package vn.hitu.ntb.chat.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.chat.databinding.ItemLoadImageBinding
import vn.hitu.ntb.chat.utils.ChatUtils
import vn.hitu.ntb.model.entity.MediaList
import vn.hitu.ntb.utils.AppUtils
import java.util.ArrayList

class LoadImageAdapter constructor(context: Context) : AppAdapter<MediaList>(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding =
            ItemLoadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemLoadImageBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val data = getItem(position)
            val lp: ViewGroup.LayoutParams = binding.imgChat.layoutParams
            if (lp is FlexboxLayoutManager.LayoutParams) {
                lp.flexGrow = 1.0f
                lp.alignSelf = AlignItems.FLEX_END
            }

            if (data.pathLocal == "") {
                ChatUtils.loadPhoto(binding.imgChat, data.original.url)
            } else
                ChatUtils.loadPhotoLocal(binding.imgChat, data.pathLocal)

            binding.imgChat.setOnClickListener {
                AppUtils.showMediaNewsFeed(getContext(), getData() as ArrayList<MediaList>, position)
            }

            binding.imgChat.setOnLongClickListener { view ->
//                hitu.vn.tms.chat.mvp.ui.adapter.LoadImageChatV2Adapter.clickMoreImage.onLongClickOneImage(
//                    message,
//                    position,
//                    binding.rltVideo
//                )
                true
            }
        }
    }
}