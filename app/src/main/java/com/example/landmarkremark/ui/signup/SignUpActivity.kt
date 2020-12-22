package com.example.landmarkremark.ui.signup

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.interfaces.FBUserTaskOnCompleteListener
import com.example.landmarkremark.ui.main.MainRepository
import com.example.landmarkremark.ui.signin.SignInActivity
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.example.landmarkremark.utilities.Utils
import com.example.landmarkremark.widgets.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog

    private val nextPageTimer = object : CountDownTimer(1500, 1500) {
        override fun onFinish() {
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        loadingDialog = LoadingDialog(this)
        setListeners()
    }

    private fun setListeners() {
        sign_up_container.setOnClickListener {
            clearFocus()
        }

        sign_up_btn.setOnClickListener {
            signUp()
        }

        sign_up_password.setOnKeyListener { _, keyCode, event ->
            if (Utils.isEnterPressed(keyCode, event)) {
                signUp()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }
        }
    }

    private fun clearFocus() {
        Utils.hideKeyboard(this)
        sign_up_email.clearFocus()
        sign_up_password.clearFocus()
    }

    private fun signUp() {
        // Hide keyboard and clear focus
        clearFocus()

        // Check if email is blank
        if (sign_up_email.text.isNullOrBlank()) {
            Snackbar.make(sign_up_container, R.string.user_email_blank, Snackbar.LENGTH_LONG)
                .show()
            return
        }

        // Check if email format is correct
        if (Utils.isEmailInvalid(sign_up_email.text.toString())) {
            Snackbar.make(
                sign_up_container,
                R.string.user_email_incorrect,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        // Check if password is blank
        if (sign_up_password.text.isNullOrBlank()) {
            Snackbar.make(
                sign_up_container,
                R.string.user_password_blank, Snackbar.LENGTH_LONG
            )
                .show()
            return
        }

        // Check if password length is correct
        if (sign_up_password.text.toString().length !in 6..32) {
            Snackbar.make(
                sign_up_container,
                R.string.user_password_length_limit,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }


        // Check if user name is blank
        if (sign_up_user_name.text.isNullOrBlank()) {
            Snackbar.make(
                sign_up_container,
                R.string.user_name_blank, Snackbar.LENGTH_LONG
            ).show()
            return
        }

        loadingDialog.show(true)

        MainRepository.createUserWithEmailAndPassword(sign_in_email.text.toString(), sign_in_password.text.toString(), this, object:
            FBUserTaskOnCompleteListener {
                override fun onSuccess() {
                    SharedPreferenceUtils.setEmail(sign_up_email.text.toString())
                    SharedPreferenceUtils.setPassword(sign_up_password.text.toString())
                    SharedPreferenceUtils.setUserName(sign_up_user_name.text.toString())

                    SharedPreferenceUtils.setRememberMe(true)
                    Toast.makeText(this@SignUpActivity, getString(R.string.sign_up_success), Toast.LENGTH_LONG).show()
                    nextPageTimer.cancel()
                    nextPageTimer.start()
                }

                override fun onError(err: Exception?) {
                    val error = getString(R.string.sign_up_fail, err.toString())
                    Toast.makeText(this@SignUpActivity, error, Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}