package com.example.project03.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project03.databinding.ActivityLoginRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel


private lateinit var binding: ActivityLoginRegisterBinding
@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}