package vn.hitu.ntb.chat.ui.activity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.databinding.ActivityForwardingMessageBinding
import vn.hitu.ntb.chat.interfaces.ChooseItemListener
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.MyFriendAdapter
import vn.hitu.ntb.utils.FCMSend
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.other.doAfterTextChanged
import vn.hitu.ntb.utils.AppUtils
import java.sql.Timestamp
import java.util.*

class ForwardingMessageActivity : AppActivity(), ChooseItemListener {
    lateinit var binding: ActivityForwardingMessageBinding
    private var mAuth: FirebaseAuth? = null
    private var db: DbReference? = null
    private var mStorage: StorageReference? = null
    var databaseReference: DatabaseReference? = null
    private var myFriendAdapter: MyFriendAdapter? = null
    private var friendList: ArrayList<UserData> = ArrayList()
    private var friendListTmp: ArrayList<UserData> = ArrayList()
    private val listIdGroup: ArrayList<String> = ArrayList()
    private var didUserChat: String? = null
    private var message = ChatMessage()
    private val listGroup: ArrayList<GroupData> = ArrayList()


    override fun getLayoutView(): View {
        binding = ActivityForwardingMessageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
        myFriendAdapter = MyFriendAdapter(this, 0)
        AppUtils.initRecyclerViewVertical(binding.rcvListFriend, myFriendAdapter)
        myFriendAdapter!!.setData(friendList)
        myFriendAdapter!!.setClickChooseItem(this)
    }

    override fun initData() {
        val bundle = intent.extras
        message = Gson().fromJson(bundle!!.getString("MESSAGE"), ChatMessage::class.java)
        didUserChat = bundle.getString("didUserChat")
        val date = Date()
        val timestamp = Timestamp(date.time)
        val currentTime = timestamp.toString()
        message.messageTime = currentTime
        db = DbReference
        FirebaseDatabase.getInstance().getReference("Groups")
            .orderByChild("lastTime")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listGroup.clear()
                    for (dataSnapshot in snapshot.children) {
                        val group: GroupData? = dataSnapshot.getValue(GroupData::class.java)
                        if (group!!.listUidMember
                                .contains(Auth.getAuth()) && group.lastMessage.isNotEmpty()
                        ) {
                            listGroup.add(0, group)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        getFullFriend()

        binding.svUser.doAfterTextChanged(500){
            search(it, friendListTmp)
        }

        binding.btnSend.setOnClickListener{
            for (i in listIdGroup.indices) {
                val id: String = listIdGroup[i]
                for (j in listGroup.indices) {
                    if (listGroup[j].listUidMember.contains(id)) {
                        listIdGroup[i] = listGroup[j].gid
                    }
                }
            }
            for (i in listIdGroup.indices) {
                sendMessage(message, listIdGroup[i])
            }
        }

    }
    private fun getFullFriend() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                friendList.clear()
                for (dataSnapshot in snapshot.children) {
                    val user: UserData = dataSnapshot.getValue(UserData::class.java)!!
                    friendList.add(user)
                }
                friendListTmp.addAll(friendListTmp)
                myFriendAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
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
            friendList.addAll(friendListTmp)
        }
        friendList.forEach { it.checked = if (listIdGroup.contains(it.uid)) 1 else 0 }

        myFriendAdapter!!.notifyDataSetChanged()
    }


    override fun clickChooseItem(position: Int, isChecked: Boolean, item: Friend) {

        if (isChecked){
            friendList[position].checked = 1
            listIdGroup.add(friendList[position].uid)
        }else
        {
            friendList[position].checked = 0
            listIdGroup.remove(friendList[position].uid)
        }

        if (listIdGroup.isEmpty())
            binding.btnSend.visibility = View.GONE
        else
            binding.btnSend.visibility = View.VISIBLE


    }
    private fun sendMessage(chat: ChatMessage, idGroup: String) {
        //path: ChatMessage
        val ref = FirebaseDatabase.getInstance().reference
        val messageId = ref.child("ChatMessage").child(idGroup).push().key
        chat.messageId = messageId!!
        val messUpdates: MutableMap<String, Any> = HashMap()
        val messValues = chat.toMap()
        //path/ChatMessage/idGroup/messageId
        messUpdates["/ChatMessage/$idGroup/$messageId"] = messValues
        ref.updateChildren(messUpdates)
        val refGroups = FirebaseDatabase.getInstance().getReference("Groups").child(idGroup)
        val refMessageGroup = refGroups.child("lastMessage")
        val refTimeGroup = refGroups.child("lastTime")
        refTimeGroup.setValue(chat.messageTime)
        var notification = ""
        notification = when (chat.typeMessage) {
            "image" -> {
                refMessageGroup.setValue("image")
                "sent a picture"
                // :(
            }
            "file" -> {
                refMessageGroup.setValue("file pdf")
                "sent a file"
            }
            else -> {
                refMessageGroup.setValue(chat.message)
                chat.message
                // :(
            }
        }
        val finalNotification = notification
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: UserData? = snapshot.getValue(UserData::class.java)
                    FCMSend.pushNotification(
                        applicationContext,
                        idGroup,
                        didUserChat,
                        user!!.name,
                        finalNotification,
                        user.image,
                        notification
                    )
                    finish()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ForwardingMessageActivity,
                        "Send failed!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })

    }


}