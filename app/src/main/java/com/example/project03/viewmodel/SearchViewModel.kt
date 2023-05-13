package com.example.hotelapplication.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapplication.data.Booking
import com.example.hotelapplication.data.Hotel
import com.example.hotelapplication.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class MainHotelViewModel @Inject constructor(
    private var firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    // hiện trang user
    private val _hotelDetails =
        MutableStateFlow<Resource<List<Hotel>>>(Resource.Unspecified())
    val hotelDetail: StateFlow<Resource<List<Hotel>>> = _hotelDetails
    private val _hotelBooking =
        MutableStateFlow<Resource<List<Booking>>>(Resource.Unspecified())
    val hotelBooking: StateFlow<Resource<List<Booking>>> = _hotelBooking
    private val _hotelsearch =
        MutableStateFlow<Resource<List<Hotel>>>(Resource.Unspecified())
    val hotelSearch: StateFlow<Resource<List<Hotel>>> = _hotelsearch
    //hiện trang admin
    private val _hotelshow = MutableStateFlow<Resource<List<Hotel>>>(Resource.Unspecified())
    val hotelshow: StateFlow<Resource<List<Hotel>>> = _hotelshow

    init {
        fetchHotelDetails()
        fetchHotelBooking()
        fetchHotelShow()
    }

    private fun fetchHotelBooking() {
        viewModelScope.launch {
        }

        firestore.collection("user").document("${auth.currentUser!!.uid}").collection("bookings")
            .get().addOnSuccessListener { result ->
                val booking = result.toObjects(Booking::class.java)
                viewModelScope.launch {
                    _hotelBooking.emit(Resource.Success(booking))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _hotelBooking.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchHotelDetails() {
        viewModelScope.launch {
        }

        firestore.collection("Hotels")
            .get().addOnSuccessListener { result ->
                val hotel = result.toObjects(Hotel::class.java)
                viewModelScope.launch {
                    _hotelDetails.emit(Resource.Success(hotel))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _hotelDetails.emit(Resource.Error(it.message.toString()))
                }
            }
}
    fun fetchHotelShow() {
        viewModelScope.launch {
            _hotelshow.emit(Resource.Loading())
        }
        firestore.collection("Hotels").whereEqualTo("idAdmin","${auth.currentUser!!.uid}")
            .get().addOnSuccessListener {
                val hotelshow = it.toObjects(Hotel::class.java)
                viewModelScope.launch {
                    _hotelshow.emit(Resource.Success(hotelshow))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _hotelshow.emit(Resource.Error(it.message.toString()))
                }
            }
    }
//    fun searchHotels(query:String){
//        viewModelScope.launch {
//            _hotelsearch.emit(Resource.Loading())
//        }
//        firestore.collection("Hotels").whereEqualTo("address","${query}")
//            .get().addOnSuccessListener {
//                val hotelsearch = it.toObjects(Hotel::class.java)
//                viewModelScope.launch {
//                    _hotelsearch.emit(Resource.Success(hotelsearch))
//                }
//            }.addOnFailureListener {
//                viewModelScope.launch {
//                    _hotelsearch.emit(Resource.Error(it.message.toString()))
//                }
//            }
//    }
    fun searchHotel(query:String) {
        viewModelScope.launch {
            _hotelsearch.value = Resource.Loading()

            val searchResults = mutableListOf<Hotel>()

            // Tìm kiếm theo tên hotel hoặc địa chỉ
            val nameResults = firestore.collection("Hotels")
                .whereEqualTo("name", query)
                .get()
                .await()

            searchResults.addAll(nameResults.toObjects(Hotel::class.java))

            val addressResults = firestore.collection("Hotels")
                .whereEqualTo("address", query)
                .get()
                .await()

            searchResults.addAll(addressResults.toObjects(Hotel::class.java))

            // Sắp xếp kết quả theo giá tăng dần
            searchResults.sortBy { it.price }

            _hotelsearch.value = Resource.Success(searchResults)
//            _hotelsearch.value = Resource.Error("Không tìm thấy kết quả")
        }
    }

}