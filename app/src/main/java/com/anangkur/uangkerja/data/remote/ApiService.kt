package com.anangkur.uangkerja.data.remote

import com.anangkur.uangkerja.BuildConfig.baseUrl
import com.anangkur.uangkerja.data.model.BasePagination
import com.anangkur.uangkerja.data.model.BaseResponse
import com.anangkur.uangkerja.data.model.product.Product
import com.anangkur.uangkerja.data.model.auth.Register
import com.anangkur.uangkerja.data.model.auth.ResponseLogin
import com.anangkur.uangkerja.data.model.config.ConfigCoin
import com.anangkur.uangkerja.data.model.product.Category
import com.anangkur.uangkerja.data.model.product.DetailProduct
import com.anangkur.uangkerja.data.model.profile.ResponseUser
import com.anangkur.uangkerja.data.model.profile.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiService {

    @GET("config/coin")
    suspend fun getConfigCoin(
        @Header("Authorization") token: String
    ): Response<BaseResponse<List<ConfigCoin>>>

    @GET("product")
    suspend fun getListProduct(
        @Header("Authorization") token: String,
        @Query("category_id") category: Int?,
        @Query("page") page: Int?
    ): Response<BaseResponse<BasePagination<Product>>>

    @GET("category")
    suspend fun getListCategory(
        @Header("Authorization") token: String
    ): Response<BaseResponse<List<Category>>>

    @GET("product/{product_id}")
    suspend fun getDetailProduct(
        @Header("Authorization") token: String,
        @Path("product_id") productId: String
    ): Response<BaseResponse<DetailProduct>>

    @POST("login")
    @FormUrlEncoded
    suspend fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ResponseLogin>

    @POST("register")
    @FormUrlEncoded
    suspend fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirm: String
    ): Response<BaseResponse<Register>>

    @GET("profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<User>

    companion object Factory{
        val getApiService: ApiService by lazy {

            val mClient =
                OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor { chain ->
                        val request =
                            chain.request()
                                .newBuilder()
                                .addHeader("User-Agent", "UANGKERJA-MOBILE")
                                .build()
                        chain.proceed(request)
                    }
                    .build()

            val mRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build()

            mRetrofit.create(ApiService::class.java)
        }
    }
}