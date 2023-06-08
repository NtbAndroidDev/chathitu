package vn.hitu.ntb.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.databinding.ActivityVideoPlayBinding
import vn.hitu.ntb.widget.PlayerView
import java.io.File

open class VideoPlayActivity : AppActivity(), PlayerView.OnPlayListener {
    private lateinit var binding: ActivityVideoPlayBinding

    companion object {
        const val INTENT_KEY_PARAMETERS: String = "parameters"
    }

    private var builder: Builder? = null


    override fun getLayoutView(): View {
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.pvVideoPlayView.setLifecycleOwner(this)
        binding.pvVideoPlayView.setOnPlayListener(this)
    }

    override fun initData() {
        builder = getParcelable(INTENT_KEY_PARAMETERS)
        if (builder == null) {
            throw IllegalArgumentException("are you ok?")
        }
        binding.pvVideoPlayView.setVideoTitle(builder!!.getVideoTitle())
        binding.pvVideoPlayView.setVideoSource(builder!!.getVideoSource())
        binding.pvVideoPlayView.setGestureEnabled(builder!!.isGestureEnabled())
        if (builder!!.isAutoPlay()) {
            binding.pvVideoPlayView.start()
        }
    }

    /**
     * [PlayerView.OnPlayListener]
     */
    override fun onClickBack(view: PlayerView) {
        finish()
    }

    override fun onPlayStart(view: PlayerView) {
        val progress: Int = builder?.getPlayProgress() ?: 0
        if (progress > 0) {
            binding.pvVideoPlayView.setProgress(progress)
        }
    }

    override fun onPlayProgress(view: PlayerView) {
        // 记录播放进度
        builder?.setPlayProgress(view.getProgress())
    }

    override fun onPlayEnd(view: PlayerView) {
        builder?.let {
            if (it.isLoopPlay()) {
                binding.pvVideoPlayView.setProgress(0)
                binding.pvVideoPlayView.start()
                return
            }
            if (it.isAutoOver()) {
                finish()
            }
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig() // 隐藏状态栏和导航栏
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存播放进度
        outState.putParcelable(INTENT_KEY_PARAMETERS, builder)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 读取播放进度
        builder = savedInstanceState.getParcelable(INTENT_KEY_PARAMETERS)
    }

    /** 竖屏播放 */
    class Portrait : VideoPlayActivity() {
        override fun getLayoutView(): View {
            TODO("Not yet implemented")
        }
    }

    /** 横屏播放 */
    class Landscape : VideoPlayActivity() {
        override fun getLayoutView(): View {
            TODO("Not yet implemented")
        }
    }

    /**
     * 播放参数构建
     */
    class Builder : Parcelable {

        /** 视频源 */
        private var videoSource: String? = null

        /** 视频标题 */
        private var videoTitle: String? = null

        /** 播放进度 */
        private var playProgress: Int = 0

        /** 手势开关 */
        private var gestureEnabled: Boolean = true

        /** 循环播放 */
        private var loopPlay: Boolean = false

        /** 自动播放 */
        private var autoPlay: Boolean = true

        /** 播放完关闭 */
        private var autoOver: Boolean = true

        /** 播放方向 */
        private var activityOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        constructor()

        constructor(`in`: Parcel) {
            videoSource = `in`.readString()
            videoTitle = `in`.readString()
            activityOrientation = `in`.readInt()
            playProgress = `in`.readInt()
            gestureEnabled = `in`.readByte().toInt() != 0
            loopPlay = `in`.readByte().toInt() != 0
            autoPlay = `in`.readByte().toInt() != 0
            autoOver = `in`.readByte().toInt() != 0
        }

        fun setVideoSource(file: File): Builder = apply {
            videoSource = file.path
            if (videoTitle == null) {
                videoTitle = file.name
            }
        }

        fun setVideoSource(url: String?): Builder = apply {
            videoSource = url
        }

        fun getVideoSource(): String? {
            return videoSource
        }

        fun setVideoTitle(title: String?): Builder = apply {
            videoTitle = title
        }

        fun getVideoTitle(): String? {
            return videoTitle
        }

        fun setPlayProgress(progress: Int): Builder = apply {
            playProgress = progress
        }

        fun getPlayProgress(): Int {
            return playProgress
        }

        fun setGestureEnabled(enabled: Boolean): Builder = apply {
            gestureEnabled = enabled
        }

        fun isGestureEnabled(): Boolean {
            return gestureEnabled
        }

        fun setLoopPlay(enabled: Boolean): Builder = apply {
            loopPlay = enabled
        }

        fun isLoopPlay(): Boolean {
            return loopPlay
        }

        fun setAutoPlay(enabled: Boolean): Builder = apply {
            autoPlay = enabled
        }

        fun isAutoPlay(): Boolean {
            return autoPlay
        }

        fun setAutoOver(enabled: Boolean): Builder = apply {
            autoOver = enabled
        }

        fun isAutoOver(): Boolean {
            return autoOver
        }

        fun setActivityOrientation(orientation: Int): Builder = apply {
            activityOrientation = orientation
        }

        fun start(context: Context) {
            val intent = Intent()
            when (activityOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> intent.setClass(
                    context,
                    Landscape::class.java
                )

                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> intent.setClass(
                    context,
                    Portrait::class.java
                )

                else -> intent.setClass(context, VideoPlayActivity::class.java)
            }
            intent.putExtra(INTENT_KEY_PARAMETERS, this)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(videoSource)
            dest.writeString(videoTitle)
            dest.writeInt(activityOrientation)
            dest.writeInt(playProgress)
            dest.writeByte(if (gestureEnabled) 1.toByte() else 0.toByte())
            dest.writeByte(if (loopPlay) 1.toByte() else 0.toByte())
            dest.writeByte(if (autoPlay) 1.toByte() else 0.toByte())
            dest.writeByte(if (autoOver) 1.toByte() else 0.toByte())
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<Builder> = object : Parcelable.Creator<Builder> {
                override fun createFromParcel(source: Parcel): Builder {
                    return Builder(source)
                }

                override fun newArray(size: Int): Array<Builder?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}