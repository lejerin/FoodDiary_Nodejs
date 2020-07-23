package com.example.fooddiary.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class User(
   // val id: Int,
    val email: String,
    val password: String,
    val name: String
)
