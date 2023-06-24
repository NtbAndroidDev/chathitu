package vn.hitu.ntb.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.databinding.FragmentFoodBinding
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.other.doAfterTextChanged
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.ui.adapter.ListGroupAdapter
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.removeVietnameseFromString
import java.util.Locale

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 */
class FoodFragment : AppFragment<HomeActivity>(), ListGroupAdapter.OnListener {

    private lateinit var binding: FragmentFoodBinding
    private var groupDataList = ArrayList<GroupData>()
    private var groupDataListTmp = ArrayList<GroupData>()
    private var adapterGroupChat: ListGroupAdapter? = null


    //database
    private var mDatabase: DatabaseReference? = null
    private var mStorage: StorageReference? = null
    private var mAuth: FirebaseAuth? = null

    companion object {
        fun newInstance(): FoodFragment {
            return FoodFragment()
        }
    }

    override fun getLayoutView(): View {
        binding = FragmentFoodBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

        //database
        mDatabase = DbReference.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        adapterGroupChat = ListGroupAdapter(getAttachActivity()!!)
        adapterGroupChat!!.setOnListener(this)
        AppUtils.initRecyclerViewVertical(binding.recyclerViewGroupList, adapterGroupChat)
        adapterGroupChat!!.setData(groupDataList)
    }

    override fun initData() {

        getGroupChat()


        binding.btnAddGroup.setOnClickListener {
            try {
                val intent = Intent(
                    getAttachActivity(),
                    Class.forName(ModuleClassConstants.CREATE_GROUP_CHAT)
                )
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }


        binding.searchViewSearch.doAfterTextChanged(500){
            search(it, groupDataListTmp)
        }
    }


    private fun getGroupChat() {
        FirebaseDatabase.getInstance().getReference("Groups")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupDataList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = mAuth!!.currentUser
                        val group: GroupData = dataSnapshot.getValue(GroupData::class.java)!!
                        if (group.listUidMember.size > 2 && group.listUidMember.toString().contains(user!!.uid)) {
                            groupDataList.add(0,group)
                        }
                    }
                    adapterGroupChat!!.notifyDataSetChanged()
                    groupDataListTmp.addAll(groupDataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, "Get groups failed!", Toast.LENGTH_SHORT).show()
                }
            })

    }



    override fun clickGroup(position: Int) {
        try {
            val intent = Intent(context, Class.forName(ModuleClassConstants.CHAT_MESSAGE_ACTIVITY))
            val bundle = Bundle()
            val group = GroupData()
            with(group){
                gid = groupDataList[position].gid
                name = groupDataList[position].name
                imageId = groupDataList[position].imageId
                background =  groupDataList[position].background
            }

            val uidChat: String =
                if (Auth.getAuth() == groupDataList[position].listUidMember[0])
                    groupDataList[position].listUidMember[1]
                else groupDataList[position].listUidMember[0]
            bundle.putString("uidChat", uidChat)
            bundle.putString("DATA_GROUP", Gson().toJson(group))
            intent.putExtras(bundle)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun search(charText: String, data: ArrayList<GroupData>) {

        groupDataList.clear()
        val finalCharText = charText.lowercase(Locale.getDefault())
        if (charText != "") {
            val tmpList: ArrayList<GroupData> = ArrayList()
            tmpList.addAll(data.filter {
                it.name.lowercase().contains(finalCharText)
                        || removeVietnameseFromString(it.name).lowercase().contains(finalCharText)

            })


            groupDataList.addAll(tmpList)
        } else {
            groupDataList.addAll(data)
        }

        adapterGroupChat!!.notifyDataSetChanged()
    }


}