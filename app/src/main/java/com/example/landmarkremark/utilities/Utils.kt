package com.example.landmarkremark.utilities

import android.app.Activity
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.view.inputmethod.InputMethodManager
import java.util.*
import java.util.regex.Pattern

class Utils {

    companion object {
        // Hide keyboard with activity
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // Hide keyboard with view
        fun hideKeyboard(view: View) {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }

        /**
         * Helper method to check if enter has been clicked
         */
        fun isEnterPressed(keyCode: Int, event: KeyEvent): Boolean {
            return event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
        }

        /**
         * Helper method to check email format
         */
        fun isEmailInvalid(email: String): Boolean {
            val str = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            val p = Pattern.compile(str)
            val m = p.matcher(email)
            return !m.matches()
        }

        /**
         * Helper method to check for valid password format
         */
        fun isPasswordInvalid(password: String): Boolean {
            var hasDecimal = false
            var hasAlphabet = false

            for (c in password) {
                if (Character.isDigit(c)) hasDecimal = true
                if (Character.isLetter(c)) hasAlphabet = true
            }

            return !(hasDecimal and hasAlphabet and password.matches("[a-zA-Z0-9!@#$%^&]+".toRegex()))
        }

        /**
         * Enable touches after loading
         */
        fun unblockTouches(view: View?) {
            view?.visibility = GONE
            view?.setOnClickListener(null)
        }

        /**
         * Used to show the current play time on playback hud
         */
        fun convertTimeMillisToMinute(time: Int): String {
            val seconds = time  % 60
            val minutes = (time - seconds)  / 60
            return if (seconds < 10) {
                "0$minutes:0$seconds"
            } else {
                "0$minutes:$seconds"
            }
        }

        /**
         * Hide Action and navigation bar
         */
        fun hideSystemUI(window: Window) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        fun randomStringGenerator(): String {
            val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
            val result = StringBuilder()
            val rnd = Random()
            while (result.length < 10) { // length of the random string.
                val index = (rnd.nextFloat() * characters.length).toInt()
                result.append(characters[index])
            }
            return result.toString()
        }

        fun animateFadeIn(view: View, speed: Long) {
            view.alpha = 0F
            view.animate().alpha(1F).setDuration(speed).start()
        }

        fun getAccessToken(email: String?, password: String? = null): String {
            return email?.reversed().hashCode().toString() + "-" +password
        }
    }
}
