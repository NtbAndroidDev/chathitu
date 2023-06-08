package vn.hitu.ntb.ui.activity

import android.content.*
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.hitu.base.FragmentPagerAdapter
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.databinding.HomeActivityBinding
import vn.hitu.ntb.eventbus.*
import vn.hitu.ntb.interfaces.PassAddress
import vn.hitu.ntb.manager.ActivityManager
import vn.hitu.ntb.model.entity.*
import vn.hitu.ntb.other.DoubleClickHelper
import vn.hitu.ntb.ui.adapter.NavigationAdapter
import vn.hitu.ntb.ui.fragment.AccountFragment
import vn.hitu.ntb.ui.fragment.FoodFragment
import vn.hitu.ntb.utils.AppUtils
import java.util.*


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/09/2022
 */
class HomeActivity : AppActivity(), NavigationAdapter.OnNavigationListener {
    lateinit var binding: HomeActivityBinding
    var lat = ""
    private lateinit var mPassAddress: PassAddress
    private var titleMessage = Title()
    private var titleFood = Title()
    private var titleAccount = Title()
    private var titleHome = Title()
    private var objectType = ""
    private var objectId = ""
    private var uuid = ""
    var isCheck = true
    override fun getLayoutView(): View {
        binding = HomeActivityBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        setIntentCallBackNotification(extras)
    }


    companion object {
        private const val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
        private const val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"
    }

    private var navigationAdapter: NavigationAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null


    @Subscribe(sticky = true)
    fun onMessageEvent(event: CurrentFragmentEventBus?) {
        binding.nsvPager.currentItem = event!!.currentFragment
        navigationAdapter?.setSelectedPosition(event.currentFragment)
    }


    @Subscribe(sticky = true)
    fun onSettingGroupChat(event: EventbusSettingGroup) {
        binding.nsvPager.currentItem = event.key
        navigationAdapter?.setSelectedPosition(event.key)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTheme(event: ThemeEventBus) {

        recreate()
        binding.nsvPager.currentItem = 3


    }

    override fun initView() {


        titleHome.title = getString(R.string.home_nav_message)
        titleFood.title = getString(R.string.group)
        titleMessage.title = getString(R.string.chat_gpt)
        titleAccount.title = getString(R.string.home_nav_account)
        ImmersionBar.setTitleBar(this, binding.header.tbHeader)
        initOnClickListener()
        navigationAdapter = NavigationAdapter(this).apply {
            addItem(
                NavigationAdapter.MenuItem(
                    titleHome,
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_message_selector)
                )
            )
            addItem(
                NavigationAdapter.MenuItem(
                    titleFood,
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_group_selector)
                )
            )
//            addItem(
//                NavigationAdapter.MenuItem(
//                    titleNewsFeed,
//                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_news_feed_selector)
//                )
//
//            )
            addItem(
                NavigationAdapter.MenuItem(
                    titleMessage,
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_chat_gpt_selector)
                )
            )
            addItem(
                NavigationAdapter.MenuItem(
                    titleAccount,
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.home_user_selector)
                )
            )
            setOnNavigationListener(this@HomeActivity)
            binding.recyclerViewNavigation.adapter = this
        }

    }


    override fun initData() {


        pagerAdapter = FragmentPagerAdapter<AppFragment<*>>(this).apply {
            addFragment(Class.forName(ModuleClassConstants.CHAT)
                .newInstance() as AppFragment<*>)
            addFragment(FoodFragment.newInstance())
//            addFragment(
//                Class.forName(ModuleClassConstants.TIME_LINE_FRAGMENT)
//                    .newInstance() as AppFragment<*>
//            )
            addFragment(
                Class.forName(ModuleClassConstants.CALL_HISTORY)
                    .newInstance() as AppFragment<*>
            )
            addFragment(AccountFragment.newInstance())
            binding.nsvPager.adapter = this

            binding.nsvPager.currentItem = 0
            binding.nsvPager.offscreenPageLimit = 1
        }

        onNewIntent(intent)




    }

    private fun setIntentCallBackNotification(extras: Bundle?) {
        if (extras != null) {
            objectType = extras.getString(AppConstants.OBJECT_TYPE).toString()
            objectId = extras.getString(AppConstants.OBJECT_ID).toString()
            uuid = extras.getString(AppConstants.UUID).toString()
        }
    }



    private fun initOnClickListener() {
        binding.header.btnScan.setOnClickListener {
            try {
                val intent = Intent(
                    this,
                    Class.forName(ModuleClassConstants.SCAN_QR_CODE_ACTIVITY)
                )
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }


        binding.header.btnScan.setOnClickListener {
            try {
                val intent = Intent(
                    this,
                    Class.forName(ModuleClassConstants.SCAN_QR_CODE_ACTIVITY)
                )
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        //Đến màn hình thông tin điểm
        binding.header.btnPoint.setOnClickListener {
            try {
                val intent = Intent(
                    this,
                    Class.forName(ModuleClassConstants.POINT_INFORMATION)

                )
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        pagerAdapter?.let {
            switchFragment(it.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.nsvPager.let {
            // Lưu vị trí chỉ mục Fragment hiện tại
            outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, it.currentItem)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Khôi phục vị trí chỉ mục Fragment hiện tại
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX))
    }

    private fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }
        if (fragmentIndex == 2) {
            EventBus.getDefault().post(EventBusNewFeedClickTab())
        }
        when (fragmentIndex) {

            0, 1, 2, 3, 4 -> {
                binding.nsvPager.currentItem = fragmentIndex
                navigationAdapter?.setSelectedPosition(fragmentIndex)
            }
        }
    }







    /**
     * [NavigationAdapter.OnNavigationListener]
     */
    override fun onNavigationItemSelected(position: Int): Boolean {

        EventBus.getDefault().post(EventBusNewFeedClickTab())
        return when (position) {
            0, 1, 2, 3, 4 -> {
                binding.nsvPager.currentItem = position
                true
            }

            else -> false
        }
    }






    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            EventBus.getDefault().post(EventbusScrollNewFeed(1))
            toast(R.string.home_exit_hint)
            return
        }
        // Di chuyển đến ngăn xếp nhiệm vụ trước đó để tránh các phản ứng bất lợi do trượt cạnh gây ra
        moveTaskToBack(false)
        postDelayed({
            // Thực hiện tối ưu hóa bộ nhớ và phá hủy tất cả các giao diện
            ActivityManager.getInstance().finishAllActivities()
        }, 300)
    }

    override fun onDestroy() {
        binding.nsvPager.adapter = null
        binding.recyclerViewNavigation.adapter = null
        navigationAdapter?.setOnNavigationListener(null)
//        val uid = FirebaseAuth.getInstance().currentUser!!.uid
//        DbReference.writeIsOnlineUserAndGroup(uid, false)
        super.onDestroy()


    }


    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val status = sharedPreferences.getBoolean("status", true)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        if (status) {
            DbReference.writeIsOnlineUserAndGroup(uid, true)
        }
    }



    override fun onPause() {
//        val uid = FirebaseAuth.getInstance().currentUser!!.uid
//        DbReference.writeIsOnlineUserAndGroup(uid, false)
        super.onPause()
    }
}