package vn.hitu.ntb.chat.ui.activity

import android.os.Bundle
import android.view.View
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.chat.databinding.ActivityImageClickBinding

class ImageClickActivity : AppActivity() {
    lateinit var binding : ActivityImageClickBinding
    private var imgSrc: String = ""

    override fun getLayoutView(): View {
        binding = ActivityImageClickBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        
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
    override fun initData() {
        val bundleRev = intent.extras
        imgSrc = bundleRev!!.getString("imgSrc")!!
        FirebaseStorage.getInstance().reference.child("images/$imgSrc")
            .downloadUrl.addOnSuccessListener { uri -> Picasso.get().load(uri).into(binding.img) }
            .addOnFailureListener {
                // Handle any errors
            }
    }

}