package com.example.project03.data.order

sealed class OrderStatus(val string: String) {

    object Ordered:OrderStatus("Ordered")
    object Canceled:OrderStatus("Canceled")
    object Confirmed:OrderStatus("Confirmed")
    object Shipped:OrderStatus("Shipped")
    object Delivered:OrderStatus("Delivered")
    object Returned:OrderStatus("Returned")
}