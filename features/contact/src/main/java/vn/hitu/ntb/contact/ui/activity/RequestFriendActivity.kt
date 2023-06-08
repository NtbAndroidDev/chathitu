package vn.hitu.ntb.contact.ui.activity


import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.hitu.base.FragmentPagerAdapter
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.contact.ui.adapter.TabRequestFriendAdapter
import vn.hitu.ntb.contact.ui.fragment.AllRequestFriendFragment
import vn.hitu.ntb.contact.ui.fragment.AllSendRequestFragment
import vn.hitu.ntb.eventbus.AmountSendEventBus
import vn.hitu.ntb.eventbus.RequestEventBus
import vn.hitu.ntb.http.api.CountApi
import vn.hitu.ntb.http.model.HttpData
import vn.hitu.ntb.model.entity.CountData
import vn.hitu.ntb.model.entity.Title
import vn.techres.line.contact.R
import vn.techres.line.contact.databinding.ActivityRequestFriendBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/2/22
 */

class RequestFriendActivity : AppActivity(), TabRequestFriendAdapter.OnTabListener,
    ViewPager.OnPageChangeListener {
    private lateinit var binding : ActivityRequestFriendBinding
    private var tabAdapter: TabRequestFriendAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null
    private var titleReceived = Title()
    private var titleSend= Title()
    override fun getLayoutView(): View {
        binding = ActivityRequestFriendBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        pagerAdapter = FragmentPagerAdapter(this)

        pagerAdapter!!.addFragment(AllRequestFriendFragment.newInstance(), getString(R.string.received))
        pagerAdapter!!.addFragment(AllSendRequestFragment.newInstance(), getString(R.string.send))


        binding.nsvPager.adapter = pagerAdapter
        binding.nsvPager.addOnPageChangeListener(this)
        binding.nsvPager.offscreenPageLimit = 2

        tabAdapter = TabRequestFriendAdapter(this)
        binding.tabRecyclerView.adapter = tabAdapter

        ImmersionBar.setTitleBar(this, binding.headerTitle)


    }

    override fun initData() {
        getCount()

        titleSend.title = getString(R.string.send)
        titleReceived.title = getString(R.string.received)

        tabAdapter?.addItem(titleReceived)
        tabAdapter?.addItem(titleSend)
        tabAdapter?.setOnTabListener(this)

    }
    /**
     * Gọi api lấy số lượng
     */
    @SuppressLint("HardwareIds")
    private fun getCount() {
        EasyHttp.post(this)
            .api(CountApi.params())
            .request(object : HttpCallback<HttpData<CountData>>(this) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onSucceed(data: HttpData<CountData>) {
                    if (data.isRequestSucceed()) {
                        titleSend.amount = data.getData()!!.noOfRequest
                        binding.nsvPager.offscreenPageLimit = 2
                        tabAdapter!!.notifyDataSetChanged()

                    }

                }
            })
    }


    /**
     * hứng dữ liệu event bus từ màn hình fragment truyền về
     */
    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AmountSendEventBus?) {
        titleSend.amount = event!!.amount
        tabAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageRequestEvent(event: RequestEventBus?) {
        titleReceived.amount = event!!.amount
        tabAdapter!!.notifyDataSetChanged()
    }

    /**
     *
     */



    override fun onTabSelected(recyclerView: RecyclerView?, position: Int): Boolean {
        binding.nsvPager.currentItem = position
        return true
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        tabAdapter?.setSelectedPosition(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

}