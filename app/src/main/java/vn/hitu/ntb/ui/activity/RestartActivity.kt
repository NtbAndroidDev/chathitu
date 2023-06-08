package vn.hitu.ntb.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppActivity

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class RestartActivity : AppActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RestartActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        fun restart(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }


    override fun getLayoutView(): View {
        TODO("Not yet implemented")
    }


    override fun initView() {}

    override fun initData() {
        restart(this)
        finish()
        toast(R.string.common_crash_hint)
    }
}