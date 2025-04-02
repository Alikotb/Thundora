package com.example.thundora.view.favorite.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.domain.model.api.Response
import com.example.thundora.domain.model.api.Weather
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals


@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: RepositoryImpl
    private lateinit var viewModel: FavoriteViewModel

    val cloud = mockk<Weather.Clouds>()
    val coord = mockk<Weather.Coord>()
    val main = mockk<Weather.Main>()
    val sys = mockk<Weather.Sys>()
    val wind = mockk<Weather.Wind>()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxUnitFun = true)

        coEvery {
            repository.getAllWeather()
        } returns flowOf(emptyList())

        coEvery {
            repository.getWeather(any(), any(), any(), any())
        }

        viewModel = FavoriteViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun getFavoriteCities() {
        val weatherList = listOf(
            Weather(
                clouds = cloud,
                cod = 2950,
                coord = coord,
                dt = 2571,
                id = 4316,
                main = main,
                name = "cairo",
                sys = sys,
                timezone = 3957,
                visibility = 1961,
                weather = listOf(),
                wind = wind,
                base = "m",
            ),
            Weather(
                clouds = cloud,
                cod = 2950,
                coord = coord,
                dt = 2571,
                id = 4316,
                main = main,
                name = "assuit",
                sys = sys,
                timezone = 3957,
                visibility = 1961,
                weather = listOf(),
                wind = wind,
                base = "m",
            ),
            Weather(
                clouds = cloud,
                cod = 2950,
                coord = coord,
                dt = 2571,
                id = 4316,
                main = main,
                name = "alex",
                sys = sys,
                timezone = 3957,
                visibility = 1961,
                weather = listOf(),
                wind = wind,
                base = "m",
            ),
        ).sortedBy { it.name }
        coEvery {
            repository.getAllWeather()
        } returns flowOf(weatherList)
        viewModel.getFavoriteCities()
        testDispatcher.scheduler.advanceUntilIdle()

        val favoriteCities = viewModel.favoriteCities.value
        assertEquals(Response.Success(weatherList), favoriteCities)
    }


//    @Test
//    fun addFavoriteCity() {
//        val weather = Weather(
//            clouds = cloud,
//            cod = 2950,
//            coord = coord,
//            dt = 2571,
//            id = 4316,
//            main = main,
//            name = "cairo",
//            sys = sys,
//            timezone = 3957,
//            visibility = 1961,
//            weather = listOf(),
//            wind = wind,
//            base = "m",
//        )
//        coEvery {
//            repository.addWeather(weather)
//        } returns Unit
//        viewModel.addFavoriteCity(weather)
//        testDispatcher.scheduler.advanceUntilIdle()
//        val favoriteCity = viewModel.favoriteCity.value
//        assertEquals(Response.Success(weather), favoriteCity)
//
//    }

}