@file:Suppress("CAST_NEVER_SUCCEEDS")

package com.example.thundora.view.home
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.thundora.R
import com.example.thundora.model.pojos.api.Forecast
import com.example.thundora.model.pojos.api.Weather
import com.example.thundora.model.utils.CountryHelper
import com.example.thundora.ui.theme.DarkBlue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.model.localdatasource.LocalDataSource
import com.example.thundora.model.localdatasource.WeatherDataBase
import com.example.thundora.model.pojos.api.ApiResponse
import com.example.thundora.model.pojos.api.Response
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.sharedpreference.SharedPreference
import com.example.thundora.model.utils.DateTimeHelper
import com.example.thundora.model.utils.dailyForecasts
import com.example.thundora.model.utils.formatNumberBasedOnLanguage
import com.example.thundora.model.utils.getDegree
import com.example.thundora.model.utils.getLanguage
import com.example.thundora.model.utils.getWindSpeed
import com.example.thundora.view.home.viewmodel.HomeFactory
import com.example.thundora.view.home.viewmodel.HomeViewModel
import com.example.thundora.view.utilies.LoadingScreen
import com.example.thundora.view.utilies.getBackgroundColor
import com.example.thundora.view.utilies.getIcon
import com.example.thundora.view.utilies.getWeatherColors
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    flag: MutableState<Boolean>,
    floatingFlag: MutableState<Boolean>,
    navToMaps: (latitude: Double, longitude: Double) -> Unit
) {
    floatingFlag.value = false
    flag.value = true

    val viewModel: HomeViewModel=viewModel(
        factory = HomeFactory(
            Repository.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(LocalContext.current).getForecastDao(),
                    SharedPreference.getInstance()
                )
            )
        )
    )

    val language by viewModel.language.collectAsStateWithLifecycle()
    val temp by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val temperatureUnit =getDegree(getLanguage(language),temp)
    val wind by viewModel.units.collectAsStateWithLifecycle()
    val windSpeedUnit  =getWindSpeed(getLanguage(language),wind)

    val apiForecast by viewModel.forecast.collectAsStateWithLifecycle()
    val error by viewModel.message.collectAsStateWithLifecycle()

    var dataPoints: MutableList<Float> = mutableListOf()
    var hourlyData: MutableList<String> = mutableListOf()


    when (apiForecast) {
        is Response.Success -> {
            Log.d("asd", "HomeScreen: ${windSpeedUnit}")
            dataPoints = (apiForecast as Response.Success).data.forecast.list.take(4)
                .filter { DateTimeHelper.isToday(it.dt.toLong()) }
                .map { it.main.temp.toFloat() }
                .toMutableList()

            hourlyData = (apiForecast as Response.Success).data.forecast.list.take(4)
                .filter { DateTimeHelper.isToday(it.dt.toLong()) }
                .map {
                    "${
                        formatNumberBasedOnLanguage(
                            DateTimeHelper.formatUnixTimestamp(it.dt.toLong(), "HH:mm"),
                        )
                    }\n\n" +
                            "${
                                formatNumberBasedOnLanguage(
                                    it.main.temp.toInt().toString(),
                                )
                            } ° $temperatureUnit"
                }.toMutableList()

            Home(
                (apiForecast as Response.Success).data,
                flag,
                navToMaps,
                dataPoints,
                hourlyData,
                temperatureUnit,
                windSpeedUnit
            )
        }

        is Response.Error -> {
            Text(text = (error as Response.Error).message)
            Log.d("ali", "HomeScreen: error state")

        }

        is Response.Loading -> {
            Log.d("ali", "HomeScreen: Loading state")

            Box(
                Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            ) {
                LoadingScreen()
            }
        }
    }
}


