package com.example.landmarkremark.ui.signin;

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.example.landmarkremark.utilities.Utils
import com.example.landmarkremark.widgets.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        loadingDialog = LoadingDialog(this)
        setListeners()

    }

    private fun setListeners() {
        sign_in_container.setOnClickListener {
            clearFocus()
        }

        sign_in_checkbox_txt.setOnClickListener {
            sign_in_checkbox.isChecked = !sign_in_checkbox.isChecked
        }

        sign_in_btn.setOnClickListener {
            signIn()
        }

        sign_in_password.setOnKeyListener { _, keyCode, event ->
            if (Utils.isEnterPressed(keyCode, event)) {
                signIn()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }
        }
    }

    private fun clearFocus() {
        Utils.hideKeyboard(this)
        sign_in_email.clearFocus()
        sign_in_password.clearFocus()
    }

    /**
     * Check email and password is in correct format before posting to server. It will then display
     * any known errors to the user and if successful then start StartIpActivity in a new stack.
     */
    private fun signIn() {
        // Hide keyboard and clear focus
        clearFocus()

        // Check if email is blank
        if (sign_in_email.text.isNullOrBlank()) {
            Snackbar.make(sign_in_container, R.string.user_email_blank, Snackbar.LENGTH_LONG)
                .show()
            return
        }

        // Check if email format is correct
        if (Utils.isEmailInvalid(sign_in_email.text.toString())) {
            Snackbar.make(
                sign_in_container,
                R.string.user_email_incorrect,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        // Check if password is blank
        if (sign_in_password.text.isNullOrBlank()) {
            Snackbar.make(
                sign_in_container,
                R.string.user_password_blank, Snackbar.LENGTH_LONG
            )
                .show()
            return
        }

        // Check if password length is correct
        if (sign_in_password.text.toString().length !in 6..32) {
            Snackbar.make(
                sign_in_container,
                R.string.user_password_length_limit,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        SharedPreferenceUtils.setEmail(sign_in_email.text.toString())
        SharedPreferenceUtils.setPassword(sign_in_password.text.toString())
        SharedPreferenceUtils.setRememberMe(sign_in_checkbox.isChecked)

        postUserLogin(sign_in_email.text.toString(), sign_in_password.text.toString())
    }

    private fun postUserLogin(email: String?, password: String?) {
        val accessToken = Utils.getAccessToken(email, password)
        SharedPreferenceUtils.setAccessToken(accessToken)
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}