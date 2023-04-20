package com.example.project03.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CartProduct(
    val product: Product,
    val quality: Int,
    val selectedColor:Int?=null,
    val selectedSize: String?=null
): Parcelable{
    constructor():this(Product(),1,null, null)
}
