package com.example.project03.data

data class Admin(
    val firstName:String,
    val lastName:String,
    val email:String,
    val phone:String,
    val role:String="admin",
    val imgPath:String="",
) {
    constructor():this("","","","","admin","")
}