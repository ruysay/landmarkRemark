package com.example.landmarkremark.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.landmarkremark.R
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.ui.main.MainViewModel
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as MainActivity
        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)
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

        profile_name_txt.text = SharedPreferenceUtils.getEmail()
        profile_notes_txt.text = mainViewModel.getMyLocations()?.size.toString()
    }
}