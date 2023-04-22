package com.example.project03.data.order


import android.os.Parcelable
import com.example.project03.data.Address
import com.example.project03.data.CartProduct
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong
@Parcelize
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address,
    val date:String=SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId:Long=nextLong(0,100_000_000_000)+totalPrice.toLong(),


):Parcelable{

}

