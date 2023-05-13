package com.example.project03.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project03.data.Product
import com.example.project03.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AdminDataViewModel @Inject constructor(
    private var firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    //hiện trang admin
    private val _productsadshow = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val productsshow: StateFlow<Resource<List<Product>>> = _productsadshow

    init {
        fetchHotelShow()
    }


    fun fetchHotelShow() {
        viewModelScope.launch {
            _productsadshow.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("idadmin","${auth.currentUser!!.uid}")
            .get().addOnSuccessListener {
                val hotelshow = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _productsadshow.emit(Resource.Success(hotelshow))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _productsadshow.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}