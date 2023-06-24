package vn.hitu.ntb.chat.ui.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.hitu.base.FragmentPagerAdapter
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.chat.R
import vn.hitu.ntb.chat.databinding.FragmentChatManagerBinding
import vn.hitu.ntb.chat.ui.adapter.TabChatAdapter
import vn.hitu.ntb.constants.ModuleClassConstants.Companion.CONTACT
import vn.hitu.ntb.eventbus.AmountContactEventBus
import vn.hitu.ntb.eventbus.CurrentFragmentEventBus
import vn.hitu.ntb.model.entity.Title
import vn.hitu.ntb.ui.activity.HomeActivity

/**
 * @Author: Phạm Văn Nhân
 * @Date: 03/10/2022
 * @Update: NGUYEN THANH BINH
 */
class ChatManagerFragment : AppFragment<HomeActivity>(), TabChatAdapter.OnTabListener,
ViewPager.OnPageChangeListener {

    private lateinit var binding: FragmentChatManagerBinding
    private var tabAdapter: TabChatAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null
    private var titleMessage = Title()
    private var titleContact = Title()

    override fun getLayoutView(): View {
        binding = FragmentChatManagerBinding.inflate(layoutInflater)
        return binding.root
    }

    @Subscribe(sticky = true)
    fun onMessageEvent(event: CurrentFragmentEventBus?) {
        binding.nsvPager.currentItem = 0
        tabAdapter?.setSelectedPosition(0)
    }

    override fun initView() {

        pagerAdapter = FragmentPagerAdapter(this)
        pagerAdapter!!.addFragment(GroupFragment.newInstance(), getString(R.string.chat))
        pagerAdapter!!.addFragment(Class.forName(CONTACT).newInstance() as AppFragment<*>, getString(R.string.contact))

        binding.nsvPager.adapter = pagerAdapter
        binding.nsvPager.addOnPageChangeListener(this)
        binding.nsvPager.offscreenPageLimit = 2

        tabAdapter = TabChatAdapter(getApplication()!!)
        binding.tabRecyclerView.adapter = tabAdapter

    }

    override fun initData() {
        titleMessage.title = getString(R.string.chat)
        titleContact.title = getString(R.string.contact)


        tabAdapter?.addItem(titleMessage)
        tabAdapter?.addItem(titleContact)
        tabAdapter?.setOnTabListener(this@ChatManagerFragment)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AmountContactEventBus?) {
        titleContact.amount = event!!.amount
        tabAdapter!!.notifyDataSetChanged()
    }




    override fun onDestroy() {
        super.onDestroy()
        binding.nsvPager.adapter = null
        binding.nsvPager.removeOnPageChangeListener(this)
        tabAdapter?.setOnTabListener(null)
    }

    override fun onTabSelected(recyclerView: RecyclerView?, position: Int): Boolean {
        binding.nsvPager.currentItem = position
        return true
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //
    }

    override fun onPageSelected(position: Int) {
        tabAdapter?.setSelectedPosition(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        //
    }
}