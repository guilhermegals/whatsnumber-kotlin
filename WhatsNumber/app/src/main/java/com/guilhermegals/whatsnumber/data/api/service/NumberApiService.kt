package com.guilhermegals.whatsnumber.data.api.service

import com.guilhermegals.whatsnumber.data.model.ResultModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NumberApiService {

    @GET("rand")
    suspend fun getNumber(
        @Query("min") min: Int,
        @Query("max") max: Int
    ): Response<ResultModel<Int>>
}