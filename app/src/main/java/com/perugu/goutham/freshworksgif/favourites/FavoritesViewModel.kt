package com.perugu.goutham.freshworksgif.favourites

import androidx.lifecycle.*
import com.perugu.goutham.freshworksgif.common.convertToViewState
import com.perugu.goutham.freshworksgif.trending.Tile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesDao: FavoritesDao, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel(), LifecycleObserver {
    private val state = MutableStateFlow<List<Tile>>(emptyList())
    val uiState = state.asStateFlow()


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun fetchData(){
        viewModelScope.launch(dispatcher) {
            val flowOfFavoriteImages = favoritesDao.fetchFavoriteImages()
            flowOfFavoriteImages.collect {
                state.value = it.map {data ->
                    data.convertToViewState(favoritesDao)
                }
            }
        }
    }

    fun removeImage(id: String){
        viewModelScope.launch {
            favoritesDao.removeImage(id)
        }
    }

}