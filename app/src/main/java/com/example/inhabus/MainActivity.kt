package com.example.inhabus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var welcomeText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        welcomeText = findViewById<TextView>(R.id.user_nickname)
        welcomeText.setText(intent.getStringExtra("nickname")+"님 안녕하세요 :)")
    }
}