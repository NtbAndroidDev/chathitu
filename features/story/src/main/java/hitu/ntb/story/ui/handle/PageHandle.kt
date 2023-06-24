package hitu.ntb.story.ui.handle

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import cn.jzvd.JZUtils
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import hitu.ntb.story.databinding.ItemViewPageBinding
import hitu.ntb.story.ui.activity.StoryDetailActivity
import hitu.ntb.story.ui.adapter.CountdownAdapter
import hitu.ntb.story.ui.adapter.StoryPageAdapter
import timber.log.Timber
import vn.hitu.ntb.model.entity.StoryList
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.widget.ZoomOutPageTransformer

class PageHandle(
    val context: Context,
    val adapter : StoryDetailActivity.ScreenSlidePagerAdapter,
    var binding: ItemViewPageBinding,
    val data: ArrayList<StoryList>,
    val position: Int,
    val container: ViewGroup
): CountdownAdapter.OnListener {
    private var mPager = ViewPager(context)
    private lateinit var video: JzvdStd
    var countdownAdapter : CountdownAdapter? = null

    fun setData(){

        binding.tvNamePage.text = data[position].storyList[0].name
        FirebaseStorage.getInstance().reference.child("images/" + data[position].storyList[0].avatar)
            .downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(binding.cvAvtPage)

            }.addOnFailureListener { }

//        data[position].storyList[0].currentItem = 1
        countdownAdapter = CountdownAdapter(context)
        countdownAdapter!!.setData(data[position].storyList)
        countdownAdapter!!.setOnListener(this)
        AppUtils.initRecyclerViewHorizontal(binding.rcvCountDown, countdownAdapter)

        val pagerAdapter = StoryPageAdapter(context, data[position].storyList)
        binding.viewPage.adapter = pagerAdapter
        binding.viewPage.pageMargin = 10
        binding.viewPage.setPageTransformer(true, ZoomOutPageTransformer())
        binding.viewPage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {
//                if (data[position].storyList[i].type == 1) {
//                    JZUtils.clearSavedProgress(context, null)
//                    Jzvd.releaseAllVideos()
//                    autoPlayVideo()
//                }
//                Log.d("page_story", i.toString())
                Timber.d("page_story: ${i}")
//                data[position].storyList[i].currentItem = 1
//                countdownAdapter!!.notifyDataSetChanged()
//
                binding.tvTimePage.text = AppUtils.getLastTime(data[position].storyList[i].time)
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(i: Int) {
                //Empty
                Timber.d("page_story: ${i}")
            }

            override fun onPageScrollStateChanged(i: Int) {
                autoPlayVideo()


            }
        })


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun startProgress(position: Int) {
//        data[this.position].storyList[position].currentItem = 0
//        binding.viewPage.currentItem = position + 1
    }
    @SuppressLint("NewApi")
    private fun autoPlayVideo() {
        if (mPager.getChildAt(0) != null) {
            try {
                if (data.size == 1) {
                    video = mPager.getChildAt(0).findViewById(hitu.ntb.story.R.id.jzVideo)
//                    video.seekToInAdvance = data.first().currentSeek
//                    if (data.first().isPlay) {
//                    }
                    video.startVideoAfterPreloading()

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setEndProgress(position: Int){
        for (i in data[this.position].storyList.indices){
            if (i != position){
                data[this.position].storyList[i].currentItem = 0
                countdownAdapter!!.notifyDataSetChanged()
            }
        }
    }

}