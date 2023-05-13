package com.example.hotelapplication.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotelapplication.R
import com.example.hotelapplication.adapter.HotelAdapter
import com.example.hotelapplication.databinding.FragmentSearchBinding
import com.example.hotelapplication.util.Resource
import com.example.hotelapplication.util.showBottomNavigationView
import com.example.hotelapplication.viewmodel.main.MainHotelViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val TAG="SearchFragment"
@AndroidEntryPoint
class SearchFragment:Fragment() {
    private lateinit var binding:FragmentSearchBinding
    private lateinit var searchAdapter: HotelAdapter
    private val viewModel by viewModels<MainHotelViewModel>()
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

        searchAdapter = HotelAdapter()
        setupSearchViewHotelRv()
        searchAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("hotel",it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_hotelDetailsFragment,b)
        }
        binding.edSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.searchHotel("${text.toString()}")
            lifecycleScope.launchWhenStarted {
                viewModel.hotelSearch.collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            binding.progressbarSearch.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {

                             if (result.data.isNullOrEmpty()){
                                 binding.tvCancel.visibility = View.VISIBLE
                                 binding.progressbarSearch.visibility = View.GONE
                             } else {
                                 searchAdapter.differ.submitList(result.data)
                                 binding.progressbarSearch.visibility=View.GONE
                                 binding.tvCancel.visibility=View.GONE

                             }

                             binding.progressbarSearch.visibility=View.GONE
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT)
                                .show()
                            // Hide progress bar or loading indicator
                            binding.progressbarSearch.visibility = View.GONE
                            // Hide progress bar or loading indicator
                            if (result.message == "Không tìm thấy kết quả") {
                                // Show a message to inform the user that no results were found
                                binding.tvCancel.visibility = View.VISIBLE
                            } else {
                                binding.tvCancel.visibility = View.GONE
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
    private fun setupSearchViewHotelRv() {
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }



}