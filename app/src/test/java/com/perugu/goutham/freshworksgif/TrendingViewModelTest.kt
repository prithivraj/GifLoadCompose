package com.perugu.goutham.freshworksgif

import com.google.gson.Gson
import com.perugu.goutham.freshworksgif.favourites.FavoritesDao
import com.perugu.goutham.freshworksgif.network.GiphyApi
import com.perugu.goutham.freshworksgif.trending.Tile
import com.perugu.goutham.freshworksgif.trending.TrendingGifResponse
import com.perugu.goutham.freshworksgif.trending.ViewState
import com.perugu.goutham.freshworksgif.trending.TrendingViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Response

@ExperimentalCoroutinesApi
class TrendingViewModelTest {
    private val mockApi: GiphyApi = mockk()
    private val mockDao: FavoritesDao = mockk()
    private val testDispatcherState = TestCoroutineDispatcher()
    private val viewModel = TrendingViewModel(mockApi, mockDao, testDispatcherState) { true }
    private val gson = Gson()

    @Test
    fun testSuccess() = runBlockingTest {
        // Arrange
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        val dummyData: TrendingGifResponse =
            gson.fromJson(response0to1, TrendingGifResponse::class.java)
        coEvery { mockApi.fetchTrendingGif() } returns Response.success(dummyData)
        coEvery { mockDao.doesImageExists("UYsHE3pxe2U0COs0Pe") } returns false

        // Act
        viewModel.fetchContent()

        // Assert
        assertTrue(
            stateList.last() == ViewState.Content(
                listOf(
                    Tile(
                        id = "UYsHE3pxe2U0COs0Pe",
                        url = "https://media0.giphy.com/media/UYsHE3pxe2U0COs0Pe/giphy-preview.gif?cid=8fb67e8e1kxpup524qvo2ooa71uaz56nkecgtccf2ppgz85f&rid=giphy-preview.gif&ct=g",
                        width = 200,
                        height = 200,
                        isFavorite = false
                    )
                )
            )
        )
        job.cancel()
    }

    @Test
    fun testError() = runBlockingTest {
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        coEvery { mockApi.fetchTrendingGif() } returns Response.error(404, mockk(relaxed = true))
        viewModel.fetchContent()
        assertTrue(
            stateList.last() == ViewState.Error(R.string.error_network)
        )
        job.cancel()
    }

    @Test
    fun testEmptyBody() = runBlockingTest {
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        coEvery { mockApi.fetchTrendingGif() } returns Response.success(null)
        viewModel.fetchContent()
        assertTrue(
            stateList.last() == ViewState.Error(R.string.error_empty_body)
        )
        job.cancel()
    }

    @Test
    fun testPagination() = runBlockingTest {
        // Arrange
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        val dummyData1: TrendingGifResponse =
            gson.fromJson(response0to1, TrendingGifResponse::class.java)
        val dummyData2: TrendingGifResponse =
            gson.fromJson(response2to3, TrendingGifResponse::class.java)
        coEvery { mockApi.fetchTrendingGif(0) } returns Response.success(dummyData1)
        coEvery { mockApi.fetchTrendingGif(1) } returns Response.success(dummyData2)
        coEvery { mockDao.doesImageExists("UYsHE3pxe2U0COs0Pe") } returns false
        coEvery { mockDao.doesImageExists("3o7bujNF9KpbEMBydW") } returns false

        // Act
        viewModel.fetchContent()
        viewModel.fetchContent()

        // Assert
        assertTrue(
            stateList.last() == ViewState.Content(
                listOf(
                    Tile(
                        id = "UYsHE3pxe2U0COs0Pe",
                        url = "https://media0.giphy.com/media/UYsHE3pxe2U0COs0Pe/giphy-preview.gif?cid=8fb67e8e1kxpup524qvo2ooa71uaz56nkecgtccf2ppgz85f&rid=giphy-preview.gif&ct=g",
                        width = 200,
                        height = 200,
                        isFavorite = false
                    ),
                    Tile(
                        id = "3o7bujNF9KpbEMBydW",
                        url = "https://media3.giphy.com/media/3o7bujNF9KpbEMBydW/giphy-preview.gif?cid=8fb67e8ejmrpvxj16cuzmzgddqpmgko65oiyqxch5k0e1hfn&rid=giphy-preview.gif&ct=g",
                        width = 200,
                        height = 113,
                        isFavorite = false
                    )
                )
            )
        )
        job.cancel()
    }

