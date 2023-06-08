package vn.hitu.ntb.login.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.login.R
import vn.hitu.ntb.login.databinding.ActivityResetPasswordBinding


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 27/09/2022
 */
class ResetPasswordActivity : AppActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private var mAuth: FirebaseAuth? = null


    override fun getLayoutView(): View {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.toolbar)

        mAuth = FirebaseAuth.getInstance()


    }





    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData() {

        binding.btnDone.setOnClickListener {
            val email: String = binding.editTextEmailResetPw.getText().toString()
            if (email.isEmpty()) {
                val errorIcon = ContextCompat.getDrawable(
                    applicationContext, vn.hitu.ntb.R.drawable.ic_error_warning_line
                )
                errorIcon!!.setBounds(0, 0, errorIcon.intrinsicWidth, errorIcon.intrinsicHeight)
                binding.editTextEmailResetPw.setError("Invalid email", errorIcon)
                binding.textViewErrEmail.text = "Invalid email"
            } else {
                binding.textViewErrEmail.text = ""
                forgetPass(email)
            }
        }
        binding.btnClose.setOnClickListener {
            finish()
        }
    }





    //Kiểm tra thông tin đăng ký
    @RequiresApi(Build.VERSION_CODES.O)
//    private fun checkRegistry(): Boolean {
//        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//        val date = LocalDate.parse(binding.txtBirthday.text.toString(), formatter)
//        if (localMedia.size == 0) {
//            AppUtils.setSnackBarError(
//                binding.root,
//                getString(R.string.avatar_is_empty)
//            )
//            checkAnimationValidate(binding.rltAvatar)
//            return false
//        }
//        if (date!! > LocalDate.now()) {
//            toast(R.string.check_valid_birthday_false)
//            checkAnimationValidate(binding.txtBirthday)
//            return false
//        }
//        if (!binding.rbFemale.isChecked && !binding.rbMale.isChecked) {
//            toast(R.string.check_valid_gender_false)
//            checkAnimationValidate(binding.rgGender)
//            return false
//        }
//        if (!binding.cbPolicy.isChecked) {
//            toast(R.string.check_valid_rules_false)
//            checkAnimationValidate(binding.cbPolicy)
//            return false
//        }
//        if (binding.edtPassword.text.toString() != binding.edtRetypePassword.text.toString()) {
//            AppUtils.setSnackBarError(
//                binding.root,
//                getString(R.string.check_valid_retype_password_false)
//            )
//            checkAnimationValidate(binding.edtPassword)
//            checkAnimationValidate(binding.edtRetypePassword)
//            return false
//        }
//
//        return true
//    }


    //Hiệu ứng khi nhập không đúng dữ liệu
    private fun checkAnimationValidate(view: View) {
        view.startAnimation(
            AnimationUtils.loadAnimation(
                getContext(), vn.hitu.ntb.R.anim.shake_anim
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun forgetPass(email: String) {
        mAuth!!.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "Check your Email", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                    finish()
                } else {
                    checkAnimationValidate(binding.editTextEmailResetPw)
                    Toast.makeText(this@ResetPasswordActivity, "Error" + task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
    }


}