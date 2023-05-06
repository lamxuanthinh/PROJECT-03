package com.example.project03.viewmodel


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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(
    private var firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _productsearch =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val productsSearch: StateFlow<Resource<List<Product>>> = _productsearch
    fun searchProducts(query:String) {
        viewModelScope.launch {
            _productsearch.value = Resource.Loading()

            val searchResults = mutableListOf<Product>()

            // Tìm kiếm theo tên hotel
            val nameResults = firestore.collection("Products")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()
            searchResults.addAll(nameResults.toObjects(Product::class.java))


            // Sắp xếp kết quả theo giá tăng dần
            searchResults.sortBy { it.price }

            _productsearch.value = Resource.Success(searchResults)
        }
    }

}