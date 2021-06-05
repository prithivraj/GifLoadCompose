package com.perugu.goutham.freshworksgif.trending

import androidx.annotation.StringRes

sealed class ViewState {
    object Initial: ViewState()

    object Loading: ViewState()

    data class Content(
        val tiles: List<Tile> = emptyList()
    ): ViewState()

    data class Error(
        @StringRes val reason: Int
    ): ViewState()
}

data class Tile(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val isFavorite: Boolean,
)