package com.example.thundora.view.home

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.thundora.model.pojos.view.Units
import com.example.thundora.model.utils.CountryHelper
import com.example.thundora.ui.theme.DarkBlue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.model.remotedatasource.ApiClient
import com.example.thundora.model.remotedatasource.RemoteDataSource
import com.example.thundora.model.repositary.Repository
import com.example.thundora.model.utils.dailyForecasts
import com.example.thundora.view.home.viewmodel.HomeFactory
import com.example.thundora.view.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(lat: Double, lon: Double, navToMaps: (latitude: Double, longitude: Double) -> Unit) {

    val viewModel: HomeViewModel =
        viewModel(factory = HomeFactory(Repository.getInstance(RemoteDataSource(ApiClient.weatherService))))


    val weatherState by viewModel.weather.observeAsState()
    val forecastState = viewModel.forecast.observeAsState()
    var dataPoints: MutableList<Float> = mutableListOf()
    var hourlyData: MutableList<String> = mutableListOf()
    LaunchedEffect(viewModel) {

        viewModel.getWeather(lat, lon, Units.METRIC.toString())
        viewModel.getForecast(lat, lon, Units.METRIC.toString())
    }

    dataPoints = forecastState.value?.list?.take(4)?.map {
        it.main.temp.toFloat()
    }?.toMutableList() ?: mutableListOf()
    hourlyData = forecastState.value?.list?.take(4)?.map {
        "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.dt * 1000L))}\n" +
                "${
                    it.main.temp
                } C"
    }?.toMutableList() ?: mutableListOf()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(
                color = colorResource(id = R.color.deep_blue),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        weatherState?.let { WeatherCard(it) { navToMaps(it.coord.lat, it.coord.lon) } }

        forecastState?.let { WeatherForecast(it.value) }
        Spacer(Modifier.height(4.dp))
        DayDisplay(forecastState.value)
        LineChartScreen(dataPoints, hourlyData)
        Spacer(Modifier.height(100.dp))

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherCard(weatherState: Weather?, navToMaps: () -> Unit) {
    val iconCode = weatherState?.weather?.firstOrNull()?.icon ?: "01d"
    val iconUrl = "https://openweathermap.org/img/wn/$iconCode.png"
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
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
                    text = "${
                        weatherState?.name ?: "--"
                    }\n${
                        CountryHelper.getCountryName(weatherState?.sys?.country.toString())
                            .takeIf { it.isNotBlank() } ?: "Unknown"
                    }",
                    color = colorResource(id = R.color.white),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable(
                            onClick = { navToMaps() }
                        ),
                    )
                Column {
                    Text(
                        text = SimpleDateFormat("EEEE", Locale.getDefault()).format(
                            Date(
                                (weatherState?.dt?.toLong()?.times(1000L))
                                    ?: System.currentTimeMillis()
                            )
                        ),
                        color = colorResource(id = R.color.white),
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = SimpleDateFormat("d MMM", Locale.getDefault()).format(
                            Date(
                                (weatherState?.dt?.toLong()?.times(1000L))
                                    ?: System.currentTimeMillis()
                            )
                        ),
                        color = colorResource(id = R.color.white),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            GlideImage(
                model = iconUrl,
                contentDescription = "Weather Icon",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "${weatherState?.main?.temp ?: "--"}°C",
                fontSize = 35.sp,
                color = colorResource(id = R.color.white)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = weatherState?.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "--",
                fontSize = 20.sp,
                color = colorResource(id = R.color.white)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfo(
                    value = weatherState?.wind?.speed.toString(),
                    unit = " km/h",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                    iconTint = Color.White
                )
                WeatherInfo(
                    value = weatherState?.main?.humidity.toString(),
                    unit = "%",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                    iconTint = Color.White
                )
                WeatherInfo(
                    value = weatherState?.main?.pressure.toString(),
                    unit = " hPa",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                    iconTint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfo(
                    value = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                        Date((weatherState?.sys?.sunrise ?: 0) * 1000L)
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunrise),
                    iconTint = Color.Yellow
                )

                WeatherInfo(
                    value = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                        Date((weatherState?.sys?.sunset ?: 0) * 1000L)
                    ),
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sunsunset),
                    iconTint = Color.Yellow
                )

                WeatherInfo(
                    value = weatherState?.main?.sea_level?.toString() ?: "--",
                    unit = "",
                    icon = ImageVector.vectorResource(id = R.drawable.sea),
                    iconTint = Color.White
                )
            }
        }
    }
}

