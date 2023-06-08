package vn.hitu.ntb.ui.activity


import android.view.View


import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.databinding.ActivitySupportBinding

class SupportActivity : AppActivity() {
    private lateinit var binding: ActivitySupportBinding
    override fun getLayoutView(): View {
        binding = ActivitySupportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

    }

    override fun initData() {

    }

}