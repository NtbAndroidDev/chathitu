package vn.hitu.ntb.ui.activity

import android.transition.TransitionManager
import android.view.View
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.databinding.ActivitySettingNotificationBinding

/**
 * @Author: Hồ Quang Tùng
 * @Date: 30/09/2022
 */
class SettingNotificationActivity : AppActivity() {
    private lateinit var binding: ActivitySettingNotificationBinding
    override fun getLayoutView(): View {
        binding = ActivitySettingNotificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

    }

    override fun initData() {
        binding.settingNotification.lnSettingNotificationDiary.setOnClickListener {
            onClickSettingDiary()
        }
        binding.settingNotification.lnSettingNotificationChat.setOnClickListener {
            onClickSettingChat()
        }
    }

    //Thông báo từ Nhật ký
    private fun onClickSettingDiary() {
        if (binding.settingNotification.lnHideNotificationDiary.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding.settingNotification.lnHideNotificationDiary)
            binding.settingNotification.lnHideNotificationDiary.visibility = View.VISIBLE
            binding.settingNotification.imvDiary.setImageResource(R.drawable.ic_plus)

        } else {
            TransitionManager.beginDelayedTransition(binding.settingNotification.lnHideNotificationDiary)
            binding.settingNotification.lnHideNotificationDiary.visibility = View.GONE
            binding.settingNotification.imvDiary.setImageResource(R.drawable.ic_minus)
        }
    }

    //Thông báo từ Trò chuyện
    private fun onClickSettingChat() {
        if (binding.settingNotification.lnHideSettingNotificationChat.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding.settingNotification.lnHideSettingNotificationChat)
            binding.settingNotification.lnHideSettingNotificationChat.visibility = View.VISIBLE
            binding.settingNotification.imvChat.setImageResource(R.drawable.ic_plus)

        } else {
            TransitionManager.beginDelayedTransition(binding.settingNotification.lnHideSettingNotificationChat)
            binding.settingNotification.lnHideSettingNotificationChat.visibility = View.GONE
            binding.settingNotification.imvChat.setImageResource(R.drawable.ic_minus)
        }
    }

}