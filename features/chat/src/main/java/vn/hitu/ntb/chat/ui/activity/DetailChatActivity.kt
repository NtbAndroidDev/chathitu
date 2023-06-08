package vn.hitu.ntb.chat.ui.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import org.greenrobot.eventbus.EventBus
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.cache.UserDataCache
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.ActivityDetailChatBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.MemberChatAdapter
import vn.hitu.ntb.chat.ui.handle.FCMSend
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.eventbus.EventbusChangeBackground
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.IsLeader
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.ui.dialog.DialogAgree
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show
import vn.hitu.ntb.utils.PhotoPickerUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
/**
 * @Update: NGUYEN THANH BINH
 * @Date: 07/12/2022
 */
class DetailChatActivity : AppActivity(), MemberChatAdapter.ClickUser {
    private lateinit var binding: ActivityDetailChatBinding
    private var mAuth: FirebaseAuth? = null
    private val listMember: ArrayList<UserData> = ArrayList()
    private var adapter: MemberChatAdapter? = null
    private var group = GroupData()
    var dialog: AlertDialog.Builder? = null
    private var imageUri: Uri? = null
    private var localMedia: LocalMedia? = null
    private var mStorage: StorageReference? = null
    private var listUidMember: ArrayList<String> = ArrayList()
    private var didUserChat: String = ""


    override fun getLayoutView(): View {
        binding = ActivityDetailChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.profileToolbar)

        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference

