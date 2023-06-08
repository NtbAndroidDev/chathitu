package vn.hitu.ntb.qr_code.ui.activity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.base.FragmentPagerAdapter
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.qr_code.ui.adapter.TabQrCodeAdapter
import vn.hitu.ntb.qr_code.ui.fragment.MyQRFragment
import vn.hitu.ntb.qr_code.ui.fragment.ScanQRFragment
import vn.techres.line.qr_code.R
import vn.techres.line.qr_code.databinding.ActivityQrManagerBinding

/**
 * @Author: Nguyễn Thanh Bình
 * @Date: 06/10/2022
 */
class QrManagerActivity : AppActivity(), TabQrCodeAdapter.OnTabListener,
    ViewPager.OnPageChangeListener {

    private lateinit var binding: ActivityQrManagerBinding
    private var tabAdapter: TabQrCodeAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null

    override fun getLayoutView(): View {
        binding = ActivityQrManagerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        pagerAdapter = FragmentPagerAdapter(this)
        pagerAdapter!!.addFragment(ScanQRFragment.newInstance(), getString(R.string.scanQR))
        pagerAdapter!!.addFragment(MyQRFragment.newInstance(), getString(R.string.myQR))
        binding.nsvPager.adapter = pagerAdapter
        binding.nsvPager.addOnPageChangeListener(this)
        tabAdapter = TabQrCodeAdapter(this)
        binding.tabRecyclerView.adapter = tabAdapter
        binding.nsvPager.isNestedScrollingEnabled = false
    }

    //đổi màu trong suốt thanh điều hướng :3333
    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            .statusBarColor(vn.hitu.ntb.R.color.transparent)

    }

    override fun initData() {
        tabAdapter?.addItem(getString(R.string.scanQR))
        tabAdapter?.addItem(getString(R.string.myQR))
        tabAdapter?.setOnTabListener(this)
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