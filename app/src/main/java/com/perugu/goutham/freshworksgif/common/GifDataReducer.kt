package com.perugu.goutham.freshworksgif.common

import com.perugu.goutham.freshworksgif.favourites.FavoritesDao
import com.perugu.goutham.freshworksgif.trending.Tile
import com.perugu.goutham.freshworksgif.trending.TrendingGifResponse

suspend fun TrendingGifResponse.Data.convertToViewState(favoritesDao: FavoritesDao) = Tile(
    id = id,
    url = images.previewGif.url,
    width = images.fixedWidth.width,
    height = images.fixedWidth.height,
    isFavorite = favoritesDao.doesImageExists(id)
)