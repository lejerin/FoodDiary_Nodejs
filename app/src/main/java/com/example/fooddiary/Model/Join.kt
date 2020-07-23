package com.example.fooddiary.Model;

data class Join(
    val id: String,
    val date: String,
    val uri1: String,
    val locationname: String,
    val address: String?,
    val ranknum: Int,
    var ranknum1: Int = 0,
    var ranknum2: Int = 0,
    var ranknum3: Int = 0,
    var num: Int = 0
)
