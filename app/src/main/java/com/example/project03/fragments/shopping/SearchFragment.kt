package com.example.project03.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project03.R
import com.example.project03.adapters.BestProductsAdapter
import com.example.project03.databinding.FragmentSearchBinding
import com.example.project03.util.Resource
import com.example.project03.util.showBottomNavigationView
import com.example.project03.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG="SearchFragment"
@AndroidEntryPoint
class SearchFragment:Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: BestProductsAdapter
    private val viewModel by viewModels<SearchViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showBottomNavigationView()
        binding= FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = BestProductsAdapter()
        setupSearchViewHotelRv()
        searchAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("product",it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment,b)
        }

        binding.imhome.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.edSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.searchProducts("${text.toString()}")
            binding.imhome.visibility=View.VISIBLE
            Log.d(TAG,text.toString())


        }
        lifecycleScope.launchWhenStarted {
            viewModel.productsSearch.collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.progressbarCategories.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {

                        if (result.data.isNullOrEmpty()){
                            binding.tvThongbao.visibility = View.VISIBLE
                            binding.imhome.visibility = View.VISIBLE
                            binding.progressbarCategories.visibility = View.GONE
                        } else {
                            searchAdapter.differ.submitList(result.data)
                            binding.progressbarCategories.visibility=View.GONE
                            binding.tvThongbao.visibility=View.GONE

                        }

                        binding.progressbarCategories.visibility=View.GONE
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT)
                            .show()
                        // Hide progress bar or loading indicator
                        binding.progressbarCategories.visibility = View.GONE
                        // Hide progress bar or loading indicator
                        if (result.message == "Không tìm thấy kết quả") {
                            // Show a message to inform the user that no results were found
                            binding.tvThongbao.visibility = View.VISIBLE
                        } else {
                            binding.tvThongbao.visibility = View.GONE
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun setupSearchViewHotelRv() {
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager= GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
        }
    }



}