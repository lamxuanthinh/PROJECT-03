package com.example.project03.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project03.R
import com.example.project03.data.Admin
import com.example.project03.databinding.FragmentRegisterAdminBinding
import com.example.project03.util.RegisterValidation
import com.example.project03.util.Resource
import com.example.project03.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
private val TAG = "RegisterAdminFragment"
@AndroidEntryPoint

class RegisterAdminFragment : Fragment() {

    private lateinit var binding: FragmentRegisterAdminBinding

    // import viewModel
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterAdminBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerAdminFragment_to_loginFragment)
        }

        binding.apply {
            btnRegister.setOnClickListener {
                val admin = Admin(
                    edFirstName.text.toString().trim(),
                    edLastName.text.toString().trim(),
                    edEmail.text.toString().trim(),
                    edPhone.text.toString().trim(),
                    "admin",
                    "",
                )
                val password = edPassword.text.toString().trim()
                viewModel.createAdAccountWithEmailAndPassword(admin, password)


            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.adregister.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test ok", it.data.toString())
                        binding.btnRegister.revertAnimation()
                        Toast.makeText(context, "Register admin Successfully", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.loginFragment)

                    }
                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.btnRegister.revertAnimation()
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if(validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main) {
                        binding.edEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main) {
                        binding.edPassword.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }

    }
}