        initViewLeader()

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey("DATA_GROUP")) {
                group = Gson().fromJson(bundleIntent.getString("DATA_GROUP"), GroupData::class.java)
                didUserChat = bundleIntent.getString("didUserChat")!!
            }
        }


        //avatar
        AppUtils.loadImageUser(binding.civImage, group.imageId)
        //Name
        binding.textviewName.text = group.name



        //back button
        binding.btnClose.setOnClickListener { finish() }


        //left group btn
        dialog = AlertDialog.Builder(this@DetailChatActivity)
        binding.btnLeaveGroup.setOnClickListener {
            dialog!!.setTitle("Leave group")
                .setMessage("Are you sure want to leave the group?")
                .setCancelable(true)
                .setPositiveButton("Yes"
                ) { _, _ ->
                    Toast.makeText(this@DetailChatActivity, "Yes", Toast.LENGTH_SHORT).show()
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("Groups").child(group.gid)
                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val group: GroupData = snapshot.getValue(GroupData::class.java)!!
                            val listUser: ArrayList<String> = group.listUidMember
                            listUser.remove(Auth.getAuth())
                            val userValues: Map<String, Any> = group.toMap()
                            val userUpdates: MutableMap<String, Any> = HashMap()
                            userUpdates["/Groups/${group.gid}"] = userValues
                            FirebaseDatabase.getInstance().reference.updateChildren(userUpdates)
                            val intent: Intent = Intent(
                                this@DetailChatActivity,
                                HomeActivity::class.java
                            )
                            AppUtils.sendMessage("${UserDataCache.getUser().name} đã rời khỏi nhóm", group.gid)
                            startActivity(intent)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
                .setNegativeButton("No"
                ) { _, _ ->
                    Toast.makeText(
                        this@DetailChatActivity,
                        "No",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            val alert: AlertDialog = dialog!!.create()
            alert.setTitle("Leave group")
            alert.show()
        }




        binding.civImage.setOnClickListener {
            PhotoPickerUtils.showImagePickerChooseAvatar(
                this, pickerImageIntent
            )
        }

        binding.btnAddGroup.setOnClickListener {
            val intent = Intent(this, AddMemberGroupActivity::class.java)
            val bundle = Bundle()
            bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))
            bundle.putString("didUserChat", didUserChat)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.btnAddMember.setOnClickListener {
            val intent = Intent(this, AddMemberGroupActivity::class.java)
            val bundle = Bundle()
            bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))
            bundle.putString("didUserChat", didUserChat)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.llChangeBackground.setOnClickListener {
            EventBus.getDefault().post(EventbusChangeBackground(1))
            finish()
        }

        binding.llQrGroup.setOnClickListener {
            val intent = Intent(this, QrGroupActivity::class.java)
            val bundle = Bundle()
            bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    fun getMember(){
        //get user of group
        val mDatabase = FirebaseDatabase.getInstance()
        val myRef = mDatabase.getReference("Groups").child(group.gid)
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupData: GroupData = snapshot.getValue(GroupData::class.java)!!
                group.listUidMember = groupData.listUidMember
                listUidMember = groupData.listUidMember
                binding.llGroupMember.visibility = View.VISIBLE
                if (!groupData.isOnline) {
                    binding.civOnlineCircle.borderColor =
                        this@DetailChatActivity.resources.getColor(vn.hitu.ntb.R.color.yellow_circle)
                    binding.textviewActive.text = "Offline"
                } else {
                    binding.civOnlineCircle.borderColor =
                        this@DetailChatActivity.resources.getColor(vn.hitu.ntb.R.color.green_circle)
                    binding.textviewActive.text = "Active"
                }

                //
                listMember.clear()
                for (i in listUidMember.indices) {
                    val idUser = listUidMember[i]
                    if (idUser == Auth.getAuth())
                        continue
                    val database = FirebaseDatabase.getInstance()
                    val myRef2 = database.getReference("Users").child(idUser)
                    myRef2.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user: UserData = snapshot.getValue(UserData::class.java)!!
                            listMember.add(user)
//                            adapter!!.notifyItemInserted(listMember.size - 1)
                            adapter!!.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    /**
     * Sự kiện thay đổi avatar
     */
    private var pickerImageIntent: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent = it.data!!


            localMedia = PictureSelector.obtainMultipleResult(data)[0]
            AppUtils.loadImageUser(binding.civImage, localMedia!!.cutPath)
            var bmp: Bitmap? = null
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(localMedia!!.path))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)

            uploadImageToFirebase(baos.toByteArray())

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && data != null) {
            imageUri = data.data
            var bmp: Bitmap? = null
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val fileInBytes = baos.toByteArray()
            uploadImageToFirebase(fileInBytes)
        }
    }

    private fun uploadImageToFirebase(fileInBytes: ByteArray): String {
        showDialog()
        val imageId = UUID.randomUUID().toString() + ".jpg"
        val imgRef = mStorage!!.child("images/$imageId")
        val uploadTask = imgRef.putBytes(fileInBytes)
        uploadTask.addOnFailureListener {
            toast("Upload image failed!")
        }.addOnSuccessListener {
            DbReference.changeImageGroup(group.gid, imageId)
            hideDialog()
        }
        return imageId
    }

    override fun listenerUser(item: UserData, position: Int) {
        val intent = Intent(this@DetailChatActivity, Class.forName(ModuleClassConstants.INFO_CUSTOMER))
        val bundle = Bundle()
        bundle.putString(AppConstants.ID_USER, item.uid)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onRemove(item: UserData, position: Int) {

        DialogAgree.Builder(this, "Xoá thành viên", "Xoá ${item.name} ra khỏi nhóm")
            .onActionDone(object : DialogAgree.Builder.OnActionDone{
                override fun onActionDone(isConfirm: Boolean) {
                    if (isConfirm){
                        DbReference.removeUserGroup(item.uid, group.gid)
//                        toast("Đã xoá ${item.name} ra khỏi nhóm")
                        sendMessage("Đã xoá ${item.name} ra khỏi nhóm")
                    }
                }
            })
            .show()
    }


    private fun initViewLeader(){
        //get user of group
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Leader").child(group.gid).addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.children){
                    val item: IsLeader = data.getValue(IsLeader::class.java)!!
                    if (item.group_id == group.gid){
                        if (item.id == Auth.getAuth()){
                            adapter = MemberChatAdapter(this@DetailChatActivity, true)
                            binding.civImage.isEnabled = true
                            binding.civPen.show()
                        }else {
                            adapter = MemberChatAdapter(this@DetailChatActivity, false)
                            binding.civImage.isEnabled = false
                            binding.civPen.hide()
                        }
                        adapter!!.setClickUser(this@DetailChatActivity)
                        AppUtils.initRecyclerViewVertical(binding.recyclerViewGroupMember, adapter)
                        adapter!!.setData(listMember)
                        getMember()
                        adapter!!.notifyDataSetChanged()
                        break
                    }

                }




            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
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
                        didUserChat,
                        user.name,
                        notification,
                        user.image
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}
