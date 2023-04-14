package com.example.project03.fragments.loginRegister

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.project03.R
import com.example.project03.databinding.FragmentAccountOptionsBinding
import com.example.project03.databinding.FragmentIntroductionBinding


class AccountOptions : Fragment() {
    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptions_to_loginFragment)
        }

        binding.buttonRegisterAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptions_to_registerFragment2)
        }
    }
}