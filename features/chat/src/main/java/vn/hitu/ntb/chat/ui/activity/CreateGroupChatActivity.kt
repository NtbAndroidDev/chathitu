package vn.hitu.ntb.chat.ui.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.R
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.ActivityCreateGroupChatBinding
import vn.hitu.ntb.chat.interfaces.ChooseItemListener
import vn.hitu.ntb.chat.interfaces.RemoveItemListener
import vn.hitu.ntb.chat.ui.adapter.ChooseFriendAdapter
import vn.hitu.ntb.chat.ui.adapter.MyFriendAdapter
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.Medias
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.other.doAfterTextChanged
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.PhotoPickerUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 23/12/2022
 */

class CreateGroupChatActivity : AppActivity(), ChooseItemListener, RemoveItemListener {
    private lateinit var binding: ActivityCreateGroupChatBinding
    private var friendList: ArrayList<UserData> = ArrayList()
    private var firstData: ArrayList<UserData> = ArrayList()
    private var friendListTmp: ArrayList<UserData> = ArrayList()
    private var chooseFriendList: ArrayList<UserData> = ArrayList()
    private var myFriendAdapter: MyFriendAdapter? = null
    private var chooseFriendAdapter: ChooseFriendAdapter? = null
    private var name = ""
    private var avt = ""
    private var member: ArrayList<String> = ArrayList()
    private var localMedia: LocalMedia? = null
    private var medias: ArrayList<Medias> = ArrayList()
    private var keySearch = ""
    private var btnOne = 0
    private var btnTwo = 1
    private var btnThree = 0
    private var mAuth: FirebaseAuth? = null
    private var db: DbReference? = null
    private var mStorage: StorageReference? = null
    var databaseReference: DatabaseReference? = null
    var fileInBytes: ByteArray = ByteArray(1)
    private var progressDialog: ProgressDialog? = null




    override fun getLayoutView(): View {
        binding = ActivityCreateGroupChatBinding.inflate(layoutInflater)
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

        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
        progressDialog = ProgressDialog(this)
        ImmersionBar.setTitleBar(this, binding.itemViewTop.tbHeader)
        myFriendAdapter = MyFriendAdapter(this, 0)
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
        binding.itemViewTop.tvChoose.text =
            String.format("%s %s", getString(R.string.choose), member.size)

        binding.lnBottomView.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        getFullFriend()
        binding.itemViewTop.edtSearch.doAfterTextChanged(delay = 500) {
            friendList.clear()
            myFriendAdapter!!.notifyDataSetChanged()
            keySearch = it
            if (btnThree == 1) {

            } else
                search(it, friendListTmp)
        }

        setOnClickListener(
            binding.itemBottomView.ibCreate, binding.itemViewTop.ibCamera,
            binding.itemViewTop.tvLately,
            binding.itemViewTop.tvPhoneBook,
            binding.itemViewTop.tvSuggest,
            binding.itemViewTop.lnHeader,
            binding.itemViewTop.ibClose
        )
        db = DbReference

    }

    /**
     * sự kiện onclick
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View) {
        when (view) {
            binding.itemBottomView.ibCreate -> {
                name = binding.itemViewTop.edtName.text.toString()
                uploadImageToFirebase(fileInBytes, member)
                return
            }

            binding.itemViewTop.ibCamera -> {
                PhotoPickerUtils.showImagePickerChooseAvatar(
                    this, pickerImageIntent
                )
            }

            binding.itemViewTop.tvSuggest -> {
                if (btnOne == 0) {
                    btnOne = 1
                    btnThree = 0
                    btnTwo = 0
                    selected()
                }
            }

            binding.itemViewTop.tvLately -> {
                if (btnTwo == 0) {
                    btnTwo = 1
                    btnOne = 0
                    btnThree = 0
                    selected()
                    friendList.clear()
                    myFriendAdapter!!.notifyDataSetChanged()
                    getFullFriend()

                }
            }

            binding.itemViewTop.tvPhoneBook -> {
                if (btnThree == 0) {
                    btnThree = 1
                    btnTwo = 0
                    btnOne = 0
                    selected()
//                    binding.lnEmpty.visibility = View.GONE
//                    binding.sflMyFriend.visibility = View.VISIBLE
//                    binding.sflMyFriend.startShimmer()
//                    binding.rcvMyFriend.visibility = View.GONE
//
//                    val dataContact = ArrayList<Friend>()
//                    CoroutineScope(Dispatchers.IO).launch {
//                        AppApplication.appDatabase!!.runInTransaction {
//                            dataContact.clear()
//                            dataContact.addAll(
//                                AppApplication.contactDao!!.getAllData()
//                            )
//
//
//                        }
//                    }
//                    postDelayed({
//                        friendList.clear()
//                        myFriendAdapter!!.notifyDataSetChanged()
////                        friendList.addAll(dataContact)
//
//                        binding.sflMyFriend.visibility = View.GONE
//                        binding.sflMyFriend.stopShimmer()
//                        binding.rcvMyFriend.visibility = View.VISIBLE
//
//                        if (dataContact.isEmpty()) {
//                            binding.rcvMyFriend.visibility = View.GONE
//                            binding.lnEmpty.visibility = View.VISIBLE
//                        } else {
//                            binding.rcvMyFriend.visibility = View.VISIBLE
//                            binding.lnEmpty.visibility = View.GONE
//                        }
//
//
//
//                        friendList.forEach { friend ->
//
//                            member.forEach {
//                                if (it == friend.uid) {
//                                    friend.checked = 1
//                                }
//                            }
//                        }
//                        myFriendAdapter!!.notifyDataSetChanged()
//                    }, 500)

                }
            }

            binding.itemViewTop.lnHeader -> {
                finish()
            }

            binding.itemViewTop.ibClose -> {
                finish()
            }
        }

    }

    /**
     * Sự kiện thay đổi avatar
     */
    private var pickerImageIntent: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent = it.data!!


