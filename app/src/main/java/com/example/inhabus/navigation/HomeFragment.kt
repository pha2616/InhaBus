package com.example.inhabus.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.inhabus.R
import com.example.inhabus.ReservationFragment
import com.example.inhabus.TimetableFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    var bundle: Bundle = Bundle()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home,container,false)

        var timetable_btn: Button = view.findViewById(R.id.bus_timetable_btn)
        var reservation_btn: Button = view.findViewById(R.id.bus_reservation_btn)

        bundle.putString("nickname",arguments!!.getString("nickname"))

        timetable_btn.setOnClickListener {
            replaceFragment(TimetableFragment())
        }

        reservation_btn.setOnClickListener {
            replaceFragment(ReservationFragment())
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        user_nickname.setText(arguments!!.getString("nickname") + "님 안녕하세요:)")
    }

    override fun onResume() {
        super.onResume()
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.main_content, fragment)
        fragmentTransaction.addToBackStack(null)
        fragment.arguments = bundle
        fragmentTransaction.commit()
    }
}