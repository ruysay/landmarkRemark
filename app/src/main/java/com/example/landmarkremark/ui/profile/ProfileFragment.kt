package com.example.landmarkremark.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.landmarkremark.R
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.ui.main.MainViewModel
import com.example.landmarkremark.ui.signin.SignInActivity
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mainViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as MainActivity
        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_name_txt.text = SharedPreferenceUtils.getUserName()
        profile_email_txt.text = SharedPreferenceUtils.getEmail()
        profile_notes_txt.text = mainViewModel.getMyLocations()?.size.toString()

        profile_sign_out.setOnClickListener {
            auth.signOut()
            SharedPreferenceUtils.clearAccessToken()
            SharedPreferenceUtils.clearAccount()

            val intent = Intent(it.context, SignInActivity::class.java)
            (it.context as MainActivity).startActivity(intent)
            (it.context as MainActivity).overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
        }
    }
}