                localMedia = PictureSelector.obtainMultipleResult(data)[0]
                AppUtils.loadImageUser(binding.itemViewTop.ibCamera, localMedia!!.cutPath)
                var bmp: Bitmap? = null
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(localMedia!!.path))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val baos = ByteArrayOutputStream()

                //here you can choose quality factor in third parameter(ex. i choosen 25)
                bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)

                fileInBytes = baos.toByteArray()
            }
        }




    /**
     * Gọi api lấy danh sách bạn bè
     */
    @SuppressLint("HardwareIds")
    private fun getFullFriend() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                friendList.clear()
                var i = 0
                for (dataSnapshot in snapshot.children) {
                    i++
                    val user: UserData = dataSnapshot.getValue(UserData::class.java)!!
                    if (i < 9) {
                        if (Auth.getAuth() != user.uid) {
                            firstData.add(user)
                            friendListTmp.add(user)

                        }
                    }else{
                        if (Auth.getAuth() != user.uid) {
                            friendListTmp.add(user)
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

            binding.itemViewTop.tvChoose.text =
                String.format("%s %s", getString(R.string.choose), member.size)
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
                binding.itemViewTop.tvChoose.text =
                    String.format("%s %s", getString(R.string.choose), member.size)
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


                binding.itemViewTop.tvChoose.text =
                    String.format("%s %s", getString(R.string.choose), member.size)
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



    private fun uploadImageToFirebase(fileInBytes: ByteArray, member: ArrayList<String>) {
        val groupName: String = binding.itemViewTop.edtName.text.toString()
        if (avt.isEmpty())
            avt = "avtgroup.jpg"
        if (groupName.isEmpty()) {
            toast("Group name invalid!!")

        }else{
            val imageId = UUID.randomUUID().toString() + ".jpg"
            val imgRef = mStorage!!.child("images/$imageId")
            val uploadTask = imgRef.putBytes(fileInBytes)
            uploadTask.addOnFailureListener {
                toast("Upload image failed!")
            }.addOnSuccessListener {
                progressDialog!!.dismiss()
                member.add(Auth.getAuth())
                val groupID = db!!.writeNewGroup(groupName, member, imageId, true, "", "")
                val intent: Intent = Intent(
                    this@CreateGroupChatActivity,
                    ChatActivity::class.java
                )
                val bundle = Bundle()
                val group = GroupData()
                with(group){
                    gid = groupID
                    name = groupName
                    this.imageId = imageId
                }

                val uidChat = Auth.getAuth()
                bundle.putString("uidChat", uidChat)
                bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))
                intent.putExtras(bundle)
                startActivity(intent)
                finish()

            }.addOnProgressListener {
                progressDialog!!.setTitle("Change avatar")
                progressDialog!!.setMessage("Uploading image")
                progressDialog!!.show()
            }
        }

    }



    private fun selected() {
        if (btnOne == 1) {
            binding.itemViewTop.tvSuggest.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button)
            binding.itemViewTop.tvSuggest.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemViewTop.tvSuggest.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button_white)
            binding.itemViewTop.tvSuggest.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.main_bg, null
                )
            )
        }
        if (btnTwo == 1) {
            binding.itemViewTop.tvLately.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button)
            binding.itemViewTop.tvLately.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemViewTop.tvLately.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button_white)
            binding.itemViewTop.tvLately.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.main_bg, null
                )
            )
        }
        if (btnThree == 1) {
            binding.itemViewTop.tvPhoneBook.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button)
            binding.itemViewTop.tvPhoneBook.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemViewTop.tvPhoneBook.setBackgroundResource(vn.hitu.ntb.R.drawable.custom_button_white)
            binding.itemViewTop.tvPhoneBook.setTextColor(
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
            if (array.size == 1) {
                binding.itemBottomView.ibCreate.setBackgroundResource(vn.hitu.ntb.R.drawable.border_gray_50dp)
                binding.itemBottomView.ibCreate.isEnabled = false
            } else {
                binding.itemBottomView.ibCreate.setBackgroundResource(vn.hitu.ntb.R.drawable.border_blue_50dp)
                binding.itemBottomView.ibCreate.isEnabled = true
            }
        }

    }


}