package com.example.project03.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project03.R
import com.example.project03.adapters.BestDealsAdapter
import com.example.project03.adapters.BestProductsAdapter
import com.example.project03.databinding.FragmentBaseCateroryBinding
import com.example.project03.util.showBottomNavigationView


open class BaseCategoryFragment: Fragment() {
    private lateinit var binding: FragmentBaseCateroryBinding
    protected val offerAdapter:BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter:BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBaseCateroryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProductsRv()
        bestProductsAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("product",it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)

        }
        offerAdapter.onClick={
            val b= Bundle().apply {
                putParcelable("product",it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)

        }
        binding.rvOfferProducts.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)&& dx !=0){
                    onOfferPagingRequest()

                }
            }
        } )

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height +scrollY){
                onBestProductPagingRequest()
            }
        })

    }
    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility=View.VISIBLE
    }
    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility=View.GONE
    }
    fun showBestLoading(){
        binding.bestProductsProgressBar.visibility=View.VISIBLE
    }
    fun hideBestLoading(){
        binding.bestProductsProgressBar.visibility=View.GONE
    }
    open fun onOfferPagingRequest(){

    }
    open fun onBestProductPagingRequest(){

    }
    private fun setupBestProductsRv() {

        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter

        }
    }

    private fun setupOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter

        }
    }
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}