package com.example.project03.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth
) {
    private val cartColection= firestore.collection("user").document(auth.uid!!).collection("cart")
    fun addProductToCart(cartProduct: CartProduct,onResult:(CartProduct?,Exception?) ->  Unit){
        cartColection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct,null)
            }
            .addOnFailureListener {
                onResult(null,it)
            }
    }
    fun increaseQuantity(documentId: String,onResult:(String?,Exception?) ->  Unit){
        firestore.runTransaction { transition->
            val documentRef=cartColection.document(documentId)
            val document=transition.get(documentRef)
            val productObject= document.toObject(CartProduct::class.java)
            productObject?.let {cartProduct->
                val newQuantity=cartProduct.quality+1
                val newProductObject=cartProduct.copy(quality = newQuantity)
                transition.set(documentRef,newProductObject)
            }

        }.addOnSuccessListener {
            onResult(documentId,null)

        }.addOnFailureListener {
            onResult(documentId,it)
        }
    }

    fun decreaseQuantity(documentId: String,onResult:(String?,Exception?) ->  Unit){
        firestore.runTransaction { transition->
            val documentRef=cartColection.document(documentId)
            val document=transition.get(documentRef)
            val productObject= document.toObject(CartProduct::class.java)
            productObject?.let {cartProduct->
                val newQuantity=cartProduct.quality - 1
                val newProductObject=cartProduct.copy(quality = newQuantity)
                transition.set(documentRef,newProductObject)
            }

        }.addOnSuccessListener {
            onResult(documentId,null)

        }.addOnFailureListener {
            onResult(documentId,it)
        }
    }

    enum class QuantityChanging {
        INCREASE, DECREASE
    }



}