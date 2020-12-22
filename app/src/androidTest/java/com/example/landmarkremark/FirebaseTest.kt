package com.example.landmarkremark

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import com.example.landmarkremark.interfaces.FBUserTaskOnCompleteListener
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.ui.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FirebaseTest: FBUserTaskOnCompleteListener {

    @Mock
    private lateinit var mainViewModel: MainViewModel

    private var signUpResult = UNDEF

    companion object {
        private const val SUCCESS = 1
        private const val FAILURE = -1
        private const val UNDEF = 0
    }

    override fun onSuccess() {
        signUpResult = SUCCESS
    }

    override fun onError(err: Exception?) {
        signUpResult = FAILURE
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel()
    }

    @Test
    fun signUpSuccess_test() {
        val email = "hello@bb.cc"
        val password = "123456"
//        Mockito.`when`(MainRepository.createUserWithEmailAndPassword(email, password, mockActivity, this))
//            .thenReturn(successTask)

        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            // do something with your activity instance
            mainViewModel.createUserWithEmailAndPassword(email, password, activity, this)

        }
        assert(signUpResult == SUCCESS)
    }
}