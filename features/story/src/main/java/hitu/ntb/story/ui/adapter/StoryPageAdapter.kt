package hitu.ntb.story.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.PagerAdapter
import cn.jzvd.JZUtils
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import hitu.ntb.story.databinding.ItemImageSliderBinding
import hitu.ntb.story.databinding.ItemVideoBinding
import hitu.ntb.story.databinding.ItemViewPageBinding
import vn.hitu.ntb.model.entity.Story
import vn.hitu.ntb.utils.AppUtils

class StoryPageAdapter(
    private val context: Context, private val data: ArrayList<Story>
) : PagerAdapter() {
    private var jzvdStd = JzvdStd(context)

    @SuppressLint("CheckResult")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View
        val binding: ViewBinding
        val finalUrl = if (data[position].type == 0)
            AppUtils.getLinkPhoto(data[position].url, "images")
        else
            AppUtils.getLinkPhoto(data[position].url, "videos")





        if (data[position].type == 0) {
            binding = ItemImageSliderBinding.inflate(LayoutInflater.from(container.context), container, false)
            Glide.with(context)
                .load(finalUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.mBigImage)
        } else {
            JZUtils.clearSavedProgress(context, null)
            binding = ItemVideoBinding.inflate(LayoutInflater.from(container.context), container, false)

            jzvdStd = binding.jzVideo
            jzvdStd.setUp(finalUrl, "")
            Glide.with(context).load(finalUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(jzvdStd.posterImageView)
            jzvdStd.posterImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        container.addView(binding.root)
        return binding.root
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        container.removeView(o as View?)
    }


}