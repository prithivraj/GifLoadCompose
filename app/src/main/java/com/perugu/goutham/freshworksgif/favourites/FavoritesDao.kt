package com.perugu.goutham.freshworksgif.favourites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.perugu.goutham.freshworksgif.trending.TrendingGifResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoritesDao {

    @Insert(onConflict = IGNORE)
    suspend fun insertGifRenderData(favImage: TrendingGifResponse.Data)

    @Query("SELECT * FROM Data")
    fun fetchFavoriteImages(): Flow<List<TrendingGifResponse.Data>>

    @Query("SELECT EXISTS(SELECT * FROM Data WHERE id = :id)")
    suspend fun doesImageExists(id : String) : Boolean

    @Query("DELETE FROM Data WHERE id = :id")
    suspend fun removeImage(id: String)

}