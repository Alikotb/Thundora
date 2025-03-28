package com.example.thundora.view.favorite

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarDuration
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarHost
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarHostState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.example.thundora.R
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import com.example.thundora.view.favorite.viewModel.FavoriteFactory
import com.example.thundora.view.favorite.viewModel.FavoriteViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.utils.CountryHelper
import com.example.thundora.model.utils.DateTimeHelper
import com.example.thundora.model.utils.formatNumberBasedOnLanguage
import com.example.thundora.model.utils.getDegree
import com.example.thundora.model.utils.getLanguage
import com.example.thundora.model.utils.transferUnit
import com.example.thundora.view.utilies.LoadingScreen
import com.example.thundora.view.utilies.getIcon
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoriteScreen(
    floatingFlag: MutableState<Boolean>,
    navToDetails: (city: String, lang: Double, lat: Double) -> Unit
) {
    floatingFlag.value = true
    val viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteFactory(
            Repository.getInstance(
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

    val language by viewModel.language.collectAsStateWithLifecycle()
    val temp by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val temperatureUnit = getDegree(getLanguage(language), temp)
    val favoriteCities by viewModel.favoriteCities.collectAsStateWithLifecycle()
    viewModel.getFavoriteCities()
    when (favoriteCities) {
        is Response.Error -> {
            Text(text = (favoriteCities as Response.Error).message)
        }

        Response.Loading -> {
            LoadingScreen()
        }
        is Response.Success -> {
            if ((favoriteCities as Response.Success).data.isEmpty())
                Text(text = stringResource(R.string.no_favorite_cities))
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
    navTodestails: (String, Double, Double) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.dark_blue)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                navTodestails(item.name, item.coord.lat, item.coord.lon)
            },
        ) {
        Row(
            modifier = Modifier
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
                            (CountryHelper.getCountryName(item.sys.country)?:""),
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

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    onRestore: (T) -> Unit,
    snackBarHostState: SnackbarHostState,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    val currentItem by rememberUpdatedState(item)

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    val context =LocalContext.current
    LaunchedEffect(isRemoved, currentItem) {
        if (isRemoved) {
            val result = snackBarHostState.showSnackbar(
                message = context.getString(R.string.item_deleted),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                onRestore(currentItem)
                isRemoved = false

                state.snapTo(SwipeToDismissBoxValue.Settled)
            } else {
                delay(animationDuration.toLong())
                onDelete(currentItem)
            }
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        enter = expandVertically(
            animationSpec = tween(durationMillis = animationDuration),
            expandFrom = Alignment.Top
        ) + fadeIn(),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        isRemoved = true
                        true
                    } else {
                        false
                    }
                }
            ),
            backgroundContent = { },
            enableDismissFromStartToEnd = false
        ) {
            content(currentItem)
        }
    }
}
