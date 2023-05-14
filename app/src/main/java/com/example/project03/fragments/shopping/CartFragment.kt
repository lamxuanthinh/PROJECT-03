package com.example.project03.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project03.R
import com.example.project03.adapters.CartProductAdapter
import com.example.project03.databinding.FragmentCartBinding
import com.example.project03.firebase.FirebaseCommon
import com.example.project03.util.Resource
import com.example.project03.util.VerticalItemDecoration
import com.example.project03.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()


        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest {price ->
                price?.let {
                    totalPrice = it as Float
                    binding.tvTotalPrice.text = "$ $price"
                }
            }
        }
        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }
        cartAdapter.onProductClick = {
            val  b = Bundle().apply { putParcelable("product",it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,b)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.DECREASE)
        }

        binding.buttonCheckout.setOnClickListener {
                val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice, cartAdapter.differ.currentList.toTypedArray())
                findNavController().navigate(action)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel"){ dialog,_ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){ dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        }else{
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error ->{
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}