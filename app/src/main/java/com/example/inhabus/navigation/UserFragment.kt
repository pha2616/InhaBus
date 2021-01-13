package com.example.inhabus.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.inhabus.R
import kotlinx.android.synthetic.main.fragment_user.*
import java.util.*

class UserFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        return view
    }

    override fun onStart() {
        super.onStart()
        user_nickname.text = arguments!!.getString("nickname")
    }

    override fun onResume() {
        super.onResume()
    }
}