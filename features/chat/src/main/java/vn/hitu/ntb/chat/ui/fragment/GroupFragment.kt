package vn.hitu.ntb.chat.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.FragmentGroupBinding
import vn.hitu.ntb.chat.interfaces.GroupChatListener
import vn.hitu.ntb.chat.interfaces.ManagerMemberListener
import vn.hitu.ntb.chat.model.entity.*
import vn.hitu.ntb.chat.ui.activity.ChatActivity
import vn.hitu.ntb.chat.ui.activity.SearchUserActivity
import vn.hitu.ntb.chat.ui.adapter.FriendOnlineAdapter
import vn.hitu.ntb.chat.ui.adapter.GroupAdapter
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.GroupChat
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils

/**
 * @Update: NGUYEN THANH BINH
 */
class GroupFragment : AppFragment<HomeActivity>(), OnRefreshLoadMoreListener,
    GroupChatListener, ManagerMemberListener {

    private lateinit var binding: FragmentGroupBinding
    private var groupDataList = ArrayList<GroupData>()
    private var groupUserList = ArrayList<GroupData>()
    private var adapterFriendOnline: FriendOnlineAdapter? = null
    private var adapterGroupChat: GroupAdapter? = null
    private var userAnother: String = ""


    //database
    private var mDatabase: DatabaseReference? = null
    private var mStorage: StorageReference? = null
    private var mAuth: FirebaseAuth? = null

    companion object {
        fun newInstance(): GroupFragment {
            return GroupFragment()
        }
    }


    override fun getLayoutView(): View {
        binding = FragmentGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {



        //database
        mDatabase = DbReference.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()


        adapterFriendOnline = FriendOnlineAdapter(getAttachActivity()!!)
        adapterFriendOnline!!.setUserListener(this)
        adapterGroupChat = GroupAdapter(getAttachActivity()!!)
        adapterGroupChat!!.setGroupChatListener(this)

        adapterGroupChat!!.setHasStableIds(true)
        binding.rclGroupChat.setHasFixedSize(true)
        binding.rclGroupChat.setItemViewCacheSize(20)

        AppUtils.initRecyclerViewVertical(binding.rclGroupChat, adapterGroupChat)
        AppUtils.initRecyclerViewHorizontal(
            binding.itemFriendOnline.rcvFriendOnline,
            adapterFriendOnline
        )

        adapterGroupChat!!.setData(groupDataList)
        adapterFriendOnline!!.setData(groupUserList)

        binding.smartRefreshLayout.setOnRefreshLoadMoreListener(this)

//        binding.shimmerGroupContainer.visibility = View.VISIBLE
//        binding.shimmerGroupContainer.startShimmer()
//        binding.lnEmptyGroupChat.visibility = View.GONE
//        binding.rclGroupChat.visibility = View.GONE
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun initData() {


        // ShareReferen
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val showNotification = sharedPreferences.getBoolean("notification", true)
        val status = sharedPreferences.getBoolean("status", true)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = it.result
                if (showNotification) {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(Auth.getAuth()).child("did").setValue(token)
                }
                // Log and toast
                Log.i("TokenDevice", token)
                //                        Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
            }


        if (status) {
            DbReference.writeIsOnlineUserAndGroup(Auth.getAuth(), true)
        } else {
            DbReference.writeIsOnlineUserAndGroup(Auth.getAuth(), false)
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = it.result
                if (showNotification) {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(Auth.getAuth()).child("did").setValue(token)
                }
                // Log and toast
                Log.i("TokenDevice", token)
                //                        Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
            }
        getGroupChat()


        adapterGroupChat!!.notifyDataSetChanged()
        adapterFriendOnline!!.notifyDataSetChanged()


        binding.itemFriendOnline.searchBtn.setOnClickListener {
            try {
                val intent = Intent(
                    getAttachActivity()!!, SearchUserActivity::class.java
                )
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Gọi api lấy danh sách cuộc trò chuyện
     */
    @SuppressLint("HardwareIds", "NotifyDataSetChanged")
    private fun getGroupChat() {
        showDialog()
        FirebaseDatabase.getInstance().getReference("Groups")
            .orderByChild("lastTime")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupDataList.clear()
                    groupUserList.clear()
                    groupUserList.add(GroupData())
                    for (dataSnapshot in snapshot.children) {
                        val group: GroupData? = dataSnapshot.getValue(GroupData::class.java)

                        //get uid of user other than the current user
                        if (group!!.listUidMember.contains(Auth.getAuth()) && group.lastMessage.isNotEmpty()) {
                            if (group.listUidMember.size == 2) {
                                userAnother =
                                    if (group.listUidMember[0] == Auth.getAuth()
                                    ) {
                                        group.listUidMember[1]
                                    } else {
                                        group.listUidMember[0]
                                    }
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userAnother)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val user: UserData? =
                                                snapshot.getValue(UserData::class.java)
//                                            Log.i("user_online", user!!.name + " " + user!!.isOnline)
                                            group.name = user!!.name
                                            group.imageId = user.image
                                            group.isOnline = user.isOnline
                                            adapterFriendOnline!!.notifyDataSetChanged()
                                            adapterGroupChat!!.notifyDataSetChanged()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                            }
                            groupDataList.add(0, group)
                        }
                    }
                    groupUserList.addAll(groupDataList)
//                    adapterFriendOnline!!.notifyDataSetChanged()
//                    adapterGroupChat!!.notifyDataSetChanged()
                    hideDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, "Get groups failed!", Toast.LENGTH_SHORT).show()
                    showDialog()
                }
            })

    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        postDelayed({

            binding.lnEmptyGroupChat.visibility = View.GONE


            getGroupChat()

            this.binding.smartRefreshLayout.finishRefresh()
        }, 1000)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        //
    }


    override fun clickGroup(position: Int) {


        try {
            Log.d("item_error", Gson().toJson(groupDataList[position]))
            val intent = Intent(context, ChatActivity::class.java)
            val bundle = Bundle()
            val group = GroupData()
            with(group) {
                gid = groupDataList[position].gid
                name = groupDataList[position].name
                imageId = groupDataList[position].imageId
                listUidMember = groupDataList[position].listUidMember
                background = groupDataList[position].background
            }

            val uidChat: String =
                if (Auth.getAuth() == groupDataList[position].listUidMember[0])
                    groupDataList[position].listUidMember[1]
                else
                    groupDataList[position].listUidMember[0]
            bundle.putString("uidChat", uidChat)
            bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))
            intent.putExtras(bundle)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

    }

    override fun clickMember(id: Int) {
        try {
            val intent = Intent(context, ChatActivity::class.java)
            val bundle = Bundle()
            val group = GroupData()
            with(group) {
                gid = groupDataList[id - 1].gid
                name = groupDataList[id - 1].name
                imageId = groupDataList[id - 1].imageId
            }

            val uidChat: String =
                if (Auth.getAuth() == groupDataList[id - 1].listUidMember[0])
                    groupDataList[id - 1].listUidMember[1]
                else groupDataList[id - 1].listUidMember[0]
            bundle.putString("uidChat", uidChat)
            bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))
            intent.putExtras(bundle)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

    }


    private fun checkPinned(array: ArrayList<GroupChat>): Int {
        var size = 0
        for (i in 0 until array.size) {
            if (array[i].isPinned == 1 && i != 0)
                size++
        }
        return size + 1
    }


}