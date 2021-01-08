package com.example.inhabus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.inhabus.navigation.AlarmFragment
import com.example.inhabus.navigation.HomeFragment
import com.example.inhabus.navigation.GridFragment
import com.example.inhabus.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var welcomeText: TextView
    var bundle: Bundle = Bundle()

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_home ->{
                var homeFragment = HomeFragment()
                homeFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,homeFragment).commit()
                return true
            }
            R.id.action_search ->{
                var gridFragment = GridFragment()
                gridFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
                return true
            }
            R.id.action_alarm ->{
                var alarmFragment = AlarmFragment()
                alarmFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                return true
            }
            R.id.action_account ->{
                var userFragment = UserFragment()
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bundle.putString("nickname",intent.getStringExtra("nickname"))
        replaceFragment(HomeFragment())

        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_content, fragment)
        fragment.arguments = bundle
        fragmentTransaction.commit()
    }
}