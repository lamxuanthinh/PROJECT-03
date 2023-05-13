package com.example.project03.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project03.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)
    }
}