@Composable
fun WeatherForecast(forecastState: Forecast?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Today", fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        forecastState?.list?.let { forecastList ->
            LazyRow {
                items(forecastList.take(8)) { item ->
                    HourlyDataRow(
                        time = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(Date(item.dt * 1000L)),
                        temp = "${item.main.temp.toInt()}°C",
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
        Text(text = temp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeatherInfo(
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp, color = Color.White),
    iconTint: Color
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
            style = textStyle
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
            .padding(
                vertical = 8.dp
            )

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
fun DayDisplay(forecastState: Forecast?) {
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
                        time = SimpleDateFormat(
                            "EEEE",
                            Locale.getDefault()
                        ).format(Date(item.dt * 1000L)),
                        date = SimpleDateFormat(
                            "dd/MM",
                            Locale.getDefault()
                        ).format(Date(item.dt * 1000L)),
                        temp = "${item.main.temp.toInt()}°C",
                        icon = item.weather.firstOrNull()?.icon ?: "01n"
                    )
                }
            }
        }
    }

    if (showBottomSheet && selectedItem != null) {
        WeatherForecastBottomSheet(weatherState = selectedItem) {
            showBottomSheet = false
            selectedItem = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun WeatherForecastBottomSheet(weatherState: Forecast.Item0?, onClose: () -> Unit) {
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
                    modifier = Modifier
                        .fillMaxWidth(),
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
                            text = weatherState?.weather?.get(0)?.description?.capitalize()
                                ?: "Unknown",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "${weatherState?.main?.temp?.toInt()}°C",
                        fontSize = 35.sp,
                        color = Color.White,

                        )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherInfo(
                            value = weatherState?.main?.humidity?.toString() ?: "--",
                            unit = "%",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                            iconTint = Color.White
                        )
                        WeatherInfo(
                            value = weatherState?.wind?.speed?.toString() ?: "--",
                            unit = " km/h",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                            iconTint = Color.White
                        )
                        WeatherInfo(
                            value = weatherState?.main?.pressure?.toString() ?: "--",
                            unit = " hPa",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
                            iconTint = Color.White
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Displaying Cloud Coverage and Rain
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherInfo(
                            value = weatherState?.clouds?.all?.toString() ?: "--",
                            unit = "%",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
                            iconTint = Color.White
                        )
                        WeatherInfo(
                            value = weatherState?.rain?.`3h`?.toString() ?: "NoRain",
                            unit = "",
                            icon = ImageVector.vectorResource(id = R.drawable.ic_humidity),
                            iconTint = Color.White
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
                        Text("Close", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
    lineColor: Color = colorResource(id = R.color.white),
    lineWidth: Float = 4f
) {
    if (dataPoints.isEmpty()) return

    val maxValue = dataPoints.maxOrNull() ?: 1f


    Box(modifier = modifier) {
        Canvas(modifier = modifier) {
            val widthStep = size.width / (dataPoints.size - 1) // X position spacing
            val path = Path().apply {
                moveTo(0f, size.height - (dataPoints[0] / maxValue) * size.height)
                dataPoints.forEachIndexed { index, dataPoint ->
                    lineTo(
                        index * size.width / (dataPoints.size - 1),
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

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                descriptions.forEach { description ->
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LineChartScreen(dataPoints: MutableList<Float>, data: MutableList<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LineChart(
            dataPoints = dataPoints,
            descriptions = data,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
    }


}

