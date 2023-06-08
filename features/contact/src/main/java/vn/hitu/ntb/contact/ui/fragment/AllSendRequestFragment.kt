package vn.hitu.ntb.contact.ui.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.paginate.Paginate
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import vn.hitu.base.BaseAdapter
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.constants.AccountConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.contact.api.FriendRequestSendApi
import vn.hitu.ntb.contact.ui.adapter.AllSendRequestAdapter
import vn.hitu.ntb.eventbus.AmountSendEventBus
import vn.hitu.ntb.http.api.CountApi
import vn.hitu.ntb.http.model.HttpData
import vn.hitu.ntb.interfaces.FriendRequestInterface
import vn.hitu.ntb.model.entity.CountData
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.other.CustomLoadingListItemCreator
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import vn.techres.line.contact.databinding.FragmentSendBinding

/**
 * @Update: NGUYEN THANH BINH
 * @Date: 01/12/2022
 */

class AllSendRequestFragment : AppFragment<HomeActivity>(), BaseAdapter.OnItemClickListener,
    FriendRequestInterface, OnRefreshListener{
    private lateinit var binding : FragmentSendBinding
    private var sendRequestList = ArrayList<Friend>()
    private var adapterSendRequestFrom: AllSendRequestAdapter? = null
    private var totalSend = 0
    private var totalPage = 0
    private var loading = false
    private var position = ""
    private var currentPage = 1

    override fun getLayoutView(): View {
        binding = FragmentSendBinding.inflate(layoutInflater)
        return binding.root
    }
    companion object {
        fun newInstance(): AllSendRequestFragment {
            return AllSendRequestFragment()
        }
    }
    override fun initView() {
        adapterSendRequestFrom = AllSendRequestAdapter(getAttachActivity()!!)
        adapterSendRequestFrom?.setOnItemClickListener(this)
        adapterSendRequestFrom!!.setClickFriendRequest(this)
        AppUtils.initRecyclerViewVertical(binding.rcvSend, adapterSendRequestFrom)
        binding.srlAllSend.setOnRefreshListener(this)

    }

    override fun initData() {
        getFriendRequestSend(position)
        paginate()
    }

    /**
     * Gọi api lấy danh sách lời mời đã gửi
     */
    @SuppressLint("HardwareIds")
    private fun getFriendRequestSend(position: String) {
        if (currentPage == 1)
        {
            binding.sflAllSend.visibility = View.VISIBLE
            binding.sflAllSend.startShimmer()
            binding.rcvSend.visibility = View.GONE
            binding.lnEmpty.visibility = View.GONE

        }
        EasyHttp.post(this)
            .api(FriendRequestSendApi.params(position))
            .request(object : HttpCallback<HttpData<List<Friend>>>(this) {
                override fun onStart(call: Call?) {

                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onSucceed(data: HttpData<List<Friend>>) {
                    if (data.isRequestSucceed()) {
                        if (currentPage == 1)
                        {
                            binding.sflAllSend.visibility = View.GONE
                            binding.sflAllSend.stopShimmer()
                            binding.rcvSend.visibility = View.VISIBLE

                            sendRequestList.clear()


                            if (data.getData()!!.isEmpty())
                            {
                                binding.rcvSend.visibility = View.GONE
                                binding.lnEmpty.visibility = View.VISIBLE
                            }else{
                                binding.rcvSend.visibility = View.VISIBLE
                                binding.lnEmpty.visibility = View.GONE
                            }

                            sendRequestList.addAll(data.getData()!!)
                            adapterSendRequestFrom?.setData(sendRequestList)
                            EventBus.getDefault().post(AmountSendEventBus(totalSend))
                            totalPage = AppUtils.calculateTotalPage(totalSend)
                            adapterSendRequestFrom!!.notifyDataSetChanged()


                        }else{
                            sendRequestList.addAll(data.getData()!!)
                            adapterSendRequestFrom!!.notifyDataSetChanged()
                        }


                    }

                }
            })
    }


    /**
     * phân trang
     */
    private fun paginate() {
        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                postDelayed({
                    if (currentPage < totalPage) {
                        currentPage += 1
                        getFriendRequestSend(sendRequestList[sendRequestList.size - 1].position)
                        loading = false

                    }
                }, 2000)
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                if (totalSend < 20)
                    return true
                return currentPage == totalPage

            }

        }

        Paginate.with(binding.rcvSend, callback)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(CustomLoadingListItemCreator())
            .build()
    }

    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        try {
            val intent = Intent(
                    requireActivity(),
                    Class.forName(ModuleClassConstants.INFO_CUSTOMER)
            )
            val bundle = Bundle()
            bundle.putInt(
                    AccountConstants.ID, sendRequestList[position].userId!!
            )
            intent.putExtras(bundle)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun clickAgree(position: Int) {

    }

    override fun clickClose(position: Int) {

    }

    override fun clickProfile(position: Int) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        postDelayed({
            position = ""
            currentPage = 1
            getFriendRequestSend(position)
            this.binding.srlAllSend.finishRefresh()
        }, 1000)
    }
}