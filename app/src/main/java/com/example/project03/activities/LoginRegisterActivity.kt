package com.example.project03.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project03.databinding.ActivityLoginRegisterBinding


private lateinit var binding: ActivityLoginRegisterBinding

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}