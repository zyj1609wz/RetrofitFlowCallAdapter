package com.example.myapplication

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * @author : zhaoyanjun
 * @time : 2021/10/26
 * @desc :  flow
 */
interface ApiService {

    @GET("users/{user}/repos")
    fun listReposWithBody(@Path("user") user: String): Flow<List<Repo>>

    @GET("users/{user}/repos")
    fun listReposWithResponse(@Path("user") user: String): Flow<Response<List<Repo>>>
}