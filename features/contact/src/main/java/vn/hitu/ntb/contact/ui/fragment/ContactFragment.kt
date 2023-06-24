package vn.hitu.ntb.contact.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.paginate.Paginate
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.hitu.base.BaseDialog
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AccountConstants
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.contact.api.*
import vn.hitu.ntb.contact.ui.adapter.FriendRequestAdapter
import vn.hitu.ntb.contact.ui.adapter.TypeFriendAdapter
import vn.hitu.ntb.eventbus.AmountContactEventBus
import vn.hitu.ntb.eventbus.RequestFriendEventBus
import vn.hitu.ntb.http.api.*
import vn.hitu.ntb.http.model.HttpData
import vn.hitu.ntb.interfaces.AddFriendInterface
import vn.hitu.ntb.interfaces.FriendRequestInterface
import vn.hitu.ntb.interfaces.MyFriendInterface
import vn.hitu.ntb.model.entity.*
import vn.hitu.ntb.other.CustomLoadingListItemCreator
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.ui.dialog.DialogFeedBack
import vn.hitu.ntb.ui.dialog.MoreDialog
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show
import vn.techres.line.contact.databinding.FragmentContactBinding
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 04/10/2022
 * @Update: 30/11/2002
 */
class ContactFragment : AppFragment<HomeActivity>(), OnRefreshLoadMoreListener,
    FriendRequestInterface, MyFriendInterface, AddFriendInterface {
    private lateinit var binding: FragmentContactBinding
    private var friendDataList = ArrayList<UserData>()
    private var friendRequestFromList = ArrayList<UserData>()
    private var adapterFriendRequestFrom: FriendRequestAdapter? = null
    private var adapterTypeFriend: TypeFriendAdapter? = null
    private var dialogMore: MoreDialog.Builder? = null
    private var totalRequestFriend = 0
    private var totalFriend = 0
    private var position = ""
    private var currentPage = 1
    private val mAuth = FirebaseAuth.getInstance()



    override fun getLayoutView(): View {
        binding = FragmentContactBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RequestFriendEventBus?) {
        adapterTypeFriend!!.setNumberRequestFriend(--totalRequestFriend)
        adapterTypeFriend!!.notifyDataSetChanged()
        friendRequestFromList.removeAt(event!!.position)

    }

    override fun initView() {
        binding.smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    override fun initData() {
        adapterFriendRequestFrom = FriendRequestAdapter(getAttachActivity()!!)
        adapterFriendRequestFrom!!.setClickFriendRequest(this)
        adapterFriendRequestFrom?.setData(friendRequestFromList)

        adapterTypeFriend = TypeFriendAdapter(getApplication()!!, adapterFriendRequestFrom!!)
        adapterTypeFriend!!.setMyFriendInterFace(this)
        adapterTypeFriend?.setOnClickAddFriend(this)
        adapterTypeFriend?.setOnClickMore(this)
        adapterTypeFriend?.setData(friendDataList)

        AppUtils.initRecyclerViewVertical(binding.rcvMyFriend, adapterTypeFriend)

//        friendDataList.add(Friend())

        getFriendRequest()
        getFriend()


    }

    /**
     * Gọi api lấy danh sách lời mời kết bạn
     */
    @SuppressLint("HardwareIds")
    private fun getFriendRequest() {
        val mDatabase = FirebaseDatabase.getInstance()
        mDatabase.getReference("Requests")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()){
//                        Log.i("user_online", Gson().toJson(dataSnapshot.value))

                        friendRequestFromList.clear()
                        for (dataSnapshot in dataSnapshot.children){
                            val user: RequestFriend = dataSnapshot.getValue(RequestFriend::class.java)!!
//                            Log.i("user_online", Gson().toJson(user))
                            if (user.id == mAuth.currentUser!!.uid && user.contactType == 3){
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(dataSnapshot.key!!)
                                    .addValueEventListener(object : ValueEventListener {
                                        @SuppressLint("NotifyDataSetChanged")
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val user: UserData? = snapshot.getValue(UserData::class.java)
                                            Log.i("user_online", Gson().toJson(user))
                                            Log.i("key_friend", dataSnapshot.key!!)
                                            friendRequestFromList.add(user!!)
                                            adapterFriendRequestFrom?.notifyDataSetChanged()
                                            adapterTypeFriend!!.setNumberRequestFriend(friendRequestFromList.size)
                                            adapterTypeFriend!!.notifyDataSetChanged()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                            }
                            adapterFriendRequestFrom?.notifyDataSetChanged()
                            adapterTypeFriend!!.notifyDataSetChanged()
                        }


                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


    }


    /**
     * Gọi api lấy danh sách bạn bè
     */
    @SuppressLint("HardwareIds", "NotifyDataSetChanged")
    private fun getFriend() {
        val mDatabase = FirebaseDatabase.getInstance()
        mDatabase.getReference("Friends").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendDataList.clear()
                friendDataList.add(UserData())
                if (dataSnapshot.exists()){
                    for (data in dataSnapshot.children){
                        val item = data.getValue(IsFriend::class.java)
                        if (item!!.mId == Auth.getAuth()){
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(item.yId)
                                .addValueEventListener(object : ValueEventListener {
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val user: UserData? = snapshot.getValue(UserData::class.java)
                                        Log.i("friend", Gson().toJson(user))
                                        Log.i("key_friend", dataSnapshot.key!!)
                                        friendDataList.add(user!!)
                                        adapterTypeFriend!!.setNumberMyFriend(friendDataList.size)
                                        adapterTypeFriend!!.notifyDataSetChanged()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })

                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        adapterTypeFriend!!.setNumberMyFriend(friendDataList.size)
        adapterTypeFriend!!.notifyDataSetChanged()
    }











    override fun onRefresh(refreshLayout: RefreshLayout) {
        postDelayed({
            position = ""
            getFriendRequest()
            getFriend()
            this.binding.smartRefreshLayout.finishRefresh()
        }, 1000)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        //
    }


    /**
     * onclick đồng ý
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun clickAgree(position: Int) { //đồng ý kết bạn
        DbReference.acceptFriend(friendRequestFromList[position], getAttachActivity()!!)
        adapterFriendRequestFrom!!.removeItem(position)
        adapterFriendRequestFrom!!.notifyDataSetChanged()



    }

    /**
     * onclick từ chối
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun clickClose(position: Int) {
//        notAccept(adapterFriendRequestFrom!!.getItem(position).userId)
        adapterFriendRequestFrom!!.removeItem(position)
        adapterFriendRequestFrom!!.notifyDataSetChanged()
    }

    override fun clickProfile(position: Int) {
        val intent = Intent(getAttachActivity()!!, Class.forName(ModuleClassConstants.INFO_CUSTOMER))
        val bundle = Bundle()
        bundle.putString(AppConstants.ID_USER, friendRequestFromList[position].uid)
        intent.putExtras(bundle)
        startActivity(intent)
    }


    override fun clickMore(item: Friend, position: Int) {
        dialogMore = MoreDialog.Builder(getAttachActivity()!!, item, 1)
            .setListener(object : MoreDialog.OnListener {
                override fun onBlockUser(dialog: BaseDialog, idUser: Int) {
//                    blockUser(idUser)
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onUnFriendUser(dialog: BaseDialog, idUser: Int) {
//                    friendDataList[position].contactType = AppConstants.NOT_FRIEND
                    adapterTypeFriend!!.notifyDataSetChanged()

                }

                override fun onContentReport(dialog: BaseDialog, type: Int, contentReport: String) {

                }

            })
        dialogMore!!.show()
    }

    override fun clickWall(position: Int) {
        if (position != 0) {
            val intent = Intent(getAttachActivity()!!, Class.forName(ModuleClassConstants.INFO_CUSTOMER))
            val bundle = Bundle()
            bundle.putString(AppConstants.ID_USER, friendDataList[position].uid)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun clickMessage(position: Int) {
        goToChat(friendDataList[position].uid, friendDataList[position].name, friendDataList[position].image)
    }

    override fun clickAddFriend(id: String) { //gửi lời mời kết bạn
        DbReference.requestFriend(id, getAttachActivity()!!)
    }

    override fun clickRecall(id: String) { //thu hồi lời mời

        DbReference.cancelRequest(id, getAttachActivity()!!)

    }

    override fun clickFeedBack(id: String, position: Int) { //phản hồi
        DialogFeedBack.Builder(getAttachActivity()!!).setListener(object : DialogFeedBack.ClickAddFriend {
            override fun onClickAddFriend(type: Int) {
                if (type == 1) {
                    DbReference.acceptFriend(friendDataList[position],getAttachActivity()!!)
                } else {
                    DbReference.refuseRequest(id, getAttachActivity()!!)
                }

            }
        }).show()

    }
    private fun goToChat(uid : String, name : String, avt : String){
        val listUidMember = ArrayList<String>()
        listUidMember.add(mAuth.currentUser!!.uid)
        listUidMember.add(uid)
        val databaseReference = FirebaseDatabase.getInstance().getReference("Groups")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var check = false
                var idGroup = ""
                for (dataSnapshot in snapshot.children) {
                    val group: GroupData = dataSnapshot.getValue(GroupData::class.java)!!
                    if (group.listUidMember == listUidMember) {
                        check = true
                        idGroup = group.gid
                        break
                    }
                }
                val intent = Intent(
                    getAttachActivity()!!,
                    Class.forName(ModuleClassConstants.CHAT_MESSAGE_ACTIVITY)
                )
                val bundle = Bundle()
                if (check) {
                    val group = GroupData()
                    with(group) {
                        this.gid = idGroup
                        this.name = name
                        imageId = avt
                    }
                    bundle.putString("uidChat", uid)
                    bundle.putString("DATA_GROUP", Gson().toJson(group))

                } else {
                    val gid: String = DbReference.writeNewGroup(
                        name,
                        listUidMember,
                        avt,
                        false,
                        "",
                        ""
                    )
                    for (i in listUidMember.indices) {
                        DbReference.updateUserGroups(listUidMember[i], gid)
                    }
                    val group = GroupData()
                    with(group) {
                        this.gid = gid
                        this.name = name
                        imageId = avt
                    }
                    bundle.putString("uidChat", uid)
                    bundle.putString("DATA_GROUP", Gson().toJson(group))
                }

                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


}