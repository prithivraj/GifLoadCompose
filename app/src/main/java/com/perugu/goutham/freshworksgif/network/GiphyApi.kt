package com.perugu.goutham.freshworksgif.network

import com.perugu.goutham.freshworksgif.trending.TrendingGifResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {
    @GET("/v1/gifs/trending?api_key=41n1HbCXf81ZIRBzzw9HiPeiI8AJIyG9")
    suspend fun fetchTrendingGif(
        @Query("offset") from: Int = 0,
        @Query("limit") limit: Int = 20
    ): Response<TrendingGifResponse>

    @GET("/v1/gifs/search?api_key=41n1HbCXf81ZIRBzzw9HiPeiI8AJIyG9&offset=0&&limit=50")
    suspend fun searchGif(@Query("q") str: String): Response<TrendingGifResponse>
}