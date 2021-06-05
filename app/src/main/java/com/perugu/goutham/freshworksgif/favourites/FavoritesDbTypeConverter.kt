package com.perugu.goutham.freshworksgif.favourites

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.perugu.goutham.freshworksgif.trending.TrendingGifResponse

class FavoritesDbTypeConverter {

    @TypeConverter
    fun imagesToString(images: TrendingGifResponse.Data.Images): String{
        return Gson().toJson(images)
    }

    @TypeConverter
    fun stringToImage(string: String): TrendingGifResponse.Data.Images{
        return Gson().fromJson(string, TrendingGifResponse.Data.Images::class.java)
    }
}