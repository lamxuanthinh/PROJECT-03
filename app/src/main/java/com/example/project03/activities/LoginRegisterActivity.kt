package com.example.project03.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project03.R
import dagger.hilt.android.AndroidEntryPoint

//import com.example.project03.databinding.ActivityLoginRegisterBinding
//import dagger.hilt.android.AndroidEntryPoint


//private lateinit var binding: ActivityLoginRegisterBinding

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_login_register)

    }
}