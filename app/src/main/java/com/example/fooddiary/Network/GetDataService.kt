package com.example.fooddiary.Network

import com.example.fooddiary.Model.*
import retrofit2.Call
import retrofit2.http.*

interface GetDataService {

    @GET("customers")
    fun getAllUsers(
    ): Call<User>

    @GET("customers/{email}")
    fun getEmailUser(
        @Path("email") email: String
    ): Call<User>

    @POST("customers")
    fun putNewUserJson(
        @Body body: User
    ): Call<User>


    @POST("times")
    fun putNewTime(
        @Body body: Time
    ): Call<Time>

    @GET("times")
    fun getEmailAndDateTime(
        @Query("email") email: String,
        @Query("year") year: String,
        @Query("month") month: String,
        @Query("order") order: String
    ): Call<List<Time>>

    @GET("times/count")
    fun getPostnumTime(
        @Query("email") email: String,
        @Query("date") date: String
    ): Call<TimeCount>

    @PUT("times")
    fun putTimeUpdate(
        @Query("id") id: String,
        @Query("date") date: String,
        @Query("uri1") uri1: String
    ): Call<Time>

    @POST("photos")
    fun putNewPhoto(
        @Body body: Photo
    ): Call<Photo>

    @PUT("photos")
    fun putPhotoUpdate(
        @Query("id") id: String,
        @Body body: Photo
    ): Call<Photo>

    @GET("photos")
    fun getPostnumCountPhoto(
        @Query("id") id: String
    ): Call<Photo>

    @GET("photos/location")
    fun getLocationJoin(
        @Query("email") email: String,
        @Query("order") order: String
    ): Call<List<Join>>

    @GET("photos/location")
    fun getAddressAllJoin(
        @Query("email") email: String,
        @Query("address") address: String,
        @Query("order") order: String
    ): Call<List<Time>>

    @DELETE("photos")
    fun setDeletePhotoById(
        @Query("id") id: String
    ): Call<Photo>

    @GET("keyword.json")
    fun getKeywordMap(
        @Query("query") query: String,
        @Header("Authorization") Authorization: String
    ): Call<LocalMapData>

}