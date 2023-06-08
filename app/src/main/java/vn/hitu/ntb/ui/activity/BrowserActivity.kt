package vn.hitu.ntb.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import vn.hitu.ntb.R
import vn.hitu.ntb.action.StatusAction
import vn.hitu.ntb.aop.CheckNet
import vn.hitu.ntb.aop.Log
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.databinding.ActivityBrowserBinding
import vn.hitu.ntb.widget.BrowserView
import vn.hitu.ntb.widget.StatusLayout

class BrowserActivity : AppActivity(), StatusAction, OnRefreshListener {
    companion object {

        const val INTENT_KEY_IN_URL: String = "url"
        const val TITLE: String = ""

        @CheckNet
        @Log
        fun start(context: Context, url: String, title: String) {
            if (TextUtils.isEmpty(url)) {
                return
            }
            val intent = Intent(context, BrowserActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_URL, url)
            intent.putExtra(TITLE, title)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val hintLayout: StatusLayout? by lazy { findViewById(R.id.hl_browser_hint) }
    private val progressBar: ProgressBar? by lazy { findViewById(R.id.pb_browser_progress) }
    private val refreshLayout: SmartRefreshLayout? by lazy { findViewById(R.id.sl_browser_refresh) }
    private val browserView: BrowserView? by lazy { findViewById(R.id.wv_browser_view) }

    private lateinit var binding: ActivityBrowserBinding

    override fun getLayoutView(): View {
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        // Đặt Kiểm soát  WebView
        browserView?.setLifecycleOwner(this)
        // Đặt trình nghe làm mới trang web
        refreshLayout?.setOnRefreshListener(this)
    }

    override fun initData() {
        showLoading()
        browserView?.apply {
            setBrowserViewClient(AppBrowserViewClient())
            setBrowserChromeClient(AppBrowserChromeClient(this))
            loadUrl(getString(INTENT_KEY_IN_URL)!!)
        }
        binding.tvTitle.title = getString(TITLE)
    }

    override fun getStatusLayout(): StatusLayout? {
        return hintLayout
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        browserView?.apply {
            if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
                // Quay lại trang web và chặn sự kiện này
                goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }



    /**
     * tải lại trang hiện tại
     */
    @CheckNet
    private fun reload() {
        browserView?.reload()
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        reload()
    }

    private inner class AppBrowserViewClient : BrowserView.BrowserViewClient() {

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            post {
                showError(object : StatusLayout.OnRetryListener {
                    override fun onRetry(layout: StatusLayout) {
                        reload()
                    }
                })
            }
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            progressBar?.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
            progressBar?.visibility = View.GONE
            refreshLayout?.finishRefresh()
            showComplete()
        }
    }

    private inner class AppBrowserChromeClient constructor(view: BrowserView) : BrowserView.BrowserChromeClient(view) {

        /**
         * nhận được tiêu đề trang
         */
        override fun onReceivedTitle(view: WebView, title: String?) {
            if (title == null) {
                return
            }
            setTitle(title)
        }

        override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
            if (icon == null) {
                return
            }
            setRightIcon(BitmapDrawable(resources, icon))
        }

        /**
         * Nhận thay đổi tiến trình tải
         */
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progressBar?.progress = newProgress
        }
    }
}