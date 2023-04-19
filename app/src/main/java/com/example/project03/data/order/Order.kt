package com.example.project03.data.order

import com.example.project03.data.Address
import com.example.project03.data.CartProduct

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address
)