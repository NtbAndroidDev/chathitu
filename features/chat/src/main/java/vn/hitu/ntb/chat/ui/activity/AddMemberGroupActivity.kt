package vn.hitu.ntb.chat.ui.activity

import android.annotation.SuppressLint
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.ActivityAddMemberGroupBinding
import vn.hitu.ntb.chat.interfaces.ChooseItemListener
import vn.hitu.ntb.chat.interfaces.RemoveItemListener
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.ChooseFriendAdapter
import vn.hitu.ntb.chat.ui.adapter.MyFriendAdapter
import vn.hitu.ntb.utils.FCMSend
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.other.doAfterTextChanged
import vn.hitu.ntb.utils.AppUtils
import java.sql.Timestamp
import java.util.Date
import java.util.Locale


class AddMemberGroupActivity : AppActivity(), ChooseItemListener, RemoveItemListener {
    private lateinit var binding: ActivityAddMemberGroupBinding
    private var friendList: ArrayList<UserData> = ArrayList()
    private var firstData: ArrayList<UserData> = ArrayList()
    private var friendListTmp: ArrayList<UserData> = ArrayList()
    private var chooseFriendList: ArrayList<UserData> = ArrayList()
    private var myFriendAdapter: MyFriendAdapter? = null
    private var chooseFriendAdapter: ChooseFriendAdapter? = null
    var group: GroupData = GroupData()
    private var member: ArrayList<String> = ArrayList()
    private var btnOne = 0
    private var btnTwo = 1
    private var btnThree = 0
    private var mAuth: FirebaseAuth? = null
    private var db: DbReference? = null
    private var mStorage: StorageReference? = null
    var databaseReference: DatabaseReference? = null
    private var didUserChat: String = ""

