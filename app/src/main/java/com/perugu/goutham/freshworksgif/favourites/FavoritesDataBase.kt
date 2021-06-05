package com.perugu.goutham.freshworksgif.favourites

import androidx.room.Database
import androidx.room.RoomDatabase
import com.perugu.goutham.freshworksgif.trending.TrendingGifResponse

@Database(entities = [TrendingGifResponse.Data::class], version = 1)
abstract class FavoritesDataBase: RoomDatabase() {
    abstract fun favoritesDao() : FavoritesDao
}