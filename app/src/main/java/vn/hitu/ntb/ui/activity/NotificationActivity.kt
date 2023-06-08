package vn.hitu.ntb.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.paginate.Paginate
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import okhttp3.Call
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.constants.NotificationConstants
import vn.hitu.ntb.databinding.ActivityNotificationBinding
import vn.hitu.ntb.http.api.NotificationApi
import vn.hitu.ntb.http.model.HttpData
import vn.hitu.ntb.interfaces.MoreListener
import vn.hitu.ntb.interfaces.NotificationsInterface
import vn.hitu.ntb.model.entity.Notification
import vn.hitu.ntb.other.CustomLoadingListItemCreator
import vn.hitu.ntb.ui.adapter.NotificationAdapter
import vn.hitu.ntb.ui.dialog.DialogMoreNotify
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: HỒ QUANG TÙNG
 * @Date: 15/10/2022
 * @Update: NGUYEN THANH BINH
 */
class NotificationActivity : AppActivity(), MoreListener, OnRefreshLoadMoreListener,
    NotificationsInterface {
    private lateinit var binding: ActivityNotificationBinding
    private var adapter: NotificationAdapter? = null
    private var notificationList: ArrayList<Notification> = ArrayList()
    private var dialogMoreNotify: DialogMoreNotify.Builder? = null
    private var position = ""
    private var limit = 20
    private var paginate: Paginate? = null
    private var isLoadMore = true
    private var loading = false
    private var isSearch = true
    override fun getLayoutView(): View {
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.headerNotification.tbHeader)
        binding.smartRefreshLayout.setOnRefreshLoadMoreListener(this)
        adapter = NotificationAdapter(this)
        adapter!!.setOnMore(this)
        adapter!!.setData(notificationList)
        AppUtils.initRecyclerViewVertical(binding.rcvNotification, adapter)

    }


    override fun initData() {
        adapter = NotificationAdapter(this)
        AppUtils.initRecyclerViewVertical(
            binding.rcvNotification, adapter
        )
        adapter!!.setOnMore(this)
        adapter!!.setOnNotifications(this)
        binding.headerNotification.btnBack.setOnClickListener {
            finish()
        }
        binding.headerNotification.tvTitle.text = getString(R.string.notification)
        /**
         * call api dan sách
         */
        callNotify("")

        /**
         *  delay phân trang
         */
            paginate()


        /**
         *  nút ẩn hiện thanh search
         */
        binding.headerNotification.btnSearch.setOnClickListener {
            if (isSearch) {
                isSearch = false
                binding.headerNotification.searchView.visibility = View.VISIBLE
                binding.headerNotification.tvTitle.visibility = View.GONE
            } else {
                isSearch = true
                binding.headerNotification.searchView.visibility = View.GONE
                binding.headerNotification.tvTitle.visibility = View.VISIBLE
            }
        }
    }

    /**
     *  phân trang danh sách notification
     */
    private fun paginate() {
        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                postDelayed({
                    if (isLoadMore && notificationList.size > 0) {
                        callNotify(notificationList[notificationList.size - 1].position!!)
                        loading = false
                    }
                }, 1000)
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                return !isLoadMore

            }

        }

        paginate = Paginate.with(binding.rcvNotification, callback)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(CustomLoadingListItemCreator())
            .build()
    }

    /**
     * call danh sách api notification
     */
    private fun callNotify(position: String) {
        EasyHttp.post(this).api(NotificationApi.params(position, limit))
            .request(object : HttpCallback<HttpData<ArrayList<Notification>?>>(this) {
                override fun onStart(call: Call?) {
                    //empty
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onSucceed(data: HttpData<ArrayList<Notification>?>) {
                    if (data.isRequestSucceed()) {

                        isLoadMore = data.getData()!!.size >= limit

                        binding.shimmerNotificationContainer.stopShimmer()
                        binding.shimmerNotificationContainer.visibility = View.GONE

                        notificationList.addAll(data.getData()!!)
                        adapter!!.setData(notificationList)
                        adapter!!.notifyDataSetChanged()

                        if (notificationList.size == 0) {
                            binding.itemEmpty.lnEmpty.visibility = View.VISIBLE
                            binding.llDataNotification.visibility = View.GONE
                        } else {
                            binding.itemEmpty.lnEmpty.visibility = View.GONE
                            binding.llDataNotification.visibility = View.VISIBLE
                        }

                    } else {
                        AppUtils.setSnackBarError(
                            binding.root, data.getMessage()
                        )
                    }
                }
            })
    }

    /**
     * sự kiện dialog xem thêm
     */
    override fun onMore(data: Notification) {
        dialogMoreNotify = DialogMoreNotify.Builder(this)
        dialogMoreNotify!!.show()
    }

    /**
     * bắt sự kiện click  chuyển tới từng loại thông báo
     */
    override fun markNotification(notifications: Notification, objectType: Int) {
        when (objectType) {
            //thông báo bạn bè , gửi lời mời ,chấp nhận lời mời...
            NotificationConstants.ALOLINE_CUSTOMER_REQUEST_FRIEND, NotificationConstants.ALOLINE_CUSTOMER_ACCEPT_REQUEST_FRIEND -> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.INFO_CUSTOMER)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    val bundle = Bundle()
                    bundle.putInt(
                        AppConstants.CUSTOMER_ID_NOTIFICATION,
                        notifications.objectId!!.toInt()
                    )
                    intent.putExtras(bundle)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }

            //thông báo booking xác nhận ,...
            NotificationConstants.ALOLINE_CUSTOMER_CONFIRM_BOOKING -> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.DETAIL_BOOKING)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    val bundle = Bundle()
                    bundle.putInt(
                        AppConstants.BOOKING_NOTIFY_TYPE_KEY,
                        AppConstants.BOOKING_NOTIFICATION
                    )
                    bundle.putInt(
                        AppConstants.BOOKING_ID_NOTIFICATION,
                        notifications.objectId!!.toInt()
                    )
                    intent.putExtras(bundle)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            //thông báo booking huỷ ,...
            NotificationConstants.ALOLINE_CUSTOMER_CANCEL_BOOKING -> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.DETAIL_BOOKING)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    val bundle = Bundle()
                    bundle.putInt(
                        AppConstants.BOOKING_NOTIFY_TYPE_KEY,
                        AppConstants.BOOKING_NOTIFICATION
                    )
                    bundle.putInt(
                        AppConstants.BOOKING_ID_NOTIFICATION,
                        notifications.objectId!!.toInt()
                    )
                    intent.putExtras(bundle)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            //Danh sách nhà hàng của tôi
            NotificationConstants.ALOLINE_MEMBERSHIP_CARD -> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.RESTAURANT_ACTIVITY)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            // Danh sách quà tặng của tôi
            NotificationConstants.ALOLINE_GIFT -> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.GIFT_ACTIVITY)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }

            NotificationConstants.ALOLINE_ATTCH_BILL_ORDER -> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.DETAIL_ORDER)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    val bundle = Bundle()
                    bundle.putInt(
                        AppConstants.ORDER_NOTIFY_TYPE_KEY,
                        AppConstants.ORDER_NOTIFICATION
                    )
                    bundle.putInt(
                        AppConstants.ORDER_ID_NOTIFICATION,
                        notifications.objectId!!.toInt()
                    )
                    intent.putExtras(bundle)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }

            NotificationConstants.ALOLINE_ACCEPT_ADVERTISING_REGISTRATION, NotificationConstants.ALOLINE_REFUSE_ADVERTISING_REGISTRATION -> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.ADVERTISEMENT_PACKAGE_ACTIVITY)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }

            NotificationConstants.ALOLINE_PAY_ACCOUNT ,NotificationConstants.ALOLINE_PLUS_POINTS-> {
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.POINT_INFORMATION)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }

            NotificationConstants.ALOLINE_NOTIFICATION_THANKS -> {
                finish()
            }

            else -> {
                //thông báo các trạng thái bai viet đăng bài,comment,reaction...
                try {
                    val intent = Intent(
                        this@NotificationActivity,
                        Class.forName(ModuleClassConstants.DETAIL_TIMELINE)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    val bundle = Bundle()
                    bundle.putString(
                        AppConstants.DETAIL_NEWS_FEED_ID_NOTIFICATION,
                        notifications.objectId
                    )
                    bundle.putInt(AppConstants.KEY_DETAIL_TIMELINE, 1)
                    intent.putExtras(bundle)
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onAction(notifications: Notification) {
        //empty
    }

    override fun onAcceptFriend(notifications: Notification) {
        //empty
    }

    override fun onRefuseFriend(notifications: Notification) {
        //empty
    }

    /**
     *  reload lại trang
     */

    override fun onRefresh(refreshLayout: RefreshLayout) {
        postDelayed({
            binding.llDataNotification.visibility = View.GONE
            binding.shimmerNotificationContainer.visibility = View.VISIBLE
            position = ""
            notificationList.clear()
            callNotify(position)
            this.binding.smartRefreshLayout.finishRefresh()
        }, 1000)

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        //empty
    }



}