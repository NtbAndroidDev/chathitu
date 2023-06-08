package vn.hitu.ntb.ui.activity


import android.view.View
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.databinding.ActivitySettingAccountBinding


/**
 * @Update: NGUYEN THANH BINH
 * @Date: 18/10/2022
 */
open class SettingAccountActivity : AppActivity() {
    private lateinit var binding: ActivitySettingAccountBinding

    override fun getLayoutView(): View {
        binding = ActivitySettingAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
    }

    override fun initData() {


    }


}