package com.example.project03.data

data class CartProduct(
    val product: Product,
    val quality: Int,
    val selectedColor:Int?=null,
    val selectedSize: String?=null
){
    constructor():this(Product(),1,null, null)
}
