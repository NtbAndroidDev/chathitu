package vn.hitu.ntb.ui.fragment

import android.view.View
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.databinding.FragmentChatBinding
import vn.hitu.ntb.ui.activity.HomeActivity

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 */
class ChatFragment : AppFragment<HomeActivity>() {

    private lateinit var binding: FragmentChatBinding

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }

    override fun getLayoutView(): View {
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

    }

    override fun initData() {
        //s
    }
}