package vn.hitu.ntb.login.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.text.*
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.login.databinding.ActivityRegisterAccountBinding
import vn.hitu.ntb.model.entity.DbReference
import java.util.*


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 27/09/2022
 */
class RegisterAccountActivity : AppActivity() {

    private lateinit var binding: ActivityRegisterAccountBinding

    private var mAuth: FirebaseAuth? = null


    override fun getLayoutView(): View {
        binding = ActivityRegisterAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        mAuth = FirebaseAuth.getInstance()


    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View) {
        when (view) {
            //empty
            binding.btnSignup -> {
                if (checkRegistry())
                    createUserWithEmailPass()
            }
            binding.btnLogin -> {
                val intent = Intent(this@RegisterAccountActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun initData() {

        setOnClickListener(binding.btnSignup, binding.btnLogin)
    }


    //Kiểm tra thông tin đăng ký
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkRegistry(): Boolean {
        val email: String = binding.editTextEmailRegister.text.toString()
        val username: String = binding.editTextUserNameRegister.text.toString()
        val password: String = binding.editTextPasswordRegister.text.toString()
        val errorIcon = ContextCompat.getDrawable(
            applicationContext, vn.hitu.ntb.R.drawable.ic_error_warning_line
        )
        errorIcon!!.setBounds(0, 0, errorIcon.intrinsicWidth, errorIcon.intrinsicHeight)
        if (password.isEmpty() || password.length < 6) {
            binding.editTextUserNameRegister.setError("Invalid password", errorIcon)
            binding.textViewErrPw.visibility = View.VISIBLE
            binding.textViewErrPw.text = "Invalid password"
            return false
        } else {
            binding.textViewErrPw.visibility = View.GONE
            binding.textViewErrPw.text = ""
        }
        if (email.isEmpty()) {
            binding.editTextEmailRegister.setError("Invalid email", errorIcon)
            binding.textViewErrEmail.visibility = View.VISIBLE
            binding.textViewErrEmail.text = "Invalid email"
            return false
        } else {
            binding.textViewErrEmail.visibility = View.GONE
            binding.textViewErrEmail.text = ""
        }
        if (username.isEmpty()) {
            binding.editTextUserNameRegister.setError("Invalid username", errorIcon)
            binding.textViewErrUsername.visibility = View.VISIBLE
            binding.textViewErrUsername.text = "Invalid username"
            return false
        } else {
            binding.textViewErrUsername.visibility = View.GONE
            binding.textViewErrUsername.text = ""
        }
        return true
    }


    //Hiệu ứng khi nhập không đúng dữ liệu
    private fun checkAnimationValidate(view: View) {
        view.startAnimation(
            AnimationUtils.loadAnimation(
                getContext(),
                vn.hitu.ntb.R.anim.shake_anim
            )
        )
    }


    private fun createUserWithEmailPass() {
        val email: String = binding.editTextEmailRegister.text.toString()
        val username: String = binding.editTextUserNameRegister.text.toString()
        val password: String = binding.editTextPasswordRegister.text.toString()
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            toast("Thong tin dang ky khong hop le")
        } else {
            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegisterAccountActivity
                ) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth!!.currentUser
                        DbReference.writeNewUser(
                            Auth.getAuth(),
                            binding.editTextEmailRegister.text.toString(),
                            binding.editTextUserNameRegister.text.toString(),
                            "avtdefault.jpg",
                            true,
                            ""
                        )
                        toast("Register successfully")
                        val intent = Intent(
                            this@RegisterAccountActivity,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    } else {
                        toast("Registration Error: " + task.exception!!.message)
                    }
                }
        }
    }


}