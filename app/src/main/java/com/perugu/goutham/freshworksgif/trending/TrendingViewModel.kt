package com.perugu.goutham.freshworksgif.trending

import androidx.lifecycle.*
import com.blankj.utilcode.util.NetworkUtils
import com.perugu.goutham.freshworksgif.R
import com.perugu.goutham.freshworksgif.common.convertToViewState
import com.perugu.goutham.freshworksgif.favourites.FavoritesDao
import com.perugu.goutham.freshworksgif.network.GiphyApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TrendingViewModel(
    private val giphyApi: GiphyApi,
    private val favoritesDao: FavoritesDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val isNetworkAvailable: () -> Boolean = { NetworkUtils.isConnected() }
) : ViewModel(), LifecycleObserver {

    private val state = MutableStateFlow<ViewState>(ViewState.Initial)
    val uiState = state.asStateFlow()

    private var currentTiles = listOf<Tile>()
    private var currentData = mutableSetOf<TrendingGifResponse.Data>()
    private var lastLoadedOffset = 0
    private var searchMode = false

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        fetchContent()
        listenForFavoriteUpdates()
    }

    private fun listenForFavoriteUpdates() {
        viewModelScope.launch(dispatcher) {
            favoritesDao.fetchFavoriteImages().collect {
                val updatedList: List<Tile> = currentData.map {
                    it.convertToViewState(favoritesDao)
                }

                currentTiles = updatedList
                state.value = ViewState.Content(
                    //Giphy response returns multiple gifs with same ID
                    currentTiles.distinctBy { it.id }
                )
            }
        }
    }

    fun fetchContent(isRefresh: Boolean = false) {
        viewModelScope.launch(dispatcher) {
            if (searchMode) return@launch
            state.value = ViewState.Loading
            if (isNetworkAvailable()) {
                try {
                    if (isRefresh) {
                        currentData.clear()
                        currentTiles = listOf()
                        lastLoadedOffset = 0
                    }
                    val response = giphyApi.fetchTrendingGif(lastLoadedOffset)
                    state.value = if (response.isSuccessful) {
                        val body: TrendingGifResponse? = response.body()
                        if (body == null) {
                            ViewState.Error(R.string.error_empty_body)
                        } else {
                            currentData += body.data
                            currentTiles = currentTiles + body.data.map {
                                it.convertToViewState(favoritesDao)
                            }
                            lastLoadedOffset += body.data.size
                            ViewState.Content(
                                tiles = currentTiles.distinctBy { it.id }
                            )
                        }
                    } else {
                        ViewState.Error(R.string.error_network)
                    }
                } catch (e: Exception) {
                    ViewState.Error(R.string.error_network)
                }
            } else {
                state.value = ViewState.Error(R.string.no_network)
            }
        }
    }

    fun search(input: String) {
        viewModelScope.launch(dispatcher) {
            if (input.isNotEmpty()) {
                searchMode = true
            } else {
                searchMode = false
                lastLoadedOffset = 0
                currentTiles = mutableListOf()
                currentData = mutableSetOf()
                fetchContent()
                return@launch
            }
            state.value = ViewState.Loading
            if (isNetworkAvailable()) {
                try {
                    val searchGifResponse = giphyApi.searchGif(input)
                    state.value = if (searchGifResponse.isSuccessful) {
                        val body = searchGifResponse.body()
                        if (body == null) {
                            ViewState.Error(R.string.error_empty_body)
                        } else {
                            currentTiles = body.data.map {
                                it.convertToViewState(favoritesDao)
                            }
                            currentData.clear()
                            currentData += body.data
                            ViewState.Content(
                                currentTiles.distinctBy { it.id }
                            )
                        }
                    } else {
                        ViewState.Error(R.string.error_network)
                    }
                } catch (e: Exception) {
                    ViewState.Error(R.string.error_network)
                }
            } else {
                state.value = ViewState.Error(R.string.no_network)
            }
        }
    }

    fun favoriteImageClicked(id: String) {
        viewModelScope.launch(dispatcher) {
            val favImage = currentData.find { it.id == id }
            if (favImage != null) {
                if (favoritesDao.doesImageExists(id)) {
                    favoritesDao.removeImage(id)
                } else {
                    favoritesDao.insertGifRenderData(favImage)
                }
            }
        }
    }

    fun refresh(searchString: String = "") {
        if (!searchMode) {
            fetchContent(isRefresh = true)
        } else {
            search(searchString)
        }
    }

}