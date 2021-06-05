package com.perugu.goutham.freshworksgif.favourites

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.perugu.goutham.freshworksgif.R
import com.perugu.goutham.freshworksgif.TrendingGifAdapter
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FavoritesFragment: Fragment(R.layout.fragment_favorites_layout) {

    val viewModel : FavoritesViewModel by sharedViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.fav_recycler_view)
        var pagedAdapter = recyclerView.adapter
        if (pagedAdapter == null) {
            pagedAdapter = TrendingGifAdapter(emptyList()){
                viewModel.removeImage(it)
            }
            recyclerView.adapter = pagedAdapter
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect {
                (recyclerView.adapter as TrendingGifAdapter).submitList(it)
            }
        }
    }

}