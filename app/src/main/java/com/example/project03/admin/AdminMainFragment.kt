package com.example.project03.admin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.project03.R
import com.example.project03.activities.LoginRegisterActivity
import com.example.project03.data.Admin
import com.example.project03.databinding.FragmentAdminMainBinding
import com.example.project03.dialog.setupBottomSheetDialog
import com.example.project03.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG="AdminMainFragment"

@AndroidEntryPoint
class AdminMainFragment:Fragment() {
    private lateinit var binding:FragmentAdminMainBinding
    private lateinit var showproductsAdapter: ShowProductsAdapter
    private val viewModel by viewModels<AdminDataViewModel>()
    private val viewsModel by viewModels<AdminViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                imageUri=it.data?.data
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAdminMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        showproductsAdapter= ShowProductsAdapter()
        binding.tvshowad.setOnClickListener {
           findNavController().navigate(R.id.action_adminMainFragment_to_addProducstFragment)
        }
        binding.rvshowproduct.apply {
            layoutManager= LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL,false)
            adapter= showproductsAdapter
        }
        showproductsAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("productadmin",it)
            }
            findNavController().navigate(R.id.action_adminMainFragment_to_updateDeleteAdminFragment,b)
        }

            lifecycleScope.launchWhenStarted {
            viewModel.productsshow.collectLatest {
                when(it){
                    is Resource.Loading->{
                    }
                    is Resource.Success->{
                        showproductsAdapter.differ.submitList(it.data)

                    }
                    is Resource.Error->{
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
            lifecycleScope.launchWhenStarted {
                viewsModel.adminup.collectLatest {
                    when(it){
                        is Resource.Loading ->{
                            showAdminLoading()
                        }
                        is Resource.Success ->{
                            hideAdminLoading()
                            showAdminInfomtion(it.data!!)
                        }
                        is Resource.Error ->{
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                        }
                        else -> Unit
                    }
                }
            }
            lifecycleScope.launchWhenStarted {
                viewsModel.updateAdminInfo.collectLatest {
                    when(it){
                        is Resource.Loading ->{
                            binding.buttonSave.startAnimation()
                        }
                        is Resource.Success ->{
                            binding.buttonSave.revertAnimation()
                            findNavController().navigateUp()
                        }
                        is Resource.Error ->{
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                        }
                        else -> Unit
                    }

                }
            }
            binding.tvUpdatePassword.setOnClickListener {
                setupBottomSheetDialog {  }
            }
            binding.imageCloseAdminAccount.setOnClickListener {
                viewsModel.logout()
                val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            binding.imgload1.setOnClickListener {
                findNavController().navigate(R.id.adminMainFragment)
            }
            binding.buttonSave.setOnClickListener {
                binding.apply {
                    val fistName= edFirstName.text.toString().trim()
                    val lastName= edLastName.text.toString().trim()
                    val email= edEmail.text.toString().trim()
                    val phone= edPhone.text.toString().trim()
                    val admin= Admin(fistName,lastName,email,phone,"admin")
                    viewsModel.updateAdmin(admin,imageUri)

                }


            }
            binding.imageEdit.setOnClickListener {
                val intent=Intent(Intent.ACTION_GET_CONTENT)
                intent.type="img/*"
                imageActivityResultLauncher.launch(intent)
            }
        }

        private fun showAdminInfomtion(data: Admin) {
            binding.apply {
                Glide.with(this@AdminMainFragment).load(data.imgPath).error(ColorDrawable(Color.BLACK)).into(imageUser)
                edFirstName.setText(data.firstName)
                edLastName.setText(data.lastName)
                edEmail.setText(data.email)
                edPhone.setText(data.phone)
            }
        }

        private fun hideAdminLoading() {
            binding.apply {
                progressbarAccount.visibility=View.GONE
                imageUser.visibility=View.VISIBLE
                imageEdit.visibility=View.VISIBLE
                edFirstName.visibility=View.VISIBLE
                edLastName.visibility=View.VISIBLE
                edPhone.visibility=View.VISIBLE
                edEmail.visibility=View.VISIBLE
                tvUpdatePassword.visibility=View.VISIBLE
                buttonSave.visibility=View.VISIBLE
            }
        }

        private fun showAdminLoading() {
            binding.apply {
                progressbarAccount.visibility=View.VISIBLE
                imageUser.visibility=View.INVISIBLE
                imageEdit.visibility=View.INVISIBLE
                edFirstName.visibility=View.INVISIBLE
                edLastName.visibility=View.INVISIBLE
                edPhone.visibility=View.INVISIBLE
                edEmail.visibility=View.INVISIBLE
                tvUpdatePassword.visibility=View.INVISIBLE
                buttonSave.visibility=View.INVISIBLE

            }
        }
        //

}