package com.perugu.goutham.freshworksgif.common

import android.content.Context
import androidx.room.Room
import com.perugu.goutham.freshworksgif.BuildConfig
import com.perugu.goutham.freshworksgif.favourites.FavoritesDao
import com.perugu.goutham.freshworksgif.favourites.FavoritesDataBase
import com.perugu.goutham.freshworksgif.favourites.FavoritesViewModel
import com.perugu.goutham.freshworksgif.network.GiphyApi
import com.perugu.goutham.freshworksgif.trending.TrendingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val commonModule = module {
    single { provideRetroFit() }
    single { provideGiphyApi(get()) }
    single { provideFavoriteDao(androidContext()) }

    viewModel {
        TrendingViewModel(giphyApi = get(), favoritesDao = get())
    }
    viewModel {
        FavoritesViewModel(favoritesDao = get())
    }
}

private fun provideRetroFit(): Retrofit {
    return Retrofit.Builder().apply {
        addConverterFactory(GsonConverterFactory.create())
        baseUrl(BuildConfig.HOST)
    }.build()
}

private fun provideGiphyApi(retrofit: Retrofit): GiphyApi? {
    return retrofit.create(GiphyApi::class.java)
}

private fun provideFavoriteDao(context: Context): FavoritesDao {
    return Room.databaseBuilder(context, FavoritesDataBase::class.java, "giphy_database").build().favoritesDao()
}