@Composable
fun Home(
    apiForecast: ApiResponse,
    flag: MutableState<Boolean>,
    navToMaps: (latitude: Double, longitude: Double) -> Unit,
    dataPoints: MutableList<Float>,
    hourlyData: MutableList<String>,
    temperatureUnit: String,
    speedUnit: String,
) {
    val todayForecastList =
        apiForecast.forecast.list.filter { DateTimeHelper.isToday(it.dt.toLong()) }
    val iconCode = apiForecast.weather.weather.firstOrNull()?.icon ?: "01d"
    val (backgroundColor, textColor) = getWeatherColors(iconCode)
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        apiForecast.weather.let {
            WeatherCard(
                it,
                flag,
                temperatureUnit,
                speedUnit,
            ) { navToMaps(it.coord.lat, it.coord.lon) }
        }
        LineChartScreen(
            dataPoints = dataPoints,
            data = hourlyData,
            isDayTime = DateTimeHelper.isDayTime(
                apiForecast.forecast.city.sunrise.toLong(),
                apiForecast.forecast.city.sunset.toLong(),
                System.currentTimeMillis() / 1000
            ),
            weatherCondition = apiForecast.weather.weather.firstOrNull()?.main ?: "Clear",
            textColor,
        )
        WeatherForecast(todayForecastList, temperatureUnit)
        Spacer(Modifier.height(4.dp))
        DayDisplay(apiForecast.forecast, temperatureUnit)

        Spacer(Modifier.height(100.dp))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherCard(
    weatherState: Weather?,
    flag: MutableState<Boolean>,
    temperatureUnit: String,
    speedUnit: String,
    navToMaps: () -> Unit
) {
    val iconCode = weatherState?.weather?.firstOrNull()?.icon ?: "01d"
    val (backgroundColor, textColor) = getWeatherColors(iconCode)

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${weatherState?.name ?: "--"}\n" +
                            (CountryHelper.getCountryName(weatherState?.sys?.country.toString())),
                    color = textColor,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable(
                            onClick = {
                                flag.value = false
                                navToMaps()
                            }
                        ),
                )
                Column {
                    Text(
                        text = formatNumberBasedOnLanguage(
                            DateTimeHelper.getDayOfWeek(weatherState?.dt?.toLong())),
                        color = textColor,
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = formatNumberBasedOnLanguage(
                            DateTimeHelper.getFormattedDate(
                                weatherState?.dt?.toLong()
                            )
                        ),
                        color = textColor,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(getIcon (icon=iconCode)),
                contentDescription = stringResource(R.string.weather_icon),
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "${
                    weatherState?.main?.temp?.let {
                        formatNumberBasedOnLanguage(
                            it.toString())
                    } ?: "--"
                } ° $temperatureUnit",
                fontSize = 35.sp,
                color = textColor
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = weatherState?.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "--",
                fontSize = 20.sp,
                color = textColor
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfo(
                    value = weatherState?.wind?.speed.let {
                        formatNumberBasedOnLanguage(
                            it.toString())
                    },
                    unit = " $speedUnit",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = weatherState?.main?.humidity.let {
                        formatNumberBasedOnLanguage(
                            it.toString())
                    },
                    unit = "%",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        weatherState?.main?.pressure.toString()),
                    unit = " hPa",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                    iconTint = textColor,
                    color = textColor

                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(
                            weatherState?.sys?.sunrise?.toLong(),
                            "hh:mm a"
                        )),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunrise),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = formatNumberBasedOnLanguage(
                        DateTimeHelper.formatUnixTimestamp(
                            weatherState?.sys?.sunset?.toLong(),
                            "hh:mm a"
                        )
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunsunset),
                    iconTint = textColor,
                    color = textColor

                )
                WeatherInfo(
                    value = weatherState?.main?.sea_level?.let {
                        formatNumberBasedOnLanguage(
                            it.toString())
                    } ?: "--",
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sea),
                    iconTint = textColor,
                    color = textColor
                )
            }
        }
    }
}


