package com.bangkit.feynmind.data.retrofit

import com.bangkit.feynmind.data.response.ChatResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("api/v1/chat/create-session")
    fun createSession(): Call<ChatResponse>

    @GET("api/v1/chat/classes")
    fun getClasses(): Call<List<String>>

    @GET("api/v1/chat/topics")
    fun getTopics(@Query("className") className: String): Call<List<String>>

    @POST("api/v1/chat/topic-preference")
    fun setTopicPreference(@Body requestBody: RequestBody): Call<ChatResponse>

    @Multipart
    @POST("api/v1/chat/uploadPDF")
    fun uploadPDF(
        @Part pdf: MultipartBody.Part,
        @Part("sessionId") sessionId: RequestBody
    ): Call<ChatResponse>

    @POST("api/v1/chat/inputTeks")
    fun inputText(@Body requestBody: RequestBody): Call<ChatResponse>

    @GET("api/v1/chat/history")
    fun getChatHistory(@Query("sessionId") sessionId: String): Call<List<ChatResponse>>
}