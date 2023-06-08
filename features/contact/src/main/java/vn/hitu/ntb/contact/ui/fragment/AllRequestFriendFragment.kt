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
import vn.hitu.ntb.contact.api.FriendRequestListApi
import vn.hitu.ntb.contact.ui.adapter.AllRequestFriendAdapter
import vn.hitu.ntb.eventbus.RequestEventBus
import vn.hitu.ntb.eventbus.RequestFriendEventBus
import vn.hitu.ntb.http.api.CreateConversationApi
import vn.hitu.ntb.http.model.HttpData
import vn.hitu.ntb.interfaces.FriendRequestInterface
import vn.hitu.ntb.model.entity.Conversation
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.other.CustomLoadingListItemCreator
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import vn.techres.line.contact.databinding.FragmentReceivedBinding

/**
 * @Update: NGUYEN THANH BINH
 * @Date: 01/12/2022
 */

class AllRequestFriendFragment : AppFragment<HomeActivity>(), BaseAdapter.OnItemClickListener,
    FriendRequestInterface, OnRefreshListener {

    private lateinit var binding : FragmentReceivedBinding
    private var friendRequestFromList = ArrayList<Friend>()
    private var adapterFriendRequestFrom: AllRequestFriendAdapter? = null
    private var totalRequest = 0
    private var totalPage = 0
    private var loading = false
    private var position = ""
    private var currentPage = 1
    private var limit = 20

    override fun getLayoutView(): View {
        binding = FragmentReceivedBinding.inflate(layoutInflater)
        return binding.root
    }
    companion object {
        fun newInstance(): AllRequestFriendFragment {
            return AllRequestFriendFragment()
        }
    }
    override fun initView() {

        adapterFriendRequestFrom = AllRequestFriendAdapter(getAttachActivity()!!)
        adapterFriendRequestFrom?.setOnItemClickListener(this)
        adapterFriendRequestFrom!!.setClickFriendRequest(this)
        AppUtils.initRecyclerViewVertical(binding.rcvFriendRequest, adapterFriendRequestFrom)
        binding.srlAllSuggest.setOnRefreshListener(this)
        adapterFriendRequestFrom?.setData(friendRequestFromList)

//        binding.rcvFriendRequest.adapter = adapterFriendRequestFrom


    }

    override fun initData() {
        getFriendRequest("")
        paginate()
    }

    /**
     * Gọi api lấy danh sách lời mời kết bạn
     */
    @SuppressLint("HardwareIds")
    private fun getFriendRequest(position : String) {
        if (currentPage == 1)
        {
            binding.sflAllSuggest.visibility = View.VISIBLE
            binding.rcvFriendRequest.visibility = View.GONE
            binding.lnEmpty.visibility = View.GONE
            binding.sflAllSuggest.startShimmer()
        }

        EasyHttp.post(this)
            .api(FriendRequestListApi.params(position, limit))
            .request(object : HttpCallback<HttpData<List<Friend>>>(this) {

                override fun onStart(call: Call?) {

                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onSucceed(data: HttpData<List<Friend>>) {
                    if (data.isRequestSucceed()) {
                        if (currentPage == 1){

                            binding.sflAllSuggest.visibility = View.GONE
                            binding.sflAllSuggest.stopShimmer()
                            binding.rcvFriendRequest.visibility = View.VISIBLE

                            friendRequestFromList.clear()
                            adapterFriendRequestFrom!!.notifyDataSetChanged()


                            if (data.getData()!!.isEmpty())
                            {
                                binding.rcvFriendRequest.visibility = View.GONE
                                binding.lnEmpty.visibility = View.VISIBLE
                            }else{
                                binding.rcvFriendRequest.visibility = View.VISIBLE
                                binding.lnEmpty.visibility = View.GONE
                            }


                            EventBus.getDefault().post(RequestEventBus(totalRequest))
                            totalPage = AppUtils.calculateTotalPage(totalRequest)

                            friendRequestFromList.addAll(data.getData()!!)
                            adapterFriendRequestFrom!!.notifyDataSetChanged()



                        }else{
                            friendRequestFromList.addAll(data.getData()!!)
                            adapterFriendRequestFrom?.notifyDataSetChanged()
                        }


                    }

                }
            })
    }



    private fun paginate() {
        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                postDelayed({
                    if (currentPage < totalPage) {
                        currentPage +=1
                        getFriendRequest(friendRequestFromList[friendRequestFromList.size - 1].position)
                        loading = false
                    }
                }, 2000)
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                if (totalRequest < limit) return true
                return currentPage == totalPage

            }

        }

        Paginate.with(binding.rcvFriendRequest, callback)
            .setLoadingTriggerThreshold(2)
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
                AccountConstants.ID, friendRequestFromList[position].userId
            )
            intent.putExtras(bundle)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun clickAgree(position: Int) {
        EventBus.getDefault().post(RequestFriendEventBus(position))
//        adapterFriendRequestFrom!!.removeItem(position)
    }

    override fun clickClose(position: Int) {
        EventBus.getDefault().post(RequestFriendEventBus(position))
//        adapterFriendRequestFrom!!.removeItem(position)
    }

    override fun clickProfile(position: Int) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        postDelayed({
            position = ""
            currentPage = 1
            getFriendRequest(position)
            this.binding.srlAllSuggest.finishRefresh()
        }, 1000)

    }

    /**
     * api tạo cuộc trò chuyện
     */
    private fun createGroup(id: Int) {
        EasyHttp.post(this)
            .api(CreateConversationApi.params(id))
            .request(object : HttpCallback<HttpData<Conversation>>(this) {
                override fun onSucceed(data: HttpData<Conversation>) {
                    if (!data.isRequestSucceed())
                        toast(data.getMessage())

                }
            })
    }
}