package com.example.project03.fragments.categories
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project03.R
import com.example.project03.adapters.BestDealsAdapter
import com.example.project03.adapters.BestProductsAdapter
import com.example.project03.adapters.SpecialProductsAdapter
import com.example.project03.databinding.FragmentMainCategoryBinding
import com.example.project03.util.Resource
import com.example.project03.viewmodel.MainCategoryViewModel
import com.example.project03.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG="MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialProductsRv()
        setupBestProductsRv()
        setupBestDealsRv()
        specialProductsAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("product",it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)

        }
        bestProductsAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("product",it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)

        }
        bestDealsAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("product",it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)

        }

        lifecycleScope.launchWhenStarted {
            viewModel.specialProduct.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Success->{
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()

                    }
                    is Resource.Error->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter= BestDealsAdapter()
        binding.rvBestDealProducts.apply {
            layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=bestDealsAdapter
        }
    }

    private fun setupBestProductsRv() {
        bestProductsAdapter= BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager= GridLayoutManager(requireContext(),2,GridLayoutManager.HORIZONTAL,false)
            adapter=bestProductsAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility= View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility= View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter= SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=specialProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}