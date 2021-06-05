package com.perugu.goutham.freshworksgif.trending

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.perugu.goutham.freshworksgif.favourites.FavoritesDbTypeConverter

data class TrendingGifResponse(
    @SerializedName("data")
    val `data`: List<Data> = listOf(),
    @SerializedName("meta")
    val meta: Meta = Meta(),
) {

    @Entity
    @TypeConverters(FavoritesDbTypeConverter::class)
    data class Data(
        @SerializedName("id")
        @PrimaryKey
        val id: String = "",
        @SerializedName("images")
        val images: Images = Images(),
    ) {
        data class Images(
            @SerializedName("fixed_width")
            val fixedWidth: FixedWidth = FixedWidth(),
            @SerializedName("preview_gif")
            val previewGif: PreviewGif = PreviewGif(),
        ) {
            data class FixedWidth(
                @SerializedName("height")
                val height: Int = 0,
                @SerializedName("width")
                val width: Int = 0
            )

            data class PreviewGif(
                @SerializedName("url")
                val url: String = "",
            )
        }
    }

    data class Meta(
        @SerializedName("msg")
        val msg: String = "",
        @SerializedName("status")
        val status: Int = 0
    )
}