package hitu.ntb.story.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import hitu.ntb.story.databinding.FragmentStoryBinding
import hitu.ntb.story.ui.activity.StoryDetailActivity
import hitu.ntb.story.ui.adapter.StoryAdapter
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.cache.UserDataCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.IsFriend
import vn.hitu.ntb.model.entity.Story
import vn.hitu.ntb.model.entity.StoryList
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.PhotoPickerUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.sql.Timestamp
import java.util.Date
import java.util.UUID


class StoryFragment :  AppFragment<HomeActivity>(), StoryAdapter.OnListener {
    private lateinit var binding: FragmentStoryBinding
    private var adapter : StoryAdapter? = null
    private var storyListList : ArrayList<StoryList> = ArrayList()
    private var mStorage: StorageReference? = null
    private var localMedia: LocalMedia? = null

    override fun getLayoutView(): View {
        binding = FragmentStoryBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun initView() {

        mStorage = FirebaseStorage.getInstance().reference



        adapter = StoryAdapter(getAttachActivity()!!)
        adapter!!.setOnListener(this)
        adapter!!.setData(storyListList)
        AppUtils.initRecyclerViewHorizontal(binding.rcvStory, adapter, 2)
    }

    override fun initData() {

        getStory()
//        storyList.add(Story())
//        adapter!!.notifyDataSetChanged()
    }

    private fun getStory() {
        val mDatabase = FirebaseDatabase.getInstance()
        mDatabase.getReference("Storys").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                storyListList.clear()
                storyListList.add(StoryList())
                adapter!!.notifyDataSetChanged()
                if (dataSnapshot.exists()){
                    for (data in dataSnapshot.children){
                        val storyList = StoryList()
                        if (data!!.key == UserDataCache.getUser().uid){
                            for (item in data.children){
                                val story = item.getValue(Story::class.java)
                                storyList.storyList.add(story!!)

                            }
                            storyListList.add(storyList)
                            adapter!!.notifyDataSetChanged()
                        }else{
                            val mFriend = FirebaseDatabase.getInstance()
                            mFriend.getReference("Friends").addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (friend in dataSnapshot.children){
                                            val f = friend.getValue(IsFriend::class.java)
                                            if (f!!.mId == Auth.getAuth() && f.yId == data.key){
                                                val yStoryList = StoryList()
                                                for (item in data.children){
                                                    val story = item.getValue(Story::class.java)
                                                    yStoryList.storyList.add(story!!)

                                                }
                                                storyListList.add(yStoryList)
                                                adapter!!.notifyDataSetChanged()
                                            }
                                        }

                                    }

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



    }


    private fun uploadImageToFirebase(fileInBytes: ByteArray): String {
        showDialog()
        val imageId = UUID.randomUUID().toString() + ".jpg"
        val imgRef = mStorage!!.child("images/$imageId")
        val uploadTask = imgRef.putBytes(fileInBytes)
        uploadTask.addOnFailureListener {
            toast("Upload image failed!")
        }.addOnSuccessListener {
            createStory(imageId, 0)
        }
        return imageId
    }
    private fun uploadVideo(fileURI: Uri) {
        showDialog()
        val filePath = UUID.randomUUID().toString() + "|" + getFileName(fileURI)
        val fileRef = mStorage!!.child("videos/$filePath")
        fileRef.putFile(fileURI).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createStory(filePath, 1)
                hideDialog()
            }
        }
    }



    override fun clickItem(position: Int) {

        if (position == 0)
            PhotoPickerUtils.showImagePickerChooseStory(getAttachActivity()!!, pickerImageIntent)
        else {
            val intent = Intent(getAttachActivity(), StoryDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putString(AppConstants.STORY_LIST, Gson().toJson(storyListList))
            bundle.putInt(AppConstants.POSITION_MEDIA, position - 1)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
    private var pickerImageIntent: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent = it.data!!


                localMedia = PictureSelector.obtainMultipleResult(data)[0]
                if (AppUtils.checkMimeTypeVideo(PictureSelector.obtainMultipleResult(data)[0].realPath)){
                    uploadVideo(Uri.parse(PictureSelector.obtainMultipleResult(data)[0]!!.path))
                }else{
                    var bmp: Bitmap? = null
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.parse(
                            PictureSelector.obtainMultipleResult(data)[0]!!.path))
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
        }

    @SuppressLint("Range", "Recycle")
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = getAttachActivity()!!.contentResolver.query(uri, null, null, null, null)
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

    private fun createStory(imageId : String, type : Int){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val date = Date()
        val timestamp = Timestamp(date.time)
        val currentTime = timestamp.toString()
        val key = DbReference.mDatabase!!.child("Storys").push().key
        DbReference.newStory(Story(key!!, UserDataCache.getUser().name, Auth.getAuth(), currentTime, UserDataCache.getUser().image, imageId, type))
        hideDialog()

    }
}