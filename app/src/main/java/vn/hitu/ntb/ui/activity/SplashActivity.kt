package vn.hitu.ntb.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.databinding.SplashActivityBinding
import vn.hitu.ntb.service.MyFirebaseMessagingService


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/09/2022
 */
class SplashActivity : AppActivity() {

    private lateinit var binding: SplashActivityBinding
    var sharedPreferences: SharedPreferences? = null
    private var mAuth: FirebaseAuth? = null
    var editor: SharedPreferences.Editor? = null
    private fun initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences!!.edit()
    }

    override fun getLayoutView(): View {
        binding = SplashActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        initPreferences()
        //
        mAuth = FirebaseAuth.getInstance()
    }

    override fun initData() {


//        getConfig()
        goToHome()
    }


    //Di chuyển đến màn hình sau khi đăng nhập thành công
    private fun goToHome() {
        postDelayed({
            val email = sharedPreferences!!.getString("EmailLogin", "")
            val password = sharedPreferences!!.getString("PasswordLogin", "")
            if (sharedPreferences!!.getInt("IsLogin", -1) != 1) {
                try {
                    val intent = Intent(
                        this@SplashActivity, Class.forName(ModuleClassConstants.LOGIN_ACTIVITY)
                    )
                    startActivity(intent)
                    finish()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            } else {
//                mAuth!!.signInWithEmailAndPassword(email!!, password!!)
//                    .addOnCompleteListener(this) {
//                        if (it.isSuccessful) {
//                            val intent = Intent(
//                                this@SplashActivity,
//                                HomeActivity::class.java
//                            )
//                            startActivity(intent)
//                            finish()
//                        } else {
//                            try {
//                                val intent = Intent(
//                                    this@SplashActivity,
//                                    Class.forName(ModuleClassConstants.LOGIN_ACTIVITY)
//                                )
//                                startActivity(intent)
//                                finish()
//                            } catch (e: ClassNotFoundException) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()

            }
        }, 1500)
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            // Ẩn thanh trạng thái và thanh điều hướng
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }

    override fun initActivity() {
        // Nếu Hoạt động hiện tại không phải là Hoạt động đầu tiên trong ngăn xếp tác vụ
        if (!isTaskRoot) {
            val intent: Intent? = intent
            // Nếu Hoạt động hiện tại được bắt đầu từ biểu tượng màn hình
            if (((intent != null) && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && (Intent.ACTION_MAIN == intent.action))) {
                // Hủy Hoạt động hiện tại để tránh lặp lại việc khởi tạo mục nhập
                finish()
                return
            }
        }
        super.initActivity()
    }


}