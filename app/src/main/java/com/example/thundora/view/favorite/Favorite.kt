package com.example.thundora.view.favorite

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.domain.model.api.Response
import com.example.thundora.domain.model.api.Weather
import com.example.thundora.utils.CountryHelper
import com.example.thundora.utils.DateTimeHelper
import com.example.thundora.utils.formatNumberBasedOnLanguage
import com.example.thundora.utils.getDegree
import com.example.thundora.utils.transferUnit
import com.example.thundora.view.components.Empty
import com.example.thundora.view.components.Error
import com.example.thundora.view.components.LoadingScreen
import com.example.thundora.view.components.SwipeToDeleteContainer
import com.example.thundora.view.components.getIcon
import com.example.thundora.view.components.getRandomGradient
import com.example.thundora.view.favorite.viewModel.FavoriteFactory
import com.example.thundora.view.favorite.viewModel.FavoriteViewModel
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoriteScreen(
    navToDetails: (city: String, lang: Double, lat: Double) -> Unit
) {


    val viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteFactory(
            RepositoryImpl.getInstance(
                RemoteDataSource(
                    ApiClient.weatherService
                ),
                LocalDataSource
                    (
                    WeatherDataBase.getInstance(LocalContext.current).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )

    val temp by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val temperatureUnit = getDegree(Locale.getDefault().language, temp)
    val favoriteCities by viewModel.favoriteCities.collectAsStateWithLifecycle()
    viewModel.getFavoriteCities()
    viewModel.fetchSettings()
    when (favoriteCities) {
        is Response.Error -> {
            Error()
        }

        Response.Loading -> {
            LoadingScreen()
        }

        is Response.Success -> {
            if ((favoriteCities as Response.Success).data.isEmpty())
                Empty(stringResource(R.string.your_favorites_list_is_currently_empty_start_adding_meals_you_love_so_you_can_easily_find_them_later))
            else {
                FavoritePage(
                    (favoriteCities as Response.Success).data,
                    temperatureUnit,
                    viewModel,
                    navToDetails,
                )
            }
        }
    }
}


@Composable
fun FavoritePage(
    initialData: List<Weather>,
    temperatureUnit: String,
    viewModel: FavoriteViewModel,
    navToDetails: (city: String, lang: Double, lat: Double) -> Unit

) {
    val favoriteList = initialData.toMutableList()
    val `snack-barHostState` = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(`snack-barHostState`, Modifier.padding(bottom = 140.dp))
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.deep_blue))
                .padding(paddingValues)
        ) {
            item {
                Spacer(Modifier.height(48.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.favorite_location),
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
            items(
                items = favoriteList,
                key = { item -> item.name }
            ) { fav ->
                SwipeToDeleteContainer(
                    item = fav,
                    onDelete = {
                        viewModel.deleteFavoriteCity(fav.name)
                    },
                    onRestore = { viewModel.addFavoriteCity(fav) },
                    snackBarHostState = `snack-barHostState`
                ) { weatherItem -> FavoriteCard(weatherItem, temperatureUnit, navToDetails) }
            }

            item {
                Spacer(Modifier.height(200.dp))
            }
        }
    }
}


@Composable
fun FavoriteCard(
    item: Weather,
    temperatureUnit: String,
    navyDestabilises: (String, Double, Double) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                navyDestabilises(item.name, item.coord.lat, item.coord.lon)
            },
    ) {
        Row(
            modifier = Modifier
                .background(getRandomGradient())
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(150.dp)
                    .padding(start = 8.dp),

                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${item.name}\n" +
                            (CountryHelper.getCountryName(item.sys.country) ?: ""),
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clickable(
                            onClick = {
                            }
                        ),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(item.dt.toLong())
                    ) + " ," + formatNumberBasedOnLanguage(
                        DateTimeHelper.getFormattedDate(
                            item.dt.toLong()
                        )
                    ),
                    color = Color.LightGray,
                    fontSize = 14.sp,
                )
            }
            Text(
                text = "${
                    transferUnit(temperatureUnit, item.main.temp).let {
                        formatNumberBasedOnLanguage(
                            it.toInt().toString()
                        )
                    }
                } ° $temperatureUnit",
                fontSize = 35.sp,
                color = Color.White
            )
            Image(
                painter = painterResource(id = getIcon(item.weather.firstOrNull()?.icon ?: "01d")),
                contentDescription = stringResource(R.string.weather_icon),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}


