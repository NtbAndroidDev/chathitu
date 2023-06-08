package vn.hitu.ntb.chat.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.ActivitySearchUserBinding
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.other.doAfterTextChanged
import vn.hitu.ntb.ui.adapter.SearchUserAdapter
import vn.hitu.ntb.utils.AppUtils
import java.util.Locale

class SearchUserActivity : AppActivity(), SearchUserAdapter.ClickUser {
    private lateinit var binding : ActivitySearchUserBinding
    private var adapter : SearchUserAdapter? = null
    private var dataList: ArrayList<UserData> = ArrayList()
    private var dataListTmp: ArrayList<UserData> = ArrayList()
    private var dataListFirst: ArrayList<UserData> = ArrayList()
    private var mAuth: FirebaseAuth? = null
    var databaseReference: DatabaseReference? = null

    override fun getLayoutView(): View {
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(
            vn.hitu.ntb.R.anim.window_ios_in,
            vn.hitu.ntb.R.anim.window_ios_out
        )
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
            vn.hitu.ntb.R.anim.window_ios_in,
            vn.hitu.ntb.R.anim.window_ios_out
        )
    }
    override fun initView() {

        ImmersionBar.setTitleBar(this, binding.toolbar)


        mAuth = FirebaseAuth.getInstance()

        adapter = SearchUserAdapter(this)
        adapter!!.setClickUser(this)
        AppUtils.initRecyclerViewVertical(binding.rcvSearchUser, adapter!!)
        adapter!!.setData(dataList)

    }

    override fun initData() {

        binding.btnClose.setOnClickListener {
            finish()
        }


        searchFullUser()

        binding.etSearchUser.doAfterTextChanged(500){

            search(it, dataListTmp)
        }


    }

    private fun searchFullUser() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                var i = 0
                for (dataSnapshot in snapshot.children) {
                    i++
                    val user: UserData = dataSnapshot.getValue(UserData::class.java)!!
                    if (i < 10) {
                        if (Auth.getAuth() != user.uid) {
                            dataListFirst.add(user)
                            dataListTmp.add(user)
                        }
                    }else{
                        if (Auth.getAuth() != user.uid) {
                            dataListTmp.add(user)
                        }
                    }
                    //

                }
                dataList.addAll(dataListFirst)
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun search(charText: String, data: ArrayList<UserData>) {

        dataList.clear()
        val finalCharText = charText.lowercase(Locale.getDefault())
        if (charText != "") {
            val tmpList: ArrayList<UserData> = ArrayList()
            tmpList.addAll(data.filter {
//                        it.name.lowercase().contains(finalCharText)
//                        || AppUtils.removeVietnameseFromString(it.name).lowercase().contains(finalCharText) ||
                        it.email.lowercase().contains(finalCharText)
                        || AppUtils.removeVietnameseFromString(it.email).lowercase().contains(finalCharText)

            })


            dataList.addAll(tmpList)
        } else {
            dataList.addAll(dataListFirst)
        }

        adapter!!.notifyDataSetChanged()
    }

    override fun listenerUser(item: UserData, position: Int) {
        val user: UserData = item
        val listUidMember = ArrayList<String>()
        listUidMember.add(Auth.getAuth())
        listUidMember.add(user.uid)
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
                    this@SearchUserActivity,
                    ChatActivity::class.java
                )
                val bundle = Bundle()
                if (check) {
                    val group = GroupData()
                    with(group) {
                        this.gid = idGroup
                        name = user.name
                        imageId = user.image
                        this.listUidMember = listUidMember

                    }
                    bundle.putString("uidChat", user.uid)
                    bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))

                } else {
                    val gid: String = DbReference.writeNewGroup(user.name, listUidMember, user.image, false, "", "")
                    for (i in listUidMember.indices) {
                        DbReference.updateUserGroups(listUidMember[i], gid)
                    }
                    val group = GroupData()
                    with(group) {
                        this.gid = gid
                        name = user.name
                        imageId = user.image
                        this.listUidMember = listUidMember

                    }
                    bundle.putString("uidChat", user.uid)
                    bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))
                }

                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}