@Composable
fun WeatherForecast(forecastList: List<Forecast.Item0>, temperatureUnit: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.today), fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        if (forecastList.isEmpty()) {
            Text(
                text = "......",
                color = Color.White,
                fontSize = 16.sp
            )
        } else {
            LazyRow {
                items(forecastList) { item ->
                    HourlyDataRow(
                        time = formatNumberBasedOnLanguage(
                            DateTimeHelper.formatUnixTimestamp(
                                item.dt.toLong(),
                                "HH:mm"
                            )
                        ),
                        temp = "${item.main.temp.toInt()} ° $temperatureUnit",
                        icon = item.weather.firstOrNull()?.icon ?: "01n"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HourlyDataRow(time: String, temp: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(100.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(text = time, color = Color.LightGray)
        GlideImage(
            model = "https://openweathermap.org/img/wn/$icon.png",
            contentDescription = "Weather Icon",
            modifier = Modifier.size(40.dp)
        )
        Text(text = formatNumberBasedOnLanguage(temp), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeatherInfo(
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconTint: Color,
    color: Color
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(25.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$value$unit",
            style = TextStyle(fontSize = 16.sp, color = color)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DayDisplayRow(time: String, date: String, temp: String, icon: String) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.deep_blue))
            .padding(vertical = 8.dp)
    ) {
        val (descripyion, image, tempereture) = createRefs()
        Column(
            modifier = Modifier
                .constrainAs(descripyion) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, 16.dp)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Text(
                text = time,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.width(110.dp)
            )
            Text(
                text = date,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
        Text(
            text = temp,
            color = Color.White,
            modifier = Modifier
                .constrainAs(tempereture) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)

                }
        )
        GlideImage(
            model = "https://openweathermap.org/img/wn/$icon.png",
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(40.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, 16.dp)
                    bottom.linkTo(parent.bottom)
                }
                .size(48.dp)
        )
    }
}

@Composable
fun DayDisplay(forecastState: Forecast?, temperatureUnit: String) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Forecast.Item0?>(null) }

    forecastState?.let { forecast ->
        val forecastMap = forecast.dailyForecasts()
        forecastMap.forEach { (_, itemList) ->
            val firstItem = itemList.firstOrNull()
            firstItem?.let { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedItem = item
                            showBottomSheet = true
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DayDisplayRow(
                        time = formatNumberBasedOnLanguage(
                            DateTimeHelper.getDayOfWeek(item.dt.toLong())
                        ),
                        date = formatNumberBasedOnLanguage(
                            DateTimeHelper.formatUnixTimestamp(
                                item.dt.toLong(),
                                "dd/mm"
                            )
                        ),
                        temp = "${item.main.temp.toInt()} ° $temperatureUnit",
                        icon = item.weather.firstOrNull()?.icon ?: "01n"
                    )
                }
            }
        }
    }

    if (showBottomSheet && selectedItem != null) {
        WeatherForecastBottomSheet(weatherState = selectedItem, temperatureUnit) {
            showBottomSheet = false
            selectedItem = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun WeatherForecastBottomSheet(
    weatherState: Forecast.Item0?,
    temperatureUnit: String,
    onClose: () -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onClose() },
            sheetState = sheetState,
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkBlue),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
                                .format(Date(weatherState?.dt?.times(1000L) ?: 0L)),
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = weatherState?.weather?.get(0)?.description?.capitalize(Locale.ROOT)
                                ?: R.string.unknown.toString(),
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "${
                            formatNumberBasedOnLanguage(
                                (weatherState?.main?.temp?.toInt() ?: 0).toString()
                            )
                        } ° $temperatureUnit",
                        fontSize = 35.sp,
                        color = Color.White,

                        )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.main?.humidity?.toString() ?: "--"
                            ),
                            unit = "%",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                            iconTint = Color.White,
                            color = Color.White
                        )
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.wind?.speed?.toString() ?: "--"
                            ),
                            unit = " km/h",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                            iconTint = Color.White,
                            color = Color.White
                        )
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.main?.pressure?.toString() ?: "--"
                            ),
                            unit = " hPa",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                            iconTint = Color.White,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.clouds?.all?.toString() ?: "--"
                            ),
                            unit = "%",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                            iconTint = Color.White,
                            color = Color.White
                        )
                        WeatherInfo(
                            value = formatNumberBasedOnLanguage(
                                weatherState?.rain?.`3h`?.toString() ?: "NoRain"
                            ),
                            unit = "",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                            iconTint = Color.White,
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showBottomSheet = false
                            }
                            onClose()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.close),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LineChart(
    dataPoints: List<Float>,
    descriptions: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White,
    lineWidth: Float = 4f,
) {
    if (dataPoints.isEmpty()) return
    val maxValue = dataPoints.maxOrNull() ?: 1f
    Box(modifier = modifier) {
        Canvas(modifier = modifier) {
            val widthStep = size.width / (dataPoints.size - 1)
            val path = Path().apply {
                moveTo(0f, size.height - (dataPoints[0] / maxValue) * size.height)
                dataPoints.forEachIndexed { index, dataPoint ->
                    lineTo(
                        index * widthStep,
                        size.height - (dataPoint / maxValue) * size.height
                    )
                }
            }
            drawPath(path, color = lineColor, style = Stroke(width = lineWidth))
            dataPoints.forEachIndexed { index, dataPoint ->
                val x = index * widthStep
                val y = size.height - (dataPoint / maxValue) * size.height
                drawCircle(color = Color.Red, radius = 6f, center = Offset(x, y))
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                descriptions.forEach { description ->
                    Text(
                        text = "$description\n",
                        fontSize = 14.sp,
                        color = lineColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(64.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LineChartScreen(
    dataPoints: MutableList<Float>,
    data: MutableList<String>,
    isDayTime: Boolean,
    weatherCondition: String,
    color: Color,
    ) {
    val backgroundColor = getBackgroundColor(isDayTime, weatherCondition)
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Temperature", fontSize = 20.sp, color = color)
            Spacer(modifier = Modifier.height(16.dp))
            LineChart(
                dataPoints = dataPoints,
                descriptions = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                lineColor = color,
            )
        }
    }
}
