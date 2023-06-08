package vn.hitu.ntb.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.cache.UserDataCache
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.databinding.FragmentAccountBinding
import vn.hitu.ntb.eventbus.ThemeEventBus
import vn.hitu.ntb.model.entity.DbReference
import vn.hitu.ntb.model.entity.ProfileCustomerNodeData
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.service.MyFirebaseMessagingService
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.ui.dialog.DialogAgree
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.PhotoPickerUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/09/2022
 * @Update_by
 */
class AccountFragment : AppFragment<HomeActivity>() {
    private lateinit var binding: FragmentAccountBinding
    private var editor: SharedPreferences.Editor? = null
    private var nighMode = false
    private var sharedPreferences: SharedPreferences? = null
    private var mAuth: FirebaseAuth? = null
    private var mStorage: StorageReference? = null
    private var imageUri: Uri? = null
    private var progressDialog: ProgressDialog? = null
    private var localMedia: LocalMedia? = null

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }

    }

    override fun getLayoutView(): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users").child(Auth.getAuth())
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: UserData = dataSnapshot.getValue(UserData::class.java)!!

                //set info
                binding.tvUserName.text = user.name
                binding.tvProfileName.text = user.name
                binding.tvProfileEmail.text = user.email

                UserDataCache.saveUser(UserData(user.uid, user.name, user.name))


                //img
                FirebaseStorage.getInstance().reference.child("images/" + user.image)
                    .downloadUrl.addOnSuccessListener {
                        Picasso.get().load(it).into(binding.civImage)

                    }.addOnFailureListener { }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        //config sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences!!.edit()
        nighMode = sharedPreferences!!.getBoolean("night", false) //light mode default false

        //UI
        progressDialog = ProgressDialog(context)
        val reRender = sharedPreferences!!.getBoolean("render", false)
        val showNotification = sharedPreferences!!.getBoolean("notification", true)
        val status = sharedPreferences!!.getBoolean("status", true)
        val isUsedBiometric = sharedPreferences!!.getBoolean("isUsedBiometric", false)


        //set expandlist
        binding.btnShowInfo.setOnClickListener {

            togglePersonInfo(binding.expandInfo.visibility == View.GONE)

        }

        binding.btnShowOptions.setOnClickListener {
            togglePersonOptions(binding.expandOptions.visibility == View.GONE)

        }

        if (reRender) {
            togglePersonOptions(true)
            editor!!.putBoolean("render", false)
            editor!!.apply()
        }

        binding.swBiometric.isChecked = isUsedBiometric

        if (nighMode) {
            binding.btnSwitchTheme.isChecked = true
        }
        if (showNotification) {
            binding.btnSwitchNotify.isChecked = true
        }
        if (status) {
            binding.btnSwitchStatus.isChecked = true
            FirebaseDatabase.getInstance().getReference("Users").child(Auth.getAuth())
                .child("isOnline").setValue(true)
        } else {
            binding.tvStatus.text = "Offline"
            binding.civOnlineCircle.borderColor = resources.getColor(R.color.yellow_circle)
        }

    }


    override fun initData() {


        //set darkmode
        binding.btnSwitchTheme.setOnCheckedChangeListener(null)
        binding.btnSwitchTheme.isChecked = sharedPreferences!!.getBoolean("night", false)
        if (sharedPreferences!!.getBoolean("night", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.btnSwitchTheme.setOnCheckedChangeListener { _, b ->
            if (b) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                AppApplication.applicationContext().setTheme(b)
                EventBus.getDefault().post(ThemeEventBus(true))
                editor!!.putBoolean("night", true)
                editor!!.putBoolean("render", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                AppApplication.applicationContext().setTheme(b)
                EventBus.getDefault().post(ThemeEventBus(false))
                editor!!.putBoolean("night", false)
                editor!!.putBoolean("render", true)
            }
            editor!!.commit()

        }

        binding.btnLogout.setOnClickListener {
            DialogAgree.Builder(getAttachActivity()!!, "Đăng xuất", "Bạn có chắc muốn đăng xuất")
                .onActionDone(object : DialogAgree.Builder.OnActionDone {
                    override fun onActionDone(isConfirm: Boolean) {
                        if (isConfirm) {
                            //btn logout
                            val userId: String = Auth.getAuth()
                            try {
                                val intent = Intent(getAttachActivity()!!, Class.forName(ModuleClassConstants.LOGIN_ACTIVITY))

                                DbReference.writeIsOnlineUserAndGroup(userId, false)
                                editor!!.putInt("IsLogin", 0)
                                editor!!.commit()
                                Auth.saveAuth("")
                                mAuth!!.signOut()
                                val serviceIntent = Intent(getApplication()!!, MyFirebaseMessagingService::class.java)
                                getApplication()!!.stopService(serviceIntent)
                                startActivity(intent)
                                finish()
                            } catch (e: ClassNotFoundException) {
                                e.printStackTrace()
                            }

                        }
                    }
                }).show()
        }


        //click notification
        binding.btnSwitchNotify.setOnClickListener(View.OnClickListener {
            if (sharedPreferences!!.getBoolean("notification", true)) {
                editor!!.putBoolean("notification", false)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(Auth.getAuth()).child("notification").setValue(false)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(Auth.getAuth()).child("did").get()
                    .addOnCompleteListener {
                        val did = it.result.value.toString() + "OFF"
                        FirebaseDatabase.getInstance().getReference("Users").child(
                            Auth.getAuth()
                        ).child("did").setValue(did)
                    }
            } else {
                editor!!.putBoolean("notification", true)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(Auth.getAuth()).child("notification").setValue(true)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(Auth.getAuth()).child("did").get()
                    .addOnCompleteListener {
                        val did = it.result.value.toString()
                        val validDid = did.substring(0, did.length - 3)
                        FirebaseDatabase.getInstance().getReference("Users").child(
                            Auth.getAuth()
                        ).child("did").setValue(validDid)
                    }
            }
            editor!!.commit()
        })


        //click status
        binding.btnSwitchStatus.setOnClickListener(View.OnClickListener {
            if (sharedPreferences!!.getBoolean("status", true)) {
                editor!!.putBoolean("status", false)
                binding.tvStatus.text = "Offline"
                binding.civOnlineCircle.borderColor =
                    resources.getColor(R.color.yellow_circle, null)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(Auth.getAuth()).child("isOnline").setValue(false)
            } else {
                editor!!.putBoolean("status", true)
                binding.tvStatus.text = "Active"
                binding.civOnlineCircle.borderColor = resources.getColor(R.color.green_circle, null)
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(Auth.getAuth()).child("isOnline").setValue(true)
            }
            editor!!.commit()
        })

        //click status
        binding.swBiometric.setOnClickListener {
            if (sharedPreferences!!.getBoolean("isUsedBiometric", false)) {
                editor!!.putBoolean("isUsedBiometric", false)
            } else {
                editor!!.putBoolean("isUsedBiometric", true)
            }
            editor!!.commit()
        }




        binding.civImage.setOnClickListener {
            Toast.makeText(activity, "Click avt!", Toast.LENGTH_SHORT).show()
            PhotoPickerUtils.showImagePickerChooseAvatar(
                getAttachActivity()!!, pickerImageIntent
            )
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
                AppUtils.loadImageUser(binding.civImage, localMedia!!.cutPath)
                var bmp: Bitmap? = null
                try {
                    bmp = MediaStore.Images.Media.getBitmap(
                        getAttachActivity()!!.contentResolver,
                        Uri.parse(localMedia!!.path)
                    )
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
                bmp = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
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
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            DbReference.writeImageUser(uid, imageId)
            binding.civImage.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    fileInBytes,
                    0,
                    fileInBytes.size
                )
            )
            hideDialog()
        }
        return imageId
    }


    private fun togglePersonInfo(visible: Boolean) {
        if (visible) {
            binding.expandInfo.visibility = View.VISIBLE
            binding.ivDropInfo.setImageResource(R.drawable.ic_arrow_up_s_line)
        } else {
            binding.expandInfo.visibility = View.GONE
            binding.ivDropInfo.setImageResource(R.drawable.ic_arrow_right_s_line)
        }
    }

    private fun togglePersonOptions(visible: Boolean) {
        if (visible) {
            binding.expandOptions.visibility = View.VISIBLE
            binding.iconDropOptions.setImageResource(R.drawable.ic_arrow_up_s_line)
        } else {
            binding.expandOptions.visibility = View.GONE
            binding.iconDropOptions.setImageResource(R.drawable.ic_arrow_right_s_line)
        }
    }


}

