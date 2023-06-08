package vn.hitu.ntb.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.databinding.ActivityProfileBinding
import vn.hitu.ntb.http.api.MyFriendApi
import vn.hitu.ntb.http.model.HttpData
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.model.entity.FriendData
import vn.hitu.ntb.ui.adapter.MyFriendAdapter
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: Hồ Quang Tùng
 * @Date: 21/10/2022
 */
class ProfileActivity : AppActivity(), vn.hitu.base.BaseAdapter.OnItemClickListener {
    private lateinit var binding: ActivityProfileBinding
    private var friendDataList = ArrayList<Friend>()
    private var adapterMyFriend: MyFriendAdapter? = null
    private var keySearch = ""
    override fun getLayoutView(): View {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        adapterMyFriend = MyFriendAdapter(this)
        adapterMyFriend?.setOnItemClickListener(this)
        ImmersionBar.setTitleBar(this, binding.header.tbHeader)
        binding.profileUser.imgBgProfile.visibility = View.VISIBLE
        binding.profileFriend.tvMyFriendUser.text =
            String.format("%s của %s", getString(R.string.tvMyFriend), UserCache.getUser().name)
        AppUtils.initRecyclerViewVertical(
            binding.profileFriend.recyclerViewFriendRequest,
            adapterMyFriend
        )
        val lln = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.profileFriend.recyclerViewFriendRequest.layoutManager = lln




    }

    override fun initData() {
        binding.header.btnBack.setOnClickListener {
            finish()
        }
        getFriend(UserCache.getUser().id)
    }

    /**
     * Gọi api lấy danh sách bạn bè
     */
    @SuppressLint("HardwareIds")
    private fun getFriend(idUser: Int) {
        EasyHttp.post(this)
            .api(MyFriendApi.params(idUser, "", keySearch))
            .request(object : HttpCallback<HttpData<FriendData>>(this) {
                override fun onSucceed(data: HttpData<FriendData>) {
                    if (data.isRequestSucceed()) {
                        friendDataList.addAll(data.getData()!!.list)
                        friendDataList.sortWith { o1, o2 ->
                            o1.fullName!!.trim().compareTo(o2.fullName!!.trim())
                        }
                        adapterMyFriend!!.setData(friendDataList)
                    }

                }
            })
    }

    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
    }

}