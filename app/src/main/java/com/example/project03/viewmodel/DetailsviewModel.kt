package com.example.project03.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project03.data.CartProduct
import com.example.project03.firebase.FirebaseCommon
import com.example.project03.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsviewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {
    private val _addToCart=MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()
    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).
        collection("cart").whereEqualTo("product.id",cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if(it.isEmpty()){//thêm sản phẩm mới
                        addNewProduct(cartProduct)
                    }else{
                        val product=it.first().toObject(CartProduct::class.java)
                        if (product==cartProduct){//giam so luong
                            val documentId=it.first().id
                            increaseQuantity(documentId,cartProduct)
                        } else{//thêm sản phẩm mới
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }

    }
    fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){addedProduct,e->
            viewModelScope.launch {
                if(e==null){
                    _addToCart.emit(Resource.Success(addedProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
    fun increaseQuantity(documentId: String,cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){_,e->
            viewModelScope.launch {
                if(e==null){
                    _addToCart.emit(Resource.Success(cartProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}