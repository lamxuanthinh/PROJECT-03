package com.example.project03.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project03.data.Address
import com.example.project03.databinding.FragmentAddressBinding
import com.example.project03.util.Resource
import com.example.project03.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment:Fragment() {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel> ()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbarAddress.visibility=View.GONE
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.apply {
            bottomSave.setOnClickListener {
                val addresTitle = edAddressTitle.text.toString()
                val fullName=edFullName.text.toString()
                val street=edStreet.text.toString()
                val phone=edPhone.text.toString()
                val city=edCity.text.toString()
                val state=edState.text.toString()
                val address= Address(addresTitle,fullName,street,phone,city,state)
                viewModel.addAddress(address)
            }
        }
    }
}