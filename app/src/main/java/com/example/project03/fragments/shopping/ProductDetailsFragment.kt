package com.example.project03.fragments.shopping

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.project03.R
import com.example.project03.adapters.ColorsAdapter
import com.example.project03.adapters.SizeAdapter
import com.example.project03.adapters.ViewPager2Images
import com.example.project03.data.CartProduct
import com.example.project03.databinding.FragmentProductDetailsBinding
import com.example.project03.util.Resource
import com.example.project03.util.hideBottomNavigationView
import com.example.project03.viewmodel.DetailsviewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment:Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var bingding: FragmentProductDetailsBinding
    private val viewPageAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizeAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsviewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        bingding = FragmentProductDetailsBinding.inflate(inflater)
        return bingding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product
        setupSizesRv()
        setupColorsRv()
        setupViewpager()
        bingding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }
        /*bingding.buttonAddToCart.setOnClickListener {
        findNavController().navigate(R.id.cartFragment)
       }*/

        sizesAdapter.onItemClick = {
            selectedSize = it
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }
        bingding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
            findNavController().navigate(R.id.cartFragment)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        bingding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        bingding.buttonAddToCart.revertAnimation()
                        bingding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error -> {
                        bingding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        bingding.apply {
            product.offerPercentage?.let {
                val remainingPricePercentage = 1f - it
                val priceAfterOffer = remainingPricePercentage * product.price
                tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                tvProductPrice.paintFlags = tvProductPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            }
            tvProductPrice.text = "$ ${product.price}"
            tvProductName.text = product.name
            if (product.offerPercentage == null) {
                tvNewPrice.visibility = View.INVISIBLE
                tvProductPrice.text = "$ ${product.price}"
                tvProductName.text = product.name
            }


            tvProductDescription.text = product.description
            if (product.colors.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }
            if (product.sizes.isNullOrEmpty()) {
                tvProductSize.visibility = View.INVISIBLE

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