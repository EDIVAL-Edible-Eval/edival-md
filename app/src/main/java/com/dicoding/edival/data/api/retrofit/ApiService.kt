package com.dicoding.edival.data.api.retrofit

import com.dicoding.edival.data.api.response.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("user/{id}")
    fun getUserProfile(
        @Path("id") id:String
    ): Call<UserResponse>

}