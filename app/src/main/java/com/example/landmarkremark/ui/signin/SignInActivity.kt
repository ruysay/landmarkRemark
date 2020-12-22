package com.example.landmarkremark.ui.signin;

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.interfaces.FBUserTaskOnCompleteListener
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.ui.main.MainRepository
import com.example.landmarkremark.ui.signup.SignUpActivity
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.example.landmarkremark.utilities.Utils
import com.example.landmarkremark.widgets.LoadingDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        loadingDialog = LoadingDialog(this)
        setListeners()
        MainRepository.clear()
    }

    private fun setListeners() {
        sign_in_container.setOnClickListener {
            clearFocus()
        }

        sign_in_checkbox_txt.setOnClickListener {
            sign_in_checkbox.isChecked = !sign_in_checkbox.isChecked
        }

        sign_in_register.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
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

        loadingDialog.show(true)
        MainRepository.signInWithEmailAndPassword(sign_in_email.text.toString(), sign_in_password.text.toString(), this, object: FBUserTaskOnCompleteListener {
            override fun onSuccess() {
                SharedPreferenceUtils.setEmail(sign_in_email.text.toString())
                SharedPreferenceUtils.setPassword(sign_in_password.text.toString())
                SharedPreferenceUtils.setRememberMe(sign_in_checkbox.isChecked)
                postUserLogin(sign_in_email.text.toString(), sign_in_password.text.toString())
            }

            override fun onError(err: Exception?) {
                val error = getString(R.string.sign_in_fail, err.toString())
                Toast.makeText(this@SignInActivity, error, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun postUserLogin(email: String?, password: String?) {
        val accessToken = Utils.getAccessToken(email, password)

        MainRepository.getFirebaseUser()?.let {firebaseUser ->
            SharedPreferenceUtils.setUserId(firebaseUser.uid)

            if (firebaseUser.displayName == null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(SharedPreferenceUtils.getUserName())
                    .build()
                firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(this, OnCompleteListener { task ->

                })
            } else {
                SharedPreferenceUtils.setUserName(firebaseUser.displayName!!)
            }
        }

        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}