package vn.hitu.ntb.login.ui.activity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.login.R
import vn.hitu.ntb.login.databinding.ActivityLoginBinding
import vn.hitu.ntb.manager.ActivityManager
import vn.hitu.ntb.other.DoubleClickHelper
import vn.hitu.ntb.service.MyFirebaseMessagingService
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show
import java.util.concurrent.Executor

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 27/09/2022
 */
class LoginActivity : AppActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var password: String = ""
    private var usingBiometric: Boolean = false
    private var mAuth: FirebaseAuth? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    private fun initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences!!.edit()
    }

    override fun getLayoutView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        initPreferences()
        //
        mAuth = FirebaseAuth.getInstance()

        showBiometricAuthenticationDialog()

        if (sharedPreferences!!.getBoolean("isUsedBiometric", false))
            binding.btnFinger.show()
        else
            binding.btnFinger.hide()
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            .statusBarColor(vn.hitu.ntb.R.color.primary_background)
    }

    //Hiển thị hộp thoại xác thực vân tây
    private fun showBiometricAuthenticationDialog() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@LoginActivity, errString, Toast.LENGTH_SHORT).show()

//                    binding.edtPassword.requestFocus()
//                    showKeyboard(binding.edtPassword)
                }

                //Xác thực thành công
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
//                    onLogin(phone, password)

                    onLogin(
                        sharedPreferences!!.getString("EmailLogin", "")!!,
                        sharedPreferences!!.getString("PasswordLogin", "")!!
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@LoginActivity, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()

                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.biometric_title))
            .setSubtitle(resources.getString(R.string.biometric_subtitle))
            .setNegativeButtonText(resources.getString(R.string.biometric_negative_button_text))
            .setAllowedAuthenticators(BIOMETRIC_WEAK).build()
    }

    override fun initData() {

        val user = UserCache.getUser()
        usingBiometric = user.isUsedBiometric!!

        if (usingBiometric) {
            password = user.password
        }

        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.USING_BIOMETRIC)) {
                usingBiometric = bundleIntent.getBoolean(AppConstants.USING_BIOMETRIC)
            }
        }

//        checkShowButtonFingerprint()


        val savedData = sharedPreferences!!.getString("EmailLogin", "")
        if (savedData!!.isNotEmpty()) {
            binding.edtEmail.setText(savedData)
            binding.checkboxLogin.isChecked = true
        } else {
            binding.edtEmail.setText("")
            binding.checkboxLogin.isChecked = false
        }





        setOnClickListener(binding.btnFinger, binding.textViewForgotPw, binding.btnSignup, binding.btnSigIn)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onClick(view: View) {
        when (view) {

            binding.btnFinger -> {
                checkDeviceHasBiometric()
                return
            }
            binding.textViewForgotPw ->{
                val intent = Intent(
                    this@LoginActivity,
                    ResetPasswordActivity::class.java
                )
                startActivity(intent)
            }
            binding.btnSignup ->{
                val intent = Intent(
                    this@LoginActivity,
                    RegisterAccountActivity::class.java
                )
                startActivity(intent)
                finish()
            }
            binding.btnSigIn -> {
                if (checkValid())
                    onLogin(
                        binding.edtEmail.text.toString(),
                        binding.edtPassword.text.toString()
                    )
            }
        }
    }

    //Kiểm tra thông tin đăng nhập
    private fun checkValid(): Boolean {
        val email: String = binding.edtEmail.text.toString()
        val password: String = binding.edtPassword.text.toString()
        if (password.isEmpty() || password.length < 6) {
            val errorIcon = ContextCompat.getDrawable(
                applicationContext, vn.hitu.ntb.R.drawable.ic_error_warning_line
            )
            errorIcon!!.setBounds(0, 0, errorIcon.intrinsicWidth, errorIcon.intrinsicHeight)
            binding.edtPassword.setError(getString(R.string.invalid_password), errorIcon)
            binding.textViewErrPw.visibility = View.VISIBLE
            binding.textViewErrPw.text = getString(R.string.invalid_password)
            checkAnimationValidate(binding.edtPassword)
            return false
        } else {
            binding.textViewErrPw.visibility = View.GONE
            binding.textViewErrPw.text = ""
        }
        if (email.isEmpty()) {
            val errorIcon = ContextCompat.getDrawable(applicationContext, vn.hitu.ntb.R.drawable.ic_error_warning_line)
            errorIcon!!.setBounds(0, 0, errorIcon.intrinsicWidth, errorIcon.intrinsicHeight)
            binding.edtEmail.setError(getString(R.string.invalid_email), errorIcon)
            binding.textViewErrEmail.visibility = View.VISIBLE
            binding.textViewErrEmail.text = getString(R.string.invalid_email)
            checkAnimationValidate(binding.edtEmail)
            return false
        } else {
            binding.textViewErrEmail.visibility = View.GONE
            binding.textViewErrEmail.text = ""
        }
        return true
    }

    //Hiệu ứng khi nhập không đúng dữ liệu
    private fun checkAnimationValidate(view: View) {
        view.startAnimation(
            AnimationUtils.loadAnimation(
                getContext(), vn.hitu.ntb.R.anim.shake_anim
            )
        )
    }


    //Kiểm tra thiết bị có xác thực sinh trắc học
    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
//                WriteLog.d("MY_APP_TAG", "No biometric features available on this device.")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
//                WriteLog.d("MY_APP_TAG", "Biometric features are currently unavailable.")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG
                    )
                }

                startActivityForResult(enrollIntent, 100)
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {

            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {

            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {

            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            Toast.makeText(this@LoginActivity, vn.hitu.ntb.R.string.home_exit_hint, Toast.LENGTH_SHORT).show()
            return
        }
        // Di chuyển đến ngăn xếp nhiệm vụ trước đó để tránh các phản ứng bất lợi do trượt cạnh gây ra
        moveTaskToBack(false)
        postDelayed({
            // Thực hiện tối ưu hóa bộ nhớ và phá hủy tất cả các giao diện
            ActivityManager.getInstance().finishAllActivities()
        }, 300)
    }
    private fun onLogin(email : String, password : String) {
        if (binding.checkboxLogin.isChecked) {
            editor!!.putString("Email", email)
            editor!!.commit()
        } else {
            editor!!.putString("Email", "")
            editor!!.commit()
        }
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (sharedPreferences!!.getString("EmailLogin", "") == email) {
                        editor!!.putBoolean("isUsedBiometric", sharedPreferences!!.getBoolean("isUsedBiometric", false))
                    } else {
                        editor!!.putBoolean("isUsedBiometric", false)
                    }
                    editor!!.putString("EmailLogin", email)
                    editor!!.putString("PasswordLogin", password)
                    editor!!.putInt("IsLogin", 1)

                    Auth.saveAuth(mAuth!!.currentUser!!.uid)

                    editor!!.commit()
                    val user: FirebaseUser = mAuth!!.currentUser!!
                    val intent = Intent(
                        this@LoginActivity,
                        HomeActivity::class.java
                    )
                    val serviceIntent = Intent(this, MyFirebaseMessagingService::class.java)
                    startService(serviceIntent)
                    startActivity(intent)
                    Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    finish()
                } else
                    Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()


            }
    }

}