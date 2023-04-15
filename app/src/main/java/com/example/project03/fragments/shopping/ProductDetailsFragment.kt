package com.example.project03.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project03.R
import com.example.project03.activities.ShoppingActivity
import com.example.project03.adapters.ColorsAdapter
import com.example.project03.adapters.SizeAdapter
import com.example.project03.adapters.ViewPager2Images
import com.example.project03.databinding.FragmentProductDetailsBinding
import com.example.project03.util.hideBottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView


class ProductDetailsFragment:Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var bingding:FragmentProductDetailsBinding
    private val viewPageAdapter by lazy{ViewPager2Images()}
    private val sizesAdapter by lazy{ SizeAdapter() }
    private val colorsAdapter by lazy{ColorsAdapter()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        bingding=FragmentProductDetailsBinding.inflate(inflater)
        return bingding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val product = args.product
        setupSizesRv()
        setupColorsRv()
        setupViewpager()
        bingding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        bingding.apply {
            tvProductName.text=product.name
            tvProductPrice.text="$ ${product.price}"
            tvProductDescription.text=product.description
            if(product.colors.isNullOrEmpty()){
                tvProductColors.visibility=View.INVISIBLE

            }
            if(product.sizes.isNullOrEmpty()){
                tvProductSize.visibility=View.INVISIBLE

            }
        }
        viewPageAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
        }
        product.sizes?.let {
            sizesAdapter.differ.submitList(it)
        }
    }

    private fun setupViewpager() {
        bingding.apply {
            viewPagerProductImages.adapter=viewPageAdapter
        }
    }

    private fun setupColorsRv() {
        bingding.rvColors.apply{
            adapter= colorsAdapter
            layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setupSizesRv() {
        bingding.rvSizes.apply{
            adapter= sizesAdapter
            layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }


}