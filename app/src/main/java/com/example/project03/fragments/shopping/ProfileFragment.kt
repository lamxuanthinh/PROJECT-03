package com.example.project03.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.project03.BuildConfig
import com.example.project03.R
import com.example.project03.activities.LoginRegisterActivity
import com.example.project03.databinding.FragmentProfileBinding
import com.example.project03.util.Resource
import com.example.project03.util.showBottomNavigationView
import com.example.project03.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }
        binding.linearOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
        binding.linearBilling.setOnClickListener {
            val action=ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f,
                emptyArray()
            )
            findNavController().navigate(action)

        }
        binding.tvContact.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_contactFragment)
        }
        binding.linearOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.tvVersionCode.text = "Version ${BuildConfig.VERSION_CODE}"
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(
                            ColorDrawable(
                                Color.BLACK
                            )
                        ).into(binding.imgUser)
                        binding.tvUserName.text = "${it.data.firstName}"

                    }
                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}