    override fun getLayoutView(): View {
        binding = ActivityAddMemberGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
        ImmersionBar.setTitleBar(this, binding.itemViewToAddMember.tbHeaderAddMember)

        myFriendAdapter = MyFriendAdapter(this, 1)
        AppUtils.initRecyclerViewVertical(binding.rcvMyFriend, myFriendAdapter)
        myFriendAdapter!!.setClickChooseItem(this)

        chooseFriendAdapter = ChooseFriendAdapter(this)
        AppUtils.initRecyclerViewHorizontal(
            binding.itemBottomView.rcvChooseItem,
            chooseFriendAdapter
        )
        chooseFriendAdapter!!.setClickRemove(this)

        chooseFriendAdapter!!.setData(chooseFriendList)
        myFriendAdapter!!.setData(friendList)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        binding.itemViewToAddMember.tbHeaderAddMember.visibility = View.VISIBLE
        binding.itemViewToAddMember.lnGroupChat.visibility = View.VISIBLE
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey("DATA_GROUP")) {
                group = Gson().fromJson(bundleIntent.getString("DATA_GROUP"), GroupData::class.java)
                didUserChat = bundleIntent.getString("didUserChat")!!

            }
        }
        binding.itemViewToAddMember.llNameGroup.visibility = View.GONE
        setOnClickListener(
            binding.itemBottomView.ibCreate, binding.itemViewToAddMember.btnCamera,
            binding.itemViewToAddMember.tvLately,
            binding.itemViewToAddMember.tvPhoneBook,
            binding.itemViewToAddMember.tvSuggest,
            binding.itemViewToAddMember.lnHeader,
            binding.itemViewToAddMember.ibClose
        )
        getFriend()
        binding.itemViewToAddMember.ibClose.setOnClickListener {
            finish()
        }

        binding.itemViewToAddMember.edtSearch.doAfterTextChanged(500) {
            search(it, friendListTmp)
        }

        binding.itemBottomView.ibCreate.setOnClickListener {
            member.forEach {
                DbReference.addUserGroup(it, group.gid)
                friendListTmp.forEach { f ->
                    if (it == f.uid)
                        sendMessage("${f.name} đã được thêm vào nhóm")
                }
            }
            finish()
        }
    }
    private fun sendMessage(content: String) {
        //path: ChatMessage
        val date = Date()
        val timestamp = Timestamp(date.time)
        val currentTime = timestamp.toString()
        val chat = ChatMessage(currentTime, content, Auth.getAuth(), MessageChatConstants.NOTIFICATION, "")
        val ref = FirebaseDatabase.getInstance().reference
        val messageId = ref.child("ChatMessage").child(group.gid).push().key
        chat.messageId = messageId!!
        val messUpdates: MutableMap<String, Any> = java.util.HashMap()
        val messValues = chat.toMap()
        //path/ChatMessage/idGroup/messageId
        messUpdates["/ChatMessage/${group.gid}/$messageId"] = messValues
        ref.updateChildren(messUpdates)
        val refGroups = FirebaseDatabase.getInstance().getReference("Groups").child(group.gid)
        val refMessageGroup = refGroups.child("lastMessage")
        val refTimeGroup = refGroups.child("lastTime")
        refTimeGroup.setValue(chat.messageTime)
        val notification =  when (chat.typeMessage) {
            MessageChatConstants.NOTIFICATION -> {
                refMessageGroup.setValue(MessageChatConstants.NOTIFICATION)
                content
                // :(
            }
            else -> {
                refMessageGroup.setValue(chat.message)
                chat.message
                // :(
            }
        }
//        binding.rcvListChat.smoothScrollToPosition(messageList.size - 1)
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: UserData = snapshot.getValue(UserData::class.java)!!
                    FCMSend.pushNotification(
                        applicationContext,
                        group.gid,
                        didUserChat,
                        user.name,
                        notification,
                        user.image,
                        MessageChatConstants.NOTIFICATION
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /**
     * sự kiện onclick
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View) {
        when (view) {

            binding.itemBottomView.ibCreate -> {

            }
            
            binding.itemViewToAddMember.tvLately -> {
                if (btnTwo == 0) {
                    btnTwo = 1
                    btnOne = 0
                    btnThree = 0
                    selected()
                    friendList.clear()
                    getFriend()

                }
            }

            binding.itemViewToAddMember.tvPhoneBook -> {
                if (btnThree == 0) {
                    btnThree = 1
                    btnTwo = 0
                    btnOne = 0
                    friendList.clear()
                    selected()
                    
                }
            }

            binding.itemViewToAddMember.lnHeader -> {
                finish()
            }

            binding.itemViewToAddMember.ibClose -> {
                finish()
            }
        }


    }

    

    /**
     * Gọi api lấy danh sách bạn bè
     */
    @SuppressLint("HardwareIds")
    private fun getFriend() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                friendList.clear()
                for (dataSnapshot in snapshot.children) {
                    val user: UserData = dataSnapshot.getValue(UserData::class.java)!!
                    for (i in group.listUidMember.indices){
                        if (Auth.getAuth() != user.uid && checkIsMember(user.uid)) {
                            friendListTmp.add(user)
                            firstData.add(user)
                            break
                        }
                    }


                }
                friendList.addAll(firstData)

                myFriendAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun search(charText: String, data: ArrayList<UserData>) {

        friendList.clear()
        val finalCharText = charText.lowercase(Locale.getDefault())
        if (charText != "") {
            val tmpList: ArrayList<UserData> = ArrayList()
            tmpList.addAll(data.filter {
                it.name.lowercase().contains(finalCharText)
                        || AppUtils.removeVietnameseFromString(it.name).lowercase().contains(finalCharText) ||
                        it.email.lowercase().contains(finalCharText)
                        || AppUtils.removeVietnameseFromString(it.email).lowercase().contains(finalCharText)

            })


            friendList.addAll(tmpList)
        } else {
            friendList.addAll(firstData)
        }

        myFriendAdapter!!.notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")

    override fun clickChooseItem(position: Int, isChecked : Boolean,item: Friend) {

        //toast(isChecked)
        if (isChecked) {
            friendList[position].checked = 1

            var isHas = 0
            if (chooseFriendList.isNotEmpty()) {
                for (i in 1..chooseFriendList.size) {
                    if (chooseFriendList[i - 1].uid == friendList[position].uid) {
                        isHas = 1
                        break
                    }
                }
            }

            if (isHas == 0) {
                chooseFriendList.add(friendList[position])
                member.add(friendList[position].uid)
            }


            chooseFriendAdapter!!.notifyDataSetChanged()
            checkChooseItem(member)
        } else {

            friendList[position].checked = 0
            var isHas = 1
            if (chooseFriendList.isNotEmpty()) {
                for (i in 1..chooseFriendList.size) {
                    if (chooseFriendList[i - 1].uid == friendList[position].uid) {
                        isHas = 0
                        break
                    }
                }
            }

            if (isHas == 0) {
                //chooseFriendList.remove(friendList[position])
                for (i in 1..chooseFriendList.size) {
                    if (chooseFriendList[i - 1].uid == friendList[position].uid) {
                        chooseFriendList.removeAt(i - 1)
                        break
                    }
                }
                member.remove(friendList[position].uid)

                chooseFriendAdapter!!.notifyDataSetChanged()
            }
            checkChooseItem(member)

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun removeItem(position: Int) {
        for (i in 1..friendList.size) {
            if (friendList[i - 1].uid == chooseFriendList[position].uid) {
                friendList[i - 1].checked = 0
                chooseFriendList.removeAt(position)


                member.removeAt(position)

                checkChooseItem(member)


                chooseFriendAdapter!!.notifyDataSetChanged()
                myFriendAdapter!!.notifyDataSetChanged()
                return
            }

        }

        if (chooseFriendList.isNotEmpty()) {
            chooseFriendList.removeAt(position)

            member.removeAt(position)
            chooseFriendAdapter!!.notifyDataSetChanged()
        }


    }






    private fun selected() {
        if (btnOne == 1) {
            binding.itemViewToAddMember.tvSuggest.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button)
            binding.itemViewToAddMember.tvSuggest.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemViewToAddMember.tvSuggest.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button_white)
            binding.itemViewToAddMember.tvSuggest.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.main_bg, null
                )
            )
        }
        if (btnTwo == 1) {
            binding.itemViewToAddMember.tvLately.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button)
            binding.itemViewToAddMember.tvLately.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemViewToAddMember.tvLately.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button_white)
            binding.itemViewToAddMember.tvLately.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.main_bg, null
                )
            )
        }
        if (btnThree == 1) {
            binding.itemViewToAddMember.tvPhoneBook.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button)
            binding.itemViewToAddMember.tvPhoneBook.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemViewToAddMember.tvPhoneBook.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button_white)
            binding.itemViewToAddMember.tvPhoneBook.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.main_bg, null
                )
            )
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun checkChooseItem(array: ArrayList<String>) {
        if (array.size == 0) {
            binding.lnBottomView.visibility = View.GONE
            chooseFriendList.clear()
            member.clear()
        } else {
            binding.lnBottomView.visibility = View.VISIBLE
//            if (array.size == 1) {
//                binding.itemBottomView.ibCreate.setBackgroundResource(vn.hitu.ntb.R.drawable.border_gray_50dp)
//                binding.itemBottomView.ibCreate.isEnabled = false
//            } else {
//                binding.itemBottomView.ibCreate.setBackgroundResource(vn.hitu.ntb.R.drawable.border_blue_50dp)
//                binding.itemBottomView.ibCreate.isEnabled = true
//            }
        }

    }


    private fun checkIsMember(id : String): Boolean{
        for (i in group.listUidMember.indices){
            if (id == group.listUidMember[i])
                return false
        }
        return true
    }

}
