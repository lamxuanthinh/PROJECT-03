package com.example.project03.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.project03.data.Product
import com.example.project03.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private var firestore:FirebaseFirestore
): ViewModel() {
    private val _specialProducts= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct:StateFlow<Resource<List<Product>>> =_specialProducts
    private val _bestDealProducts= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealProducts:StateFlow<Resource<List<Product>>> =_bestDealProducts
    private val _bestProducts= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts:StateFlow<Resource<List<Product>>> =_bestProducts



    init {
        fetchspecialProduct()
    }
    fun fetchspecialProduct(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category","Special Products").get().addOnSuccessListener{
                val specialProductsList=result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }

    }
    fun fetchBestDealProduct(){
        viewModelScope.launch {
            _bestDealProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category","Best Deal ").get().addOnSuccessListener{
                val bestDealProductsList=result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Success(bestDealProductsList))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun fetchBestProduct(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category","Best Products").get().addOnSuccessListener{
                val bestProductsList=result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(bestProductsList))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}