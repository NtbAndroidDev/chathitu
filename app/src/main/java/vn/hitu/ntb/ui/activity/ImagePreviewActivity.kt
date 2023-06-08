package vn.hitu.ntb.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.base.BaseAdapter
import vn.hitu.ntb.aop.Log
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.databinding.ActivityImagePreviewBinding
import vn.hitu.ntb.ui.adapter.ImagePreviewAdapter
import java.util.*

class ImagePreviewActivity : AppActivity(), BaseAdapter.OnItemClickListener {
    lateinit var binding: ActivityImagePreviewBinding
    private var adapter: ImagePreviewAdapter? = null
    override fun getLayoutView(): View {
        binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {

        private const val INTENT_KEY_IN_IMAGE_LIST = "imageList"
        private const val INTENT_KEY_IN_IMAGE_INDEX = "imageIndex"

        fun start(context: Context, url: String) {
            val images = ArrayList<String?>(1)
            images.add(url)
            start(context, images)
        }

        fun start(context: Context, urls: MutableList<String?>) {
            start(context, urls, 0)
        }

        @Log
        fun start(context: Context, urls: MutableList<String?>, index: Int) {
            var finalUrls: List<String?> = urls
            if (finalUrls.isEmpty()) {
                return
            }
            val intent = Intent(context, ImagePreviewActivity::class.java)
            if (finalUrls.size > 2000) {
                // 请注意：如果传输的数据量过大，会抛出此异常，并且这种异常是不能被捕获的
                // 所以当图片数量过多的时候，我们应当只显示一张，这种一般是手机图片过多导致的
                // 经过测试，传入 3121 张图片集合的时候会抛出此异常，所以保险值应当是 2000
                // android.os.TransactionTooLargeException: data parcel size 521984 bytes
                finalUrls = listOf(finalUrls[index])
            }
            if (finalUrls is ArrayList<*>) {
                intent.putExtra(INTENT_KEY_IN_IMAGE_LIST, finalUrls)
            } else {
                intent.putExtra(INTENT_KEY_IN_IMAGE_LIST, ArrayList(finalUrls))
            }
            intent.putExtra(INTENT_KEY_IN_IMAGE_INDEX, index)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }


    override fun initView() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(
            this,
            android.R.color.black
        )
        ImmersionBar.setTitleBar(this, binding.rlHeader)
        binding.vpImagePreviewPager.offscreenPageLimit = 3
    }

    override fun initData() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(
            this,
            android.R.color.black
        )
        adapter = ImagePreviewAdapter(this)
        val images = getStringArrayList(INTENT_KEY_IN_IMAGE_LIST)
        if (images.isNullOrEmpty()) {
            finish()
            return
        }
        adapter!!.setData(images)
        adapter!!.setOnItemClickListener(this)
        binding.vpImagePreviewPager.adapter = adapter

        binding.ivClose.setOnClickListener {
            finish()
        }
        if (images.size != 1) {
            if (images.size < 10) {
                // 如果是 10 张以内的图片，那么就显示圆圈指示器
                binding.ciImagePreviewIndicator.visibility = View.VISIBLE
                binding.ciImagePreviewIndicator.setViewPager(binding.vpImagePreviewPager)
            } else {
                // 如果超过 10 张图片，那么就显示文字指示器
                binding.tvImagePreviewIndicator.visibility = View.VISIBLE
                binding.vpImagePreviewPager.registerOnPageChangeCallback(mPageChangeCallback)
            }
            val index = getInt(INTENT_KEY_IN_IMAGE_INDEX)
            if (index < images.size) {
                binding.vpImagePreviewPager.setCurrentItem(index, false)
            }
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig() // 隐藏状态栏和导航栏
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }

    override fun isStatusBarDarkFont(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.vpImagePreviewPager.unregisterOnPageChangeCallback(mPageChangeCallback)
    }


    /**
     * [BaseAdapter.OnItemClickListener]
     * @param recyclerView      RecyclerView 对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        if (isFinishing || isDestroyed) {
            return
        }
        // 单击图片退出当前的 Activity
        finish()
    }

    /**
     * ViewPager2 页面改变监听器
     */
    private val mPageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {

        @Suppress("SetTextI18n")
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.tvImagePreviewIndicator.text =
                (position + 1).toString() + "/" + adapter!!.getCount()
        }
    }
}