    @Test
    fun searchTest() = runBlockingTest {
        // Arrange
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        val dummySearchResponse: TrendingGifResponse =
            gson.fromJson(searchResponse, TrendingGifResponse::class.java)
        coEvery { mockApi.searchGif("lion") } returns Response.success(dummySearchResponse)
        val dummyData: TrendingGifResponse =
            gson.fromJson(response0to1, TrendingGifResponse::class.java)
        coEvery { mockApi.fetchTrendingGif() } returns Response.success(dummyData)
        coEvery { mockDao.doesImageExists("UYsHE3pxe2U0COs0Pe") } returns true
        coEvery { mockDao.doesImageExists("OqFpgF7bet1sRoCmpb") } returns true

        // Act
        viewModel.fetchContent()
        viewModel.search("lion")

        // Assert
        assertTrue(
            stateList.last() == ViewState.Content(
                tiles = listOf(
                    Tile(
                        id = "OqFpgF7bet1sRoCmpb",
                        url = "https://media1.giphy.com/media/OqFpgF7bet1sRoCmpb/giphy-preview.gif?cid=8fb67e8e95nz7spxdl63xek6pbyqp0yf8h56wat1y899xaqy&rid=giphy-preview.gif&ct=g",
                        width = 200,
                        height = 105,
                        isFavorite = true
                    )
                )
            )
        )
        job.cancel()
    }

    @Test
    fun searchToTrendingModeTest() = runBlockingTest {
        // Arrange
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        val dummySearchResponse: TrendingGifResponse =
            gson.fromJson(searchResponse, TrendingGifResponse::class.java)
        coEvery { mockApi.searchGif("lion") } returns Response.success(dummySearchResponse)
        val dummyData: TrendingGifResponse =
            gson.fromJson(response0to1, TrendingGifResponse::class.java)
        coEvery { mockApi.fetchTrendingGif() } returns Response.success(dummyData)
        coEvery { mockDao.doesImageExists("UYsHE3pxe2U0COs0Pe") } returns false


        // Act
        viewModel.search("")

        // Assert
        assertTrue(
            stateList.last() == ViewState.Content(
                tiles = listOf(
                    Tile(
                        id = "UYsHE3pxe2U0COs0Pe",
                        url = "https://media0.giphy.com/media/UYsHE3pxe2U0COs0Pe/giphy-preview.gif?cid=8fb67e8e1kxpup524qvo2ooa71uaz56nkecgtccf2ppgz85f&rid=giphy-preview.gif&ct=g",
                        width = 200,
                        height = 200,
                        isFavorite = false
                    )
                )
            )
        )
        job.cancel()
    }


    @Test
    fun testTrendingDataRefresh()= runBlockingTest {
        // Arrange
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        val dummyData1: TrendingGifResponse =
            gson.fromJson(response0to1, TrendingGifResponse::class.java)
        val dummyData2: TrendingGifResponse =
            gson.fromJson(response2to3, TrendingGifResponse::class.java)
        coEvery { mockApi.fetchTrendingGif(0) } returns Response.success(dummyData1)
        coEvery { mockApi.fetchTrendingGif(1) } returns Response.success(dummyData2)
        coEvery { mockDao.doesImageExists("UYsHE3pxe2U0COs0Pe") } returns false
        coEvery { mockDao.doesImageExists("3o7bujNF9KpbEMBydW") } returns false

        // Act
        viewModel.fetchContent()
        viewModel.refresh()

        // Assert
        assertTrue(
            stateList.last() == ViewState.Content(
                listOf(
                    Tile(
                        id = "UYsHE3pxe2U0COs0Pe",
                        url = "https://media0.giphy.com/media/UYsHE3pxe2U0COs0Pe/giphy-preview.gif?cid=8fb67e8e1kxpup524qvo2ooa71uaz56nkecgtccf2ppgz85f&rid=giphy-preview.gif&ct=g",
                        width = 200,
                        height = 200,
                        isFavorite = false
                    ),
                )
            )
        )
        job.cancel()
    }

    @Test
    fun refreshSearchContent() = runBlockingTest{
        // Arrange
        val stateList = mutableListOf<ViewState>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        val dummySearchResponse: TrendingGifResponse =
            gson.fromJson(searchResponse, TrendingGifResponse::class.java)
        coEvery { mockApi.searchGif("lion") } returns Response.success(dummySearchResponse)
        val dummyData: TrendingGifResponse =
            gson.fromJson(response0to1, TrendingGifResponse::class.java)
        coEvery { mockApi.fetchTrendingGif() } returns Response.success(dummyData)
        coEvery { mockDao.doesImageExists("OqFpgF7bet1sRoCmpb") } returns true

        // Act
        viewModel.search("lion")
        viewModel.refresh("lion")

        // Assert
        assertTrue(
            stateList.last() == ViewState.Content(
                tiles = listOf(
                    Tile(
                        id = "OqFpgF7bet1sRoCmpb",
                        url = "https://media1.giphy.com/media/OqFpgF7bet1sRoCmpb/giphy-preview.gif?cid=8fb67e8e95nz7spxdl63xek6pbyqp0yf8h56wat1y899xaqy&rid=giphy-preview.gif&ct=g",
                        width = 200,
                        height = 105,
                        isFavorite = true
                    )
                )
            )
        )
        job.cancel()
    }
}