package com.example.project03.data.order


import android.icu.text.SimpleDateFormat
import com.example.project03.data.Address
import com.example.project03.data.CartProduct
import java.util.*
import kotlin.random.Random.Default.nextLong

data class Order(
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val products: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val date: String = SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()


) {
}


