package com.example.landmarkremark.widgets

import android.app.Activity
import android.app.Dialog
import android.os.CountDownTimer
import android.os.Handler
import com.example.landmarkremark.R

class LoadingDialog(activity: Activity?) {

    private val loadingDialog: Dialog = Dialog(activity!!)
    private val mClearDialogHandler =Handler()

    init {
        loadingDialog.setContentView(R.layout.dialog_loading)
    }

    val isShowing: Boolean
        get() = loadingDialog.isShowing

    fun show(isCancelable: Boolean) {
        loadingDialog.setCancelable(isCancelable)
        loadingDialog.show()
        dismissCountdownTimer.cancel()
    }

    fun show(isCancelable: Boolean, duration: Long) {
        show(isCancelable)
        mClearDialogHandler.postDelayed({
            dismissCountdownTimer.start()
        }, duration)
    }

    fun dismiss() {
        loadingDialog.dismiss()
    }

    private val dismissCountdownTimer = object : CountDownTimer(2000, 2000) {
        override fun onFinish() {
            loadingDialog.dismiss()
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }
}