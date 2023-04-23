package com.example.project03.data.order


import android.os.Parcelable
import com.example.project03.data.Address
import com.example.project03.data.CartProduct
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address
):Parcelable

