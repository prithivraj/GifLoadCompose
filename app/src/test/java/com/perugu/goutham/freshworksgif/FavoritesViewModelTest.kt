package com.perugu.goutham.freshworksgif

import com.google.gson.Gson
import com.perugu.goutham.freshworksgif.favourites.FavoritesDao
import com.perugu.goutham.freshworksgif.favourites.FavoritesViewModel
import com.perugu.goutham.freshworksgif.trending.Tile
import com.perugu.goutham.freshworksgif.trending.TrendingGifResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

class FavoritesViewModelTest {
    private val mockDao: FavoritesDao = mockk()
    @ExperimentalCoroutinesApi
    private val testDispatcherState = TestCoroutineDispatcher()
    @ExperimentalCoroutinesApi
    private val viewModel = FavoritesViewModel(mockDao, testDispatcherState)
    private val gson = Gson()

    @ExperimentalCoroutinesApi
    @Test
    fun testFetch() = runBlockingTest {
        // Arrange
        val stateList = mutableListOf<List<Tile>>()
        val job = launch {
            viewModel.uiState.toList(stateList)
        }
        val dummyData: TrendingGifResponse =
            gson.fromJson(response0to1, TrendingGifResponse::class.java)
        coEvery { mockDao.fetchFavoriteImages() } returns flowOf(dummyData.data)
        coEvery { mockDao.doesImageExists("UYsHE3pxe2U0COs0Pe") } returns true

        // Act
        viewModel.fetchData()

        // Assert
        Assert.assertTrue(
            stateList.last() ==
                    listOf(
                        Tile(
                            id = "UYsHE3pxe2U0COs0Pe",
                            url = "https://media0.giphy.com/media/UYsHE3pxe2U0COs0Pe/giphy-preview.gif?cid=8fb67e8e1kxpup524qvo2ooa71uaz56nkecgtccf2ppgz85f&rid=giphy-preview.gif&ct=g",
                            width = 200,
                            height = 200,
                            isFavorite = true
                        )
                    )

        )
        job.cancel()
    }
}