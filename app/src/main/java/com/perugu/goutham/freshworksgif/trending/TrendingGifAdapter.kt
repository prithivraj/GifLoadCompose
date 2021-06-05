package com.perugu.goutham.freshworksgif

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.perugu.goutham.freshworksgif.trending.Tile

class TrendingGifAdapter(var oldList: List<Tile>, private val onClick:(String) -> Unit ): RecyclerView.Adapter<GifViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_content_layout, parent, false)
        return GifViewHolder(view)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val tile = oldList[position]

        val imageView = holder.imageView

        val layoutParams = imageView.layoutParams
        layoutParams.width = enhance(tile.width, imageView.context.resources)
        layoutParams.height = enhance(tile.height, imageView.context.resources)

        val parent = holder.parent
        val parentParams = parent.layoutParams
        parentParams.width = enhance(tile.width, imageView.context.resources)
        parentParams.height = enhance(tile.height, imageView.context.resources)

        Glide.with(holder.itemView.context)
            .asGif()
            .load(tile.url)
            .placeholder(R.drawable.placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)

        if (tile.isFavorite) {
            holder.favIcon.setImageDrawable(AppCompatResources.getDrawable(imageView.context, R.drawable.ic_baseline_favorite_24))
        } else {
            holder.favIcon.setImageDrawable(AppCompatResources.getDrawable(imageView.context, R.drawable.ic_baseline_favorite_border_24))
        }

        holder.favIcon.setOnClickListener {
            onClick(tile.id)
        }

    }

    private fun enhance(px: Int, resources: Resources): Int {
        return (px * resources.displayMetrics.density).toInt()
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    fun submitList(newList: List<Tile>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]

            }
        }).dispatchUpdatesTo(this)
        this.oldList = newList
    }
}

class GifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: ImageView = view.findViewById(R.id.image_view)
    val favIcon: ImageButton = view.findViewById(R.id.fav_button)
    val parent: RelativeLayout = view.findViewById(R.id.parent)
}