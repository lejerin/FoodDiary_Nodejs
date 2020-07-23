package com.example.fooddiary.Model;

data class Photo(
    val id: String,
    val uri1: String,
    val uri2: String?,
    val uri3: String?,
    val uri4: String?,
    val text: String,
    val locationname: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val time: Int,
    val ranknum: Int
)
