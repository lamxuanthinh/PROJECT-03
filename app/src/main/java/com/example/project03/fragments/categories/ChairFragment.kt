package com.example.project03.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.project03.data.Category
import com.example.project03.util.Resource
import com.example.project03.viewmodel.CategoryViewModel
import com.example.project03.viewmodel.factory.BaseCategoryViewModelFactoryFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment: BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    val viewModel by viewModels<CategoryViewModel> {BaseCategoryViewModelFactoryFactory(firestore, Category.Chair)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showOfferLoading()
                    }
                    is Resource.Success->{
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG)
                            .show()
                        Log.d("test",it.message.toString())
                        hideOfferLoading()
                    }else ->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showBestLoading()

                    }
                    is Resource.Success->{
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestLoading()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG)
                            .show()
                        hideBestLoading()
                    }else ->Unit
                }
            }
        }
    }
}