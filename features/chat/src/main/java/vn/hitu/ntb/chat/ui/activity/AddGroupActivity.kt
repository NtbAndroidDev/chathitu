package vn.hitu.ntb.chat.ui.activity


import android.view.View
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.chat.databinding.ActivityAddGroupBinding

class AddGroupActivity : AppActivity() {
    private lateinit var binding: ActivityAddGroupBinding
    override fun getLayoutView(): View {
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.toolbar)

    }

    override fun initData() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

}