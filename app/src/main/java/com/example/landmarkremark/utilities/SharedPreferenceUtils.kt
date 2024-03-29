package com.example.landmarkremark.utilities;

import android.annotation.SuppressLint
import android.content.Context
import com.example.landmarkremark.LandmarkRemarkApplication

class SharedPreferenceUtils {
    companion object {

        /**
         * Account Shared Preferences
         */
        private const val ACCOUNT = "account"
        private const val USER_ID = "user_id"
        private const val REMEMBER_ME = "remember_me"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val USER_NAME = "user_name"

        private val accountSharedPreferences = LandmarkRemarkApplication.getContext()
            .getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)

        fun clearAccount() {
            accountSharedPreferences.edit().clear().commit()
        }

        fun getUserId(): String? {
            return accountSharedPreferences.getString(USER_ID, null)
        }

        // Set Token in Main Thread because it is needed ASAP in SignInActivity
        @SuppressLint("ApplySharedPref")
        fun setUserId(token: String) {
            accountSharedPreferences.edit().putString(USER_ID, token).commit()
        }

        @SuppressLint("ApplySharedPref")
        fun clearAccessToken() {
            accountSharedPreferences.edit().remove(USER_ID).commit()
        }

        fun getRememberMe(): Boolean {
            return accountSharedPreferences.getBoolean(REMEMBER_ME, false)
        }

        fun setRememberMe(rememberMe: Boolean) {
            accountSharedPreferences.edit().putBoolean(REMEMBER_ME, rememberMe).apply()
        }

        fun getEmail(): String? {
            return accountSharedPreferences.getString(EMAIL, null)
        }

        fun setEmail(email: String) {
            accountSharedPreferences.edit().putString(EMAIL, email).apply()
        }

        fun getPassword(): String? {
            return accountSharedPreferences.getString(PASSWORD, null)
        }

        fun setPassword(password: String) {
            accountSharedPreferences.edit().putString(PASSWORD, password).apply()
        }

        fun getUserName(): String? {
            return accountSharedPreferences.getString(USER_NAME, null)
        }

        fun setUserName(password: String) {
            accountSharedPreferences.edit().putString(USER_NAME, password).apply()
        }
    }
}
