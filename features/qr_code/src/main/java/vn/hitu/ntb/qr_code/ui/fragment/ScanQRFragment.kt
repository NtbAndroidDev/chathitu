package vn.hitu.ntb.qr_code.ui.fragment


import android.Manifest
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.core.BarcodeType
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.interfaces.PermissionResultListener
import vn.hitu.ntb.interfaces.RequestPermissionListener
import vn.hitu.ntb.model.entity.*
import vn.hitu.ntb.other.MultiplePermission.handleOnRequestPermissionResult
import vn.hitu.ntb.other.MultiplePermission.requestPermissions
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import vn.techres.line.qr_code.R
import vn.techres.line.qr_code.databinding.FragmentScanQrBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 06/10/2022
 */

class ScanQRFragment : AppFragment<HomeActivity>(),
    QRCodeView.Delegate {

    private lateinit var binding: FragmentScanQrBinding
    private var cameraId = 0
    private var btnFlash = true
    private var btnChange = true
    private var mAuth: FirebaseAuth? = null
    private val requiredPermissionCamera = arrayOf(
        Manifest.permission.CAMERA
    )
    private val requiredPermissionCameraCode = 1002


    companion object {
        fun newInstance(): ScanQRFragment {
            return ScanQRFragment()
        }
    }




    override fun getLayoutView(): View {
        binding = FragmentScanQrBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        cameraCheckPermission()

    }


    override fun onResume() {
        super.onResume()
        requestMultiplePermissionCamera()
//        BGAQRCodeUtil.setDebug(true)
//        binding.zBarView.changeToScanQRCodeStyle()
//        binding.zBarView.setType(BarcodeType.ONLY_QR_CODE, null)
//        binding.zBarView.startSpotAndShowRect()
//        binding.zBarView.startCamera()
//        binding.zBarView.setDelegate(this)
    }

    override fun initData() {
        binding.imageButtonClose.setOnClickListener {
            binding.imageButtonClose.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button_white)
            binding.imageButtonClose.setImageResource(R.drawable.ic_close_on)
            binding.imageButtonClose.setColorFilter(vn.hitu.ntb.R.color.common_text_color, android.graphics.PorterDuff.Mode.MULTIPLY)

            postDelayed({
                finish()
            }, 50)
        }



        /**
         * bật flash cho camera ;333
         */
        binding.btnFlash.setOnClickListener {
            btnFlash = !btnFlash
            if (!btnFlash){
                binding.btnFlash.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button_white)
                binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
                binding.zBarView.openFlashlight()
            }else{
                binding.btnFlash.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button)
                binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
                binding.zBarView.closeFlashlight()
            }

        }

        /**
         * xoay camera
         */
        binding.btnChangeCamera.setOnClickListener {
            btnChange = !btnChange
            if (!btnChange){
                binding.btnChangeCamera.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button_white)
                binding.btnChangeCamera.setImageResource(R.drawable.ic_camera_on)
                cameraId = 1


                binding.btnFlash.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button)
                binding.btnFlash.setImageResource(R.drawable.ic_flash_enable)
                btnFlash = true
                binding.btnFlash.isEnabled = false

            }else{
                binding.btnChangeCamera.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button)
                binding.btnChangeCamera.setImageResource(R.drawable.ic_camera_off)
                cameraId = 0

                binding.btnFlash.isEnabled = true
                binding.btnFlash.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button)
                binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
            }
            binding.zBarView.stopCamera()
            binding.zBarView.startCamera(cameraId)
            binding.zBarView.startSpotAndShowRect()
        }



    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (PictureSelector.obtainMultipleResult(data).isNotEmpty()) {
                Toast.makeText(
                    context,
                    PictureSelector.obtainMultipleResult(data)[0].realPath,
                    Toast.LENGTH_SHORT
                ).show()
                binding.zBarView.decodeQRCode(PictureSelector.obtainMultipleResult(data)[0].path)
            }
        }

    }

    private fun requestMultiplePermissionCamera() {
        requestPermissions(
            requiredPermissionCamera,
            requiredPermissionCameraCode,
            object : RequestPermissionListener {
                override fun onCallPermissionFirst(
                    namePermission: String,
                    requestPermission: () -> Unit
                ) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    namePermission: String,
                    requestPermission: () -> Unit
                ) {
                }

                override fun onPermissionPermanentlyDenied(
                    namePermission: String,
                    openAppSetting: () -> Unit
                ) {

                }

                override fun onPermissionGranted() {
                    binding.zBarView.stopCamera()
                    binding.zBarView.startCamera(cameraId)
                    binding.zBarView.startSpotAndShowRect()
                }

            })

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handleOnRequestPermissionResult(
            requiredPermissionCameraCode,
            requestCode,
            permissions,
            grantResults,
            object : PermissionResultListener {
                override fun onPermissionRationaleShouldBeShown(
                    namePermission: String,
                    requestPermission: () -> Unit
                ) {

                }

                override fun onPermissionPermanentlyDenied(
                    namePermission: String,
                    openAppSetting: () -> Unit
                ) {

                }

                override fun onPermissionGranted() {
                    binding.zBarView.stopCamera()
                    binding.zBarView.startCamera(cameraId)
                    binding.zBarView.startSpotAndShowRect()
                }
            }
        )
    }

    /**
     * nếu quét thành công
     */
    override fun onScanQRCodeSuccess(result: String?) {
        vibrate()
        if (result != null) {
            binding.zBarView.stopCamera()


            if (result.split(":").last() == "CUSTOMER")
                goToProfile(result.split(":")[0], result.split(":")[1], result.split(":")[2])
            else if (result.split(":").last() == "QR_GROUP") {
                DbReference.addUserGroup(Auth.getAuth(), result.split(":")[0])
                AppUtils.sendMessage("${result.split(":")[1]} đã tham gia nhóm", result.split(":").first())
                val group = GroupData()
                val gson = Gson()
                val listType = object : TypeToken<ArrayList<String>>() {}.type
                val arrayList: ArrayList<String> = gson.fromJson(result.split(":")[3], listType)
                with(group){
                    gid = result.split(":").first()
                    name = result.split(":")[1]
                    imageId = result.split(":")[2]
                    listUidMember = arrayList
                }
                val uidChat: String = if (Auth.getAuth() == arrayList[0])
                    arrayList[1]
                else
                    arrayList[0]

                val intent = Intent(context, Class.forName(ModuleClassConstants.CHAT_ACTIVITY))
                val bundle = Bundle()
                bundle.putString("uidChat", uidChat)
                bundle.putString("DATA_GROUP", Gson().toJson(group))
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            }
            else
                Toast.makeText(getApplication()!!, "Mã QR không hợp lệ", Toast.LENGTH_SHORT).show()

            postDelayed({
                requestMultiplePermissionCamera()
                BGAQRCodeUtil.setDebug(true)
                binding.zBarView.changeToScanQRCodeStyle()
                binding.zBarView.setType(BarcodeType.ONLY_QR_CODE, null)
                binding.zBarView.startSpotAndShowRect()
                binding.zBarView.startCamera()
                binding.zBarView.setDelegate(this@ScanQRFragment)
            }, 2000)


        } else {
            binding.zBarView.stopCamera()
            binding.zBarView.startCamera(cameraId)
            binding.zBarView.startSpotAndShowRect()
            postDelayed({
                binding.zBarView.startCamera()
            }, 2000)
        }
    }


    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    override fun onScanQRCodeOpenCameraError() {
        requestMultiplePermissionCamera()
        BGAQRCodeUtil.setDebug(true)
        binding.zBarView.changeToScanQRCodeStyle()
        binding.zBarView.setType(BarcodeType.ONLY_QR_CODE, null)
        binding.zBarView.startSpotAndShowRect()
        binding.zBarView.startCamera()
        binding.zBarView.setDelegate(this)
    }

    private fun vibrate() {
        val vibrator = activity?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(1000)
    }

    private fun cameraCheckPermission() {

        val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                requestMultiplePermissionCamera()
                BGAQRCodeUtil.setDebug(true)
                binding.zBarView.changeToScanQRCodeStyle()
                binding.zBarView.setType(BarcodeType.ONLY_QR_CODE, null)
                binding.zBarView.startSpotAndShowRect()
                binding.zBarView.startCamera()
                binding.zBarView.setDelegate(this)
            } else {
                Toast.makeText(getApplication()!!, getString(R.string.permission_camera), Toast.LENGTH_SHORT).show()



                postDelayed({
//                    val intent: Intent = Intent(
//                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                        Uri.fromParts("package", getPackageName(), null)
//                    )
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
                    finish()
                }, 1000)
            }
        }
        requestCamera.launch(Manifest.permission.CAMERA)

    }

    private fun goToChat(uid : String, name : String, avt : String){
        val listUidMember = ArrayList<String>()
        listUidMember.add(Auth.getAuth())
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
                    getAttachActivity()!!,
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
                finish()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun goToProfile(uid : String, name : String, avt : String){
        val intent = Intent(getAttachActivity()!!, Class.forName(ModuleClassConstants.INFO_CUSTOMER))
        val bundle = Bundle()
        DbReference.acceptFriend(UserData(uid, name, avt), getAttachActivity()!!)
        bundle.putString(AppConstants.ID_USER, uid)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}