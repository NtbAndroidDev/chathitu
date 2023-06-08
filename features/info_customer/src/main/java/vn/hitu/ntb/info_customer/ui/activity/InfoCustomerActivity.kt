package vn.hitu.ntb.info_customer.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
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
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.info_customer.databinding.ActivityInfoCustomerBinding
import vn.hitu.ntb.model.entity.*
import vn.hitu.ntb.ui.dialog.DialogFeedBack
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

/**
 * @Update: NGUYEN THANH BINH
 * @Date: 07/12/2022
 */
class InfoCustomerActivity : AppActivity() {
    private lateinit var binding: ActivityInfoCustomerBinding
    private var id = ""
    var dialog: AlertDialog.Builder? = null
    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private val friendRef = mDatabase.getReference("Friends")
    private val requestRef = mDatabase.getReference("Requests")
    private var user = UserData()
    override fun getLayoutView(): View {
        binding = ActivityInfoCustomerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.profileToolbar)


        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.ID_USER)) {
                id = bundleIntent.getString(AppConstants.ID_USER)!!
            }
        }



    }


    override fun initData() {


        //get user of group
        val mDatabase = FirebaseDatabase.getInstance()
        val myRef = mDatabase.getReference("Users").child(id)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(UserData::class.java)!!

                //set info
                binding.textviewName.text = user.name
                binding.textviewUserName.text = user.name
                binding.textviewEmailUser.text = user.email
                if (!user.isOnline) {
                    binding.civOnlineCircle.borderColor =
                        this@InfoCustomerActivity.resources
                            .getColor(vn.hitu.ntb.R.color.yellow_circle, null)
                    binding.textviewActive.text = "Offline"
                } else {
                    binding.civOnlineCircle.borderColor =
                        this@InfoCustomerActivity.resources
                            .getColor(vn.hitu.ntb.R.color.green_circle, null)
                    binding.textviewActive.text = "Active"
                }


                //img
                FirebaseStorage.getInstance().reference.child("images/" + user.image)
                    .downloadUrl.addOnSuccessListener {
                        Picasso.get().load(it).into(binding.civImage)

                    }.addOnFailureListener { }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })




        //back button
        binding.btnClose.setOnClickListener { finish() }


        //left group btn
        dialog = AlertDialog.Builder(this@InfoCustomerActivity)


        binding.llMakeFriend.setOnClickListener {
            DbReference.requestFriend(id, this)
            binding.llRecall.show()
            binding.llMakeFriend.hide()
            binding.llFriend.hide()
            binding.llFeedBack.hide()
        }
        binding.llRecall.setOnClickListener {
            DbReference.cancelRequest(id, this)
            binding.llRecall.hide()
            binding.llMakeFriend.show()
            binding.llFriend.hide()
            binding.llFeedBack.hide()
        }

        binding.llFeedBack.setOnClickListener {
            DialogFeedBack.Builder(this).setListener(object : DialogFeedBack.ClickAddFriend {
                override fun onClickAddFriend(type: Int) {
                    if (type == 1) {
                        DbReference.acceptFriend(user,this@InfoCustomerActivity)
                        binding.llMakeFriend.hide()
                        binding.llFriend.show()
                        binding.llFeedBack.hide()
                        binding.llRecall.hide()
                    } else {
                        DbReference.refuseRequest(id, this@InfoCustomerActivity)
                        binding.llMakeFriend.show()
                        binding.llFriend.hide()
                        binding.llFeedBack.hide()
                        binding.llRecall.hide()
                    }

                }
            }).show()

            binding.lnMessage.setOnClickListener {
                goToChat(user.uid, user.name, user.image)
            }

        }


        initViewFriend(id)

    }


    private fun initViewFriend(userId : String){
        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    for (data in dataSnapshot.children){
                        val item = data.getValue(IsFriend::class.java)
                        if (item!!.yId == userId && item.mId == mAuth.currentUser!!.uid){
                            binding.llFriend.show()
                            binding.llMakeFriend.hide()
                            binding.llRecall.hide()
                            binding.llFeedBack.hide()
                            break
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        requestRef.child(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()){
                    val data = dataSnapshot.getValue(RequestFriend::class.java)
                    when(data!!.contactType){
                        3 -> {
                            binding.llRecall.show()
                            binding.llMakeFriend.hide()
                            binding.llFriend.hide()
                            binding.llFeedBack.hide()
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        requestRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()){
                        val data = dataSnapshot.getValue(RequestFriend::class.java)



                        when(data!!.contactType){
                            3 -> {
                                binding.llRecall.hide()
                                binding.llMakeFriend.hide()
                                binding.llFriend.hide()
                                binding.llFeedBack.show()
                            }

                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
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
                    this@InfoCustomerActivity,
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