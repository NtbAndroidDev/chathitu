package vn.hitu.ntb.qr_code.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.squareup.picasso.Picasso

import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.techres.line.qr_code.R
import vn.techres.line.qr_code.databinding.FragmentMyQrBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 06/10/2022
 */
class MyQRFragment : AppFragment<HomeActivity>() {

    private lateinit var binding: FragmentMyQrBinding
    private var mAuth: FirebaseAuth? = null

    companion object {
        fun newInstance(): MyQRFragment {
            return MyQRFragment()
        }
    }

    override fun getLayoutView(): View {
        binding = FragmentMyQrBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()

    }


    override fun initData() {
        onClickMore()

        binding.imageButtonClose.setOnClickListener {
            binding.imageButtonClose.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_image_button_white)
            binding.imageButtonClose.setImageResource(R.drawable.ic_close_on)
            binding.imageButtonClose.setColorFilter(
                vn.hitu.ntb.R.color.common_text_color,
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            postDelayed({
                finish()
            }, 50)
        }

        getProfile()
    }




    private fun getProfile() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users").child(
            Auth.getAuth()
        )
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: UserData = dataSnapshot.getValue(UserData::class.java)!!

                //set info
                binding.tvName.text = user.name
                //img
                FirebaseStorage.getInstance().reference.child("images/" + user.image)
                    .downloadUrl
                    .addOnSuccessListener {
                        Picasso.get().load(it).into(binding.imvAvt)
                    }
                    .addOnFailureListener {

                    }
                binding.imvQRCode.setImageBitmap(create(user))
                binding.btnDownload.setOnClickListener {

                    saveQRCode(create(user)!!)
                }


            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }




    private fun onClickMore() {
        binding.imageButtonClose.setOnClickListener {
            try {
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

    }


    private fun create(user: UserData): Bitmap? {

        return try {
            val result: BitMatrix = QRCodeWriter().encode(
                String.format(
                    "%s:%s:%s:%s:CUSTOMER",
                    user.uid,
                    user.name,
                    user.email,
                    user.image
                ), BarcodeFormat.QR_CODE, 1024, 1024
            )
            val bitmap: Bitmap = Bitmap.createBitmap(
                result.width,
                result.height,
                Bitmap.Config.ARGB_8888
            )
            for (y in 0 until result.height) {
                for (x in 0 until result.width) {
                    if (result.get(x, y)) {
                        bitmap.setPixel(x, y, Color.BLACK)
                    }
                }
            }
            bitmap
        } catch (e: WriterException) {
            Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        }
    }


    /**
     * lưu mã qr
     */
    @SuppressLint("Recycle")
    private fun saveQRCode(bitmap: Bitmap) {


        val outputStream: FileOutputStream?
        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        path.mkdir()
        val fileName: String = System.currentTimeMillis().toString() + ".png"
        val file = File(path, fileName)
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            toast(getString(vn.hitu.ntb.R.string.downloading))
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

}