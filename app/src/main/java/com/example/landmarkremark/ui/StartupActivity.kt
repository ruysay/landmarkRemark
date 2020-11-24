package com.example.landmarkremark.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.ui.main.MainRepository
import com.example.landmarkremark.ui.signin.SignInActivity
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class StartupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var isGoingToSignInActivity: Boolean = false
    private val nextPageTimer = object : CountDownTimer(2000, 2000) {
        override fun onFinish() {
            if (isGoingToSignInActivity) {
                // Go to SignInActivity
                val intent = Intent(this@StartupActivity, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }else {
                val intent = Intent(this@StartupActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user != null) {
            // User is signed in
            Timber.d("checkUser - uid: ${user.uid}")
            //load locations with user Id
            MainRepository.getLocations()
        } else {
            // No user is signed in
            isGoingToSignInActivity = SharedPreferenceUtils.getUserId().isNullOrBlank()

        }

        if(!SharedPreferenceUtils.getRememberMe()) {
            SharedPreferenceUtils.clearAccessToken()
            isGoingToSignInActivity = true
        }

        //Delay a little bit
        nextPageTimer.cancel()
        nextPageTimer.start()
    }
}