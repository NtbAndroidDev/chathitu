package hitu.ntb.story.ui.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.jzvd.JZUtils
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import hitu.ntb.story.databinding.ActivityStoryDetailBinding
import hitu.ntb.story.databinding.ItemCountDownBinding
import hitu.ntb.story.databinding.ItemViewPageBinding
import hitu.ntb.story.ui.adapter.CountdownAdapter
import hitu.ntb.story.ui.adapter.StoryAdapter
import hitu.ntb.story.ui.adapter.StoryPageAdapter
import hitu.ntb.story.ui.handle.PageHandle
import kotlinx.coroutines.NonDisposableHandle.parent
import org.jzvd.jzvideo.JZVideoA
import timber.log.Timber
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.Story
import vn.hitu.ntb.model.entity.StoryList
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.widget.ZoomOutPageTransformer
import java.io.File

class StoryDetailActivity : AppActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    private var data: ArrayList<StoryList> = ArrayList()
    private var isMediaCountVisible: Boolean = false
    private var startPosition: Int = 0
    private var pagerAdapter: ScreenSlidePagerAdapter? = null
    private var isScrollViewPager = false
    private lateinit var video: JzvdStd
    private var position = 0
    override fun getLayoutView(): View {
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.STORY_LIST)) {
                val gson = Gson()
                val listType = object : TypeToken<ArrayList<StoryList>>() {}.type
                val arrayList: ArrayList<StoryList> =
                    gson.fromJson(bundleIntent.getString(AppConstants.STORY_LIST), listType)
                data = arrayList
                data.removeAt(0)
                position = bundleIntent.getInt(AppConstants.POSITION_MEDIA)
            }
        }

    }

    override fun initData() {
        initViewsAndSetAdapter()

    }

    private fun setStartPosition() {
        if (startPosition >= 0) {
            if (startPosition > data.size) {
                binding.mPager.currentItem = data.size - 1
                return
            }
            binding.mPager.currentItem = startPosition
        } else {
            binding.mPager.currentItem = 0
        }
        binding.mPager.offscreenPageLimit = 0
    }

    @SuppressLint("SetTextI18n")
    private fun initViewsAndSetAdapter() {
        pagerAdapter = ScreenSlidePagerAdapter(this, data, isScrollViewPager)
        binding.mPager.adapter = pagerAdapter
//        setStartPosition()
        binding.mPager.pageMargin = 10
        binding.mPager.setPageTransformer(true, ZoomOutPageTransformer())
            binding.mPager.currentItem = position




        binding.mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {
//                binding.tvNumber.text = binding.mPager.currentItem + 1).toString() + "/" + data.size
//                if (data[i].type == 1) {
//                    JZUtils.clearSavedProgress(this@StoryDetailActivity, null)
//                    Jzvd.releaseAllVideos()
//                    if (!isScrollViewPager) {
//                        autoPlayVideo()
//                    }
//                }
                Timber.d("PAGE_CHA: ${i}")


            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(i: Int) {
                //Empty
            }

            override fun onPageScrollStateChanged(i: Int) {
//                isScrollViewPager = i != ViewPager.SCROLL_STATE_IDLE
//                if (!isScrollViewPager) {
//                    autoPlayVideo()
//                }
            }
        })
    }

    class ScreenSlidePagerAdapter(
        private val context: Context,
        private val data: ArrayList<StoryList>,
        private var isScrollViewPager: Boolean
    ) : PagerAdapter() {
        private var jzvdStd = JzvdStd(context)
        private var mPager = ViewPager(context)
        private lateinit var video: JzvdStd

        @SuppressLint("CheckResult")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val binding = ItemViewPageBinding.inflate(LayoutInflater.from(container.context), container, false)

            PageHandle(context,this@ScreenSlidePagerAdapter, binding, data, position, container).setData()

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



    }
}