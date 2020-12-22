package com.example.landmarkremark.interfaces

interface FBUserTaskOnCompleteListener {
    fun onSuccess()
    fun onError(err: Exception?)
}