package hitu.ntb.story.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import hitu.ntb.story.databinding.ItemAddStoryBinding
import hitu.ntb.story.databinding.ItemImageStoryBinding
import hitu.ntb.story.databinding.ItemVideoStoryBinding
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.cache.UserDataCache
import vn.hitu.ntb.model.entity.Story
import vn.hitu.ntb.model.entity.StoryList
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show

class StoryAdapter constructor(context: Context) : AppAdapter<StoryList>(context) {


    private var listener: OnListener? = null
    private val IMAGE = 1
    private val VIDEO = 2
    fun setOnListener(onListener: OnListener) {
        this.listener = onListener
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            0
        else if (getItem(position).storyList.first().type == 0)
            IMAGE
        else
            VIDEO
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {

        return when(viewType){
            0 -> {
                val binding = ItemAddStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewCreateStoryHolder(binding)
            }

            1 -> {
                val binding = ItemImageStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewImageHolder(binding)
            }

            else ->{
                val binding = ItemVideoStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewVideoHolder(binding)
            }

        }

    }


    inner class ViewImageHolder(private val binding: ItemImageStoryBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            binding.tvName.text = item.storyList.first().name
            binding.tvQuantity.text = item.storyList.size.toString()
            if (item.storyList.size < 2)
                binding.tvQuantity.hide()
            else
                binding.tvQuantity.show()

            AppUtils.loadStory(binding.ivStory, item.storyList.first().url, "images")


            FirebaseStorage.getInstance().reference.child("images/" + item.storyList.first().avatar)
                .downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(binding.cvAvt)

                }.addOnFailureListener { }
            itemView.setOnClickListener {
                listener!!.clickItem(position)
            }



        }


    }
    inner class ViewVideoHolder(private val binding: ItemVideoStoryBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindView(position: Int) {
            val item = getItem(position)


            itemView.setOnClickListener {
                listener!!.clickItem(position)
            }
        }


    }
    inner class ViewCreateStoryHolder(private val binding: ItemAddStoryBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            AppUtils.loadStory(binding.ivStory, UserDataCache.getUser().image, "images")
            itemView.setOnClickListener {
                listener!!.clickItem(position)
            }

        }


    }

    interface OnListener {
        fun clickItem(position: Int)

    }


}