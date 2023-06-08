package vn.hitu.ntb.chat.ui.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.constants.ChatConstants
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.ActivityChatMessageBinding
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.ChangeBackgroundAdapter
import vn.hitu.ntb.chat.ui.adapter.MessageAdapter
import vn.hitu.ntb.chat.ui.handle.FCMSend
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.eventbus.EventbusChangeBackground
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.GroupData
import vn.hitu.ntb.model.entity.Medias
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.getRandomString
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.invisible
import vn.hitu.ntb.utils.AppUtils.show
import vn.hitu.ntb.utils.KeyboardUtils
import vn.hitu.ntb.utils.PhotoPickerUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.sql.Timestamp
import java.util.*

class ChatActivity : AppActivity(), MessageAdapter.ChatHandle, MessageAdapter.OnAudioChatMessage,
    ChangeBackgroundAdapter.ChangeBackgroundListener
{
    lateinit var binding : ActivityChatMessageBinding
    private var messageAdapter: MessageAdapter? = null
    private var changeBackGroundAdapter: ChangeBackgroundAdapter? = null
    private var messageList: ArrayList<ChatMessage> = ArrayList()
    private var backgroundList: ArrayList<String> = ArrayList()
    private var mAuth: FirebaseAuth? = null
    var databaseReference: DatabaseReference? = null
    private val uploadTask: StorageTask<*>? = null
    private var fileUri: Uri? = null
    private var mStorage: StorageReference? = null
    private var group = GroupData()
    private var uidChat: String = ""
    private var didUserChat: String = ""
    private var progressDialog: ProgressDialog? = null
    private var i = 0
    private var mRecorder: MediaRecorder? = null
    private var mFileName: File? = null
    private var isFreeHand = 0
    private var medias: ArrayList<Medias> = ArrayList()
    private var widthDevice = 0
    private var heightDevice = 0
    private var downX = 0f
    private var movX = 0f
    private var typeReaction = 0
    private var downClickVoice: Long = 0
    private var micX = 0
    private var removeMicX = 0
    private var freeHandX = 0
    private var positionPlayer = -1
    private var currentAudioPlay = MediaPlayer()
    private var currentKeyUpload = ChatConstants.EMIT_UPLOAD
    private var pathBackground = ""
    private var background = ""


    override fun getLayoutView(): View {
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        return binding.root
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCallBack(event: EventbusChangeBackground) {
        if (event.key == 1) {
            //Setup header
            binding.itemBackground.llBackgroundView.show()
            ImmersionBar.setTitleBar(this, binding.itemBackground.llHeaderBackground)
            binding.llTitleBar.hide()
        }
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.llTitleBar)
//        ImmersionBar.setTitleBar(this, binding.itemBackground.llBackgroundView)
//        binding.itemBackground.llBackgroundView.show()
        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
        messageAdapter = MessageAdapter(this)
        messageAdapter!!.setHandleMessage(this)
        messageAdapter!!.setOnAudioChatMessage(this)
        binding.rcvListChat.setHasFixedSize(true)
        binding.rcvListChat.setItemViewCacheSize(20)
        messageAdapter!!.setData(messageList)
        val linearLayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        linearLayoutManager.stackFromEnd = true
        binding.rcvListChat.layoutManager = linearLayoutManager
        binding.rcvListChat.adapter = messageAdapter


        changeBackGroundAdapter = ChangeBackgroundAdapter(this)
        changeBackGroundAdapter!!.setChangeBackground(this)
        AppUtils.initRecyclerViewHorizontal(binding.itemBackground.rcvImage, changeBackGroundAdapter)
        changeBackGroundAdapter!!.setData(backgroundList)

        val bundleIntent = intent.extras
        if (bundleIntent != null){
            if (bundleIntent.containsKey(MessageChatConstants.DATA_GROUP)){
                group = Gson().fromJson(bundleIntent.getString(MessageChatConstants.DATA_GROUP), GroupData::class.java)
            }
            uidChat = bundleIntent.getString("uidChat")!!
        }
        //Lấy kích thước hiện tại của device
        val defaultDisplay =
            DisplayManagerCompat.getInstance(this).getDisplay(Display.DEFAULT_DISPLAY)
        val displayContext = createDisplayContext(defaultDisplay!!)
        widthDevice = displayContext.resources.displayMetrics.widthPixels
        heightDevice = displayContext.resources.displayMetrics.heightPixels
        //Set up bàn phím
        KeyboardUtils.setupKeyboardCreateReviewNewsFeed(
            this,
            binding.etInputMessage,
            binding.emojiPopupLayout,
        )
        //set kích thước view chọn file và mic
        val paramsMic = binding.lnMic.layoutParams
        val params = binding.extensions.layoutParams
        params.height = heightDevice / 3
        paramsMic.height = heightDevice / 3
        params.width = widthDevice
        paramsMic.width = widthDevice
        binding.extensions.layoutParams = params
        binding.lnMic.layoutParams = paramsMic

    }

    override fun initData() {

        //handle sent icon
        EmojiManager.install(GoogleEmojiProvider())
        val popup: EmojiPopup = EmojiPopup.Builder.fromRootView(binding.rlChatLayout).build(binding.etInputMessage)
        binding.btnSentEmoji.setOnClickListener {
            if (!popup.isShowing) {
                binding.btnSentEmoji.setImageResource(vn.hitu.ntb.R.drawable.ic_text)
            } else {
                binding.btnSentEmoji.setImageResource(vn.hitu.ntb.R.drawable.ic_emotion_happy_line)
            }
            popup.toggle()
        }

        initViewToolBar()

        getMessage(group.gid)
        getImage()

        binding.etInputMessage.setOnClickListener {
//            binding.ibMore.isSelected = false
            binding.btnMic.isSelected = false
//            binding.ibEmojiSticker.isSelected = false
            //*//
            binding.btnSentFile.isSelected = false
            binding.extensions.hide()
            binding.lnMic.hide()
            pauseRecording(0)//Ngưng record và xóa audio
            initAudio()
        }

        //handle sent message
        binding.btnSend.setOnClickListener {
            val message: String = binding.etInputMessage.text.toString()
            if (message.trim { it <= ' ' }.isNotEmpty()) {
                val date = Date()
                val timestamp = Timestamp(date.time)
                val currentTime = timestamp.toString()
                val chat = ChatMessage(
                    currentTime, message.trim { it <= ' ' }, mAuth!!.currentUser!!
                        .uid, "text", ""
                )
                sendMessage(chat, group.gid, "")
                binding.etInputMessage.setText("")
            }
        }


        //handle when click back
        binding.btnBackMain.setOnClickListener {
            finish()
        }


        binding.civGroupImg.setOnClickListener {
            var intent = Intent()
            val bundle = Bundle()
            bundle.putString("didUserChat", didUserChat)
            if (group.listUidMember.size == 2){
                intent = Intent(this@ChatActivity, Class.forName(ModuleClassConstants.INFO_CUSTOMER))
                bundle.putString(AppConstants.ID_USER,
                    if (group.listUidMember[0] == Auth.getAuth()) {
                    group.listUidMember[1]
                } else {
                    group.listUidMember[0]
                })

            }else{
                intent =  Intent(this@ChatActivity, DetailChatActivity::class.java)
                bundle.putString(MessageChatConstants.DATA_GROUP, Gson().toJson(group))

            }
            intent.putExtras(bundle)
            startActivity(intent)
        }

        //handle sent image
        binding.btnSentImage.setOnClickListener {
            currentKeyUpload = ChatConstants.EMIT_UPLOAD
            PhotoPickerUtils.showImagePickerChat(
                this,
            )
        }
        binding.btnSentFile.setOnClickListener {

            binding.btnSentFile.isSelected = !binding.btnSentFile.isSelected

            if (binding.extensions.isVisible){
                binding.extensions.hide()
            }else
                binding.extensions.show()

            if (binding.emojiPopupLayout.visibility == View.VISIBLE) {
                binding.emojiPopupLayout.hide()
            }

            if (binding.lnMic.visibility == View.VISIBLE) {
                binding.lnMic.hide()
            }

            hideKeyboard(binding.etInputMessage)

            pauseRecording(0)
            initAudio()

        }
        binding.llSendFile.setOnClickListener {
            if (checkPermissionsFile()) {
                openFileManager()
            } else {
                requestPermissionsReadFile()
            }
        }

        binding.btnMic.setOnClickListener {

            toolbarChatActionMic()
        }

        binding.btnCancel.setOnClickListener {
            binding.llChatOption.visibility = View.GONE
        }

        binding.itemBackground.ibCancel.setOnClickListener {
            pathBackground = ""
            AppUtils.loadBackground(binding.ivBackground, group.background)
            binding.llTitleBar.show()
            binding.itemBackground.llBackgroundView.hide()
        }

        binding.itemBackground.ibConfirm.setOnClickListener {
            if (pathBackground.isNotEmpty()){
                var bmp: Bitmap? = null
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(pathBackground))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val baos = ByteArrayOutputStream()

                //here you can choose quality factor in third parameter(ex. i choosen 25)

                bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)
                val fileInBytes = baos.toByteArray()
                uploadImageToFirebase(fileInBytes)
            }else{
                if (background.isNotEmpty()) {
                    DbReference.changeBackground(background, group.gid)
                    group.background = background
                }
                else
                    AppUtils.loadBackground(binding.ivBackground, group.background)
            }

            binding.llTitleBar.show()
            binding.itemBackground.llBackgroundView.hide()

        }

    }
    private fun checkPermissionsFile(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermissionsReadFile() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), 1
        )
    }
    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                vn.hitu.ntb.R.anim.right_in_activity,
                vn.hitu.ntb.R.anim.right_out_activity
            )
            ActivityCompat.startActivityForResult(
                this,
                Intent.createChooser(intent, getString(vn.hitu.ntb.R.string.choose_file)),
                PictureConfig.CHOOSE_REQUEST_FILE,
                options.toBundle()
            )
        } catch (e: Exception) {
//            toast(getString(vn.hitu.ntb.R.string.can_not_find_file_manager))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {

                if (AppUtils.checkMimeTypeVideo(PictureSelector.obtainMultipleResult(data)[0]!!.realPath)){
                    uploadVideo(Uri.parse(PictureSelector.obtainMultipleResult(data)[0]!!.path))
                }else{
                    pathBackground = PictureSelector.obtainMultipleResult(data)[0]!!.path
                    var bmp: Bitmap? = null
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(
                            PictureSelector.obtainMultipleResult(data)[0]!!.path))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val baos = ByteArrayOutputStream()

                    //here you can choose quality factor in third parameter(ex. i choosen 25)

                    bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)
                    val fileInBytes = baos.toByteArray()

                    if (currentKeyUpload != ChatConstants.CHANGE_BACKGROUND)
                        uploadImageToFirebase(fileInBytes)
                    else
                        AppUtils.loadBackground(binding.ivBackground, PictureSelector.obtainMultipleResult(data)[0]!!.cutPath)
                }
            }
            if (requestCode == PictureConfig.CHOOSE_REQUEST_FILE) {
                uploadFile(data!!.data!!)

            }
        }
    }


    private fun initViewToolBar(){
        //render UI cho thanh tool barr
        FirebaseDatabase.getInstance().getReference("Users").child(uidChat)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: UserData = snapshot.getValue(UserData::class.java)!!
                    didUserChat = user.did
                    //img
                    FirebaseStorage.getInstance().reference.child("images/" + group.imageId)
                        .downloadUrl.addOnSuccessListener {
                            Picasso.get().load(it).into(binding.civGroupImg)

                        }.addOnFailureListener { }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


//        AppUtils.loadAvatar(binding.civGroupImg, group.imageId)
//        AppUtils.loadBackground(binding.ivBackground, group.background)
        FirebaseStorage.getInstance().reference.child("images/" + group.background)
            .downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(binding.ivBackground)

            }.addOnFailureListener { }


        binding.tvGroupName.text = group.name
    }
    private fun getMessage(idGroup: String) {
        showDialog()
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatMessage").child(idGroup)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                i = 0
                messageList.clear()
                val bundleRev = intent.extras
                val id = bundleRev!!.getString("chatPos")
                var lop = true
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(ChatMessage::class.java)
                    if (id != null) {
                        if (chat!!.messageId == id) {
                            lop = false
                        }
                    }
                    if (lop) {
                        i++
                    }
                    messageList.add(chat!!)
                }
                messageAdapter!!.notifyDataSetChanged()
                binding.rcvListChat.scrollToPosition(messageList.size - 1)
                binding.rcvListChat.post {
//                    kohii!!.unlockManager(manager!!)
                }
                hideDialog()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
    private fun sendMessage(chat: ChatMessage, idGroup: String, nameFile : String) {
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
        val notification =  when (chat.typeMessage) {
            MessageChatConstants.IMAGE -> {
                refMessageGroup.setValue(MessageChatConstants.IMAGE)
                "sent a picture"
                // :(
            }
            MessageChatConstants.VIDEO -> {
                refMessageGroup.setValue(MessageChatConstants.VIDEO)
                "sent a video"
                // :(
            }
            MessageChatConstants.FILE -> {
                refMessageGroup.setValue(MessageChatConstants.FILE)
                "sent a file"
            }
            MessageChatConstants.AUDIO -> {
                refMessageGroup.setValue(MessageChatConstants.AUDIO)
                "sent a audio"
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
    private fun uploadImageToFirebase(fileInBytes: ByteArray): String {
        showDialog()
        val imageId = UUID.randomUUID().toString() + ".jpg"
        val imgRef = mStorage!!.child("images/$imageId")
        val uploadTask = imgRef.putBytes(fileInBytes)
        uploadTask.addOnFailureListener {
            Toast.makeText(
                this@ChatActivity,
                "Upload image failed!",
                Toast.LENGTH_SHORT
            ).show()
            showDialog()
        }.addOnSuccessListener {
            progressDialog!!.dismiss()
            val date = Date()
            val timestamp = Timestamp(date.time)
            val currentTime = timestamp.toString()
            val chat = ChatMessage(currentTime, imageId, Auth.getAuth(), MessageChatConstants.IMAGE, "")
            if (currentKeyUpload != ChatConstants.CHANGE_BACKGROUND)
                sendMessage(chat, group.gid, "")
            else {
                DbReference.changeBackground(imageId, group.gid)
                group.background = imageId
            }

            hideDialog()
        }
        return imageId
    }

    private fun uploadFile(fileURI: Uri) {
        showDialog()
        val filePath = UUID.randomUUID().toString() + "|" + getFileName(fileURI)
        val fileRef = mStorage!!.child("files/$filePath")
        fileRef.putFile(fileURI).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val date = Date()
                val timestamp = Timestamp(date.time)
                val currentTime = timestamp.toString()
                val chat = ChatMessage(
                    currentTime, filePath, mAuth!!.currentUser!!
                        .uid, MessageChatConstants.FILE, ""
                )
                sendMessage(chat, group.gid, getFileName(fileURI))
                hideDialog()
            }
        }
    }
    private fun uploadVideo(fileURI: Uri) {
        showDialog()
        val filePath = UUID.randomUUID().toString() + "|" + getFileName(fileURI)
        val fileRef = mStorage!!.child("videos/$filePath")
        fileRef.putFile(fileURI).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val date = Date()
                val timestamp = Timestamp(date.time)
                val currentTime = timestamp.toString()
                val chat = ChatMessage(
                    currentTime, filePath, mAuth!!.currentUser!!
                        .uid, MessageChatConstants.VIDEO, ""
                )
                sendMessage(chat, group.gid, getFileName(fileURI))
                hideDialog()
            }
        }
    }
    @SuppressLint("Range")
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
    @SuppressLint("ClickableViewAccessibility")
    private val onTouchMic = View.OnTouchListener { _, motionEvent ->
        freeHandX = widthDevice * 2 / 3 + widthDevice / 3 / 2
        removeMicX = widthDevice / 4
        micX = widthDevice / 2
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> motionEventActionDowAudio(motionEvent.x)
            MotionEvent.ACTION_UP -> motionEventActionUpAudio(motionEvent.x)
            MotionEvent.ACTION_MOVE -> motionEventActionMoveAudio(motionEvent.x)
        }
        true
    }
    private fun motionEventActionDowAudio(x: Float) {
        if (isFreeHand != 1) {
            downX = x
            downClickVoice = Date().time
            binding.removeAudio.show()
            binding.freeHand.show()
            binding.timeAudio.show()
            binding.moveLeft.show()
            binding.moveRight.show()
            startRecording()
            binding.removeAudio.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_del_normal
                )
            )
            binding.freeHand.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_fh_unlock
                )
            )
            binding.onMic.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_normal
                )
            )
            binding.titleAudio.text = getString(vn.hitu.ntb.R.string.title_recording)
        }
    }
    private fun motionEventActionUpAudio(x: Float) {
        movX = if (x < 100) {
            if (x < 0) {
                micX - downX + x
            } else {
                micX - (100 - x)
            }
        } else {
            micX + (x - 100)
        }
        if (movX < removeMicX + 5) {
            pauseRecording(0)//Ngưng record và xóa audio
            initAudio()
        } else if (movX > freeHandX - 5) {
            isFreeHand = 1
            binding.titleAudio.text = getString(vn.hitu.ntb.chat.R.string.title_recording_hand_free)
            binding.titleAudio.setTextColor(getColor(vn.hitu.ntb.R.color.primary_text))
            binding.onMic.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_recording_send_now
                )
            )
            binding.removeAudio.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_del_red
                )
            )
            binding.freeHand.invisible()
            binding.timeAudio.show()
            binding.removeAudio.show()
            binding.removeAudio.layoutParams.width =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
            binding.removeAudio.layoutParams.height =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
            binding.moveLeft.invisible()
            binding.moveRight.invisible()
            binding.removeAudio.setOnClickListener {
                pauseRecording(0)//Ngưng record và xóa audio
                initAudio()
            }

        } else {
            val upClickVoice = Date().time
            if ((upClickVoice - downClickVoice) / 1000 > 1) {
                pauseRecording(1)//Ngưng record và upload audio
            }
            initAudio()
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun motionEventActionMoveAudio(x: Float) {
        movX = if (x < 100) {
            if (x < 0) {
                micX - downX + x
            } else {
                micX - (100 - x)
            }
        } else {
            micX + (x - 100)
        }
        if (movX < removeMicX + 5) {
            binding.removeAudio.setImageDrawable(getDrawable(vn.hitu.ntb.chat.R.drawable.ic_voice_delete_big))
            binding.removeAudio.layoutParams.width =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_48).toInt()
            binding.removeAudio.layoutParams.height =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_48).toInt()
            binding.titleAudio.text = getString(vn.hitu.ntb.chat.R.string.release_to_cancel_recording)
            binding.titleAudio.setTextColor(getColor(vn.hitu.ntb.R.color.red))
            binding.timeAudio.invisible()
            binding.moveLeft.invisible()
            binding.onMic.setImageDrawable(getDrawable(vn.hitu.ntb.chat.R.drawable.ic_voice_delete))
        } else if (movX > freeHandX - 5) {
            binding.titleAudio.text = getString(vn.hitu.ntb.chat.R.string.title_recording_hand_free)
            binding.titleAudio.setTextColor(getColor(vn.hitu.ntb.R.color.bg_main))
            binding.timeAudio.invisible()
            binding.moveRight.invisible()
            binding.freeHand.layoutParams.width =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_48).toInt()
            binding.freeHand.layoutParams.height =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_48).toInt()
            binding.freeHand.setImageDrawable(getDrawable(vn.hitu.ntb.chat.R.drawable.ic_voice_fh_hold))
            binding.onMic.setImageDrawable(getDrawable(vn.hitu.ntb.chat.R.drawable.ic_voice_recording_hand_free))
        } else {
            binding.titleAudio.text = getString(vn.hitu.ntb.chat.R.string.title_recording)
            binding.titleAudio.setTextColor(getColor(vn.hitu.ntb.R.color.primary_text))
            binding.timeAudio.show()
            binding.moveRight.show()
            binding.moveLeft.show()
            binding.removeAudio.setImageDrawable(getDrawable(vn.hitu.ntb.chat.R.drawable.ic_voice_del_normal))
            binding.freeHand.setImageDrawable(getDrawable(vn.hitu.ntb.chat.R.drawable.ic_voice_fh_unlock))
            binding.onMic.setImageDrawable(getDrawable(vn.hitu.ntb.chat.R.drawable.ic_voice_normal))
            binding.removeAudio.layoutParams.width =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
            binding.removeAudio.layoutParams.height =
                resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
        }
    }

    private fun toolbarChatActionMic() {
        if (binding.lnMic.visibility == View.VISIBLE) {
            binding.btnMic.isSelected = false
            binding.lnMic.hide()
        } else {
            hideKeyboard(binding.etInputMessage)
//            binding.ibMore.isSelected = false
//            binding.ibEmojiSticker.isSelected = false
            binding.btnSentFile.isSelected = false
            binding.btnMic.isSelected = true
            binding.extensions.hide()
            promptSpeechInput()
        }
    }

    private fun checkPermissionsAudio(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun promptSpeechInput() {
        if (checkPermissionsAudio()) {
            binding.removeAudio.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_del_normal
                )
            )
            binding.freeHand.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_fh_unlock
                )
            )
            binding.onMic.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, vn.hitu.ntb.chat.R.drawable.ic_voice_normal
                )
            )
            binding.lnMic.show()
            val set = AnimatorSet()
            val set1 = AnimatorSet()
            set.playTogether(
                ObjectAnimator.ofFloat(binding.moveLeft, ChatConstants.ALPHA, 1f, 0f)
                    .setDuration(1000), ObjectAnimator.ofFloat(
                    binding.moveLeft,
                    ChatConstants.TRANSLATION_X,
                    binding.moveLeft.x,
                    binding.moveLeft.x - 400
                ).setDuration(1500)
            )
            set1.playTogether(
                ObjectAnimator.ofFloat(binding.moveRight, ChatConstants.ALPHA, 1f, 0f)
                    .setDuration(1000), ObjectAnimator.ofFloat(
                    binding.moveRight,
                    ChatConstants.TRANSLATION_X,
                    binding.moveRight.x,
                    binding.freeHand.x + 400
                ).setDuration(1500)
            )

            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    set.start()
                }
            })
            set.start()
            set1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    set1.start()
                }
            })
            set1.start()

            binding.onMic.setOnTouchListener(onTouchMic)

        } else {
            binding.btnMic.isSelected = false
            requestPermissions()
        }
    }
    private fun requestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun startRecording() {
        val contextWrapper = ContextWrapper(this)
        mFileName = File(
            contextWrapper.externalCacheDir, String.format(
                "%s%s%s", getRandomString(12), Date().time, "audio.m4a"
            )
        )
        mRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(applicationContext)
        } else {
            MediaRecorder()
        }
        // below method is used to set the audio
        // source which we are using a mic.
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)

        // below method is used to set
        // the output format of the audio.
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        // below method is used to set the
        // audio encoder for our recorded audio.
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        // below method is used to set the
        // output file location for our recorded audio
        mRecorder!!.setOutputFile(mFileName!!.absolutePath)
        try {
            // below method will prepare
            // our audio recorder class
            mRecorder!!.prepare()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
        // start method will start
        // the audio recording.
        val elapsedRealtime = SystemClock.elapsedRealtime()
        // Set the time that the count-up timer is in reference to.
        binding.timeAudio.base = elapsedRealtime
        binding.timeAudio.start()
        mRecorder!!.start()
    }

    private fun pauseRecording(type: Int) {
        // below method will stop
        // the audio recording.
        if (mRecorder != null) {
            binding.timeAudio.stop()
            mRecorder!!.stop()
            binding.titleAudio.setTextColor(getColor(vn.hitu.ntb.R.color.primary_text))

            if (type == 1) {
                //Upload file tại đây
                getAudioUpload(Uri.fromFile(mFileName!!))
            }
            // below method will release
            // the media recorder class.
            mRecorder!!.release()
            mRecorder = null
            isFreeHand = 0

        }
    }

    private fun getAudioUpload(fileURI: Uri) {
        showDialog()
        val filePath = UUID.randomUUID().toString() + "|" + getFileName(fileURI)
        val fileRef = mStorage!!.child("audios/$filePath")
        fileRef.putFile(fileURI).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val date = Date()
                val timestamp = Timestamp(date.time)
                val currentTime = timestamp.toString()
                val chat = ChatMessage(
                    currentTime, filePath, mAuth!!.currentUser!!
                        .uid, MessageChatConstants.AUDIO, ""
                )
                sendMessage(chat, group.gid, getFileName(fileURI))
                hideDialog()
            }
        }.addOnFailureListener{
            toast(it.message)
        }

    }

    private fun initAudio() {
        binding.removeAudio.invisible()
        binding.freeHand.invisible()
        binding.timeAudio.invisible()
        binding.removeAudio.setImageDrawable(
            AppCompatResources.getDrawable(
                this, vn.hitu.ntb.chat.R.drawable.ic_voice_del_normal
            )
        )
        binding.freeHand.setImageDrawable(
            AppCompatResources.getDrawable(
                this, vn.hitu.ntb.chat.R.drawable.ic_voice_fh_unlock
            )
        )
        binding.onMic.setImageDrawable(
            AppCompatResources.getDrawable(
                this, vn.hitu.ntb.chat.R.drawable.ic_voice_normal
            )
        )
        binding.freeHand.layoutParams.width =
            resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
        binding.freeHand.layoutParams.height =
            resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
        binding.removeAudio.layoutParams.width =
            resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
        binding.removeAudio.layoutParams.height =
            resources.getDimension(vn.hitu.ntb.R.dimen.dp_32).toInt()
        binding.titleAudio.text = getString(vn.hitu.ntb.R.string.title_audio)
        binding.moveLeft.invisible()
        binding.moveRight.invisible()
    }



    override fun onClickItem(item: ChatMessage) {

    }

    override fun onLongClickItem(item: ChatMessage) {
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatMessage").child(group.gid)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                i = 0
                val bundleRev = intent.extras
                val id = bundleRev!!.getString("chatPos")
                var lop = true
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(ChatMessage::class.java)
                    if (id != null) {
                        if (chat!!.messageId == id) {
                            lop = false
                        }
                    }
                    if (lop) {
                        i++
                    }
                }
                binding.llOptionsSent.visibility = View.GONE
                binding.llChatOption.visibility = View.VISIBLE
                binding.btnDeleteMessage.setOnClickListener {
                    snapshot.child(item.messageId).ref.removeValue()
                    val refGroups = FirebaseDatabase.getInstance().getReference("Groups").child(group.gid)
                    val refMessageGroup = refGroups.child("lastMessage")

                    if (messageList.indexOf(messageList.find { it.messageId == item.messageId }) == messageList.size - 1)
                        refMessageGroup.setValue(messageList[messageList.size - 2].message)
                    else
                        refMessageGroup.setValue(messageList[messageList.size - 1].message)


                    binding.llOptionsSent.visibility = View.VISIBLE
                    binding.llChatOption.visibility = View.GONE
                }
                binding.btnForwardingMessage.setOnClickListener {
                    val intent = Intent(
                        this@ChatActivity,
                        ForwardingMessageActivity::class.java
                    )
                    val bundle = Bundle()
                    bundle.putString("didUserChat", didUserChat)
                    bundle.putString("MESSAGE", Gson().toJson(item))
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                messageAdapter!!.notifyItemChanged(i)

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    override fun onResume() {
        super.onResume()
        AppUtils.loadAvatar(binding.civGroupImg, group.imageId)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {

            if (positionPlayer != -1) {
                messageList[positionPlayer].stop = true
                messageAdapter!!.notifyItemChanged(positionPlayer)
            }
            currentAudioPlay.release()
        } catch (ignored: java.lang.Exception) {
            ignored.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
//        binding.ibMore.isSelected = false
        binding.btnMic.isSelected = false
        binding.btnSentFile.isSelected = false
//        binding.ibEmojiSticker.isSelected = false
        pauseRecording(0)//Ngưng record và xóa audio
        initAudio()
        if (!binding.emojiPopupLayout.onBackPressed() && !binding.emojiPopupLayout.isKeyboardOpen && binding.extensions.visibility != View.VISIBLE && binding.lnMic.visibility != View.VISIBLE) {
            if (isTaskRoot) {
                startActivity(Intent(this, HomeActivity::class.java))
                onBackPressed()
            } else {
                super.onBackPressed()
            }
        } else if (binding.extensions.visibility == View.VISIBLE) {
            binding.extensions.hide()
        } else if (binding.lnMic.visibility == View.VISIBLE) {
            binding.lnMic.hide()
        } else if (binding.emojiPopupLayout.isKeyboardOpen) {
            super.onBackPressed()
        } else {
            hideKeyboard(binding.etInputMessage)
            binding.emojiPopupLayout.hide()
        }
    }

    override fun onRunning(position: Int, audioFilePath: String) {
        if (positionPlayer != -1 && positionPlayer != position) {
            currentAudioPlay.pause()
            messageList[positionPlayer].seekTo = currentAudioPlay.currentPosition
            messageList[positionPlayer].stop = true
            messageList[positionPlayer].play = false
            messageAdapter!!.notifyItemChanged(positionPlayer)
        }

        positionPlayer = position
        messageList[positionPlayer].play =
            !messageList[positionPlayer].play

        if (messageList[positionPlayer].play) {
            currentAudioPlay = MediaPlayer()
            currentAudioPlay.setDataSource(AppUtils.getLinkPhoto(audioFilePath, "audios"))
            currentAudioPlay.prepare()
            currentAudioPlay.seekTo(messageList[positionPlayer].seekTo)
            currentAudioPlay.start()
        } else {
            currentAudioPlay.pause()
            messageList[positionPlayer].seekTo = currentAudioPlay.currentPosition
            messageAdapter!!.notifyItemChanged(positionPlayer)
        }

        currentAudioPlay.setOnCompletionListener {
            if (positionPlayer != -1) {
                messageList[positionPlayer].play = false
                messageList[positionPlayer].seekTo = 0
                messageAdapter!!.notifyItemChanged(positionPlayer)
            }

            positionPlayer = -1
        }
    }


    private fun getImage(){
        val mDatabase = FirebaseDatabase.getInstance()
        val myRef = mDatabase.getReference("Image")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("image_data", Gson().toJson(snapshot.value))
                backgroundList.clear()
                backgroundList.add("")
                backgroundList.addAll(snapshot.value!! as Collection<String>)
                changeBackGroundAdapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        postDelayed({
            changeBackGroundAdapter!!.notifyDataSetChanged()
        }, 1000)

    }

    override fun clickChangeImageHandle(position: Int, image: String, background: String) {
        currentKeyUpload = ChatConstants.CHANGE_BACKGROUND
        if (position == 0){
            PhotoPickerUtils.showImagePickerChooseAvatar(this)
        }else{
            this.background = image
            AppUtils.loadBackground(binding.ivBackground, image)
        }

    }

    private fun changeBackGround(background: String){
        val mDatabase = FirebaseDatabase.getInstance()
        val myRef = mDatabase.getReference("Groups").child("background").setValue(background)

    }
}