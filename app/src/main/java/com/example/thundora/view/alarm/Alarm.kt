package com.example.thundora.view.alarm


//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
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
import com.example.thundora.view.alarm.alarmviewmodel.AlarmFactory
import com.example.thundora.view.alarm.alarmviewmodel.AlarmViewModel
import com.example.thundora.view.alarm.component.AlarmButtonDialogSheet
import com.example.thundora.view.alarm.component.AlarmList
import com.example.thundora.view.components.Empty
import com.example.thundora.view.components.Error
import com.example.thundora.view.components.LoadingScreen
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(
    floatingFlag: MutableState<Boolean>,
    fabIcon: MutableState<ImageVector>,
    fabAction: MutableState<() -> Unit>
) {
    val ctx = LocalContext.current
    val startDuration = remember { mutableStateOf(ctx.getString(R.string.start_duration)) }
    val endDuration = remember { mutableStateOf(ctx.getString(R.string.end_duration)) }
    val dayAndTime = remember { mutableStateOf("${startDuration.value}, ${endDuration.value}") }
    val viewModel: AlarmViewModel = viewModel(
        factory = AlarmFactory(
            RepositoryImpl.getInstance(
                RemoteDataSource(ApiClient.weatherService),
                LocalDataSource(
                    WeatherDataBase.getInstance(LocalContext.current).getForecastDao(),
                    SharedPreference.getInstance()
                )
            ),
            context = LocalContext.current
        )
    )
    val alarms by viewModel.allAlarms.collectAsStateWithLifecycle()

    floatingFlag.value = true
    fabIcon.value = Icons.Default.Notifications
    var showBottomSheet by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->

    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    ctx, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fabAction.value = {

        showBottomSheet = true
    }
    when (alarms) {
        is Response.Error -> {
            Error()
        }

        Response.Loading -> {
            LoadingScreen()
        }

        is Response.Success -> {
            val alarmList = (alarms as Response.Success).data
            if (alarmList.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.deep_blue)),
                ) {
                    AlarmList(alarmList, viewModel,startDuration,endDuration)
                }

                if (showBottomSheet) {
                    AlarmButtonDialogSheet(
                        onClose = {
                            showBottomSheet = false
                            floatingFlag.value = false
                        },
                        onOKey = { startTime, endTime ->
                            showBottomSheet = false
                            floatingFlag.value = false
                            startDuration.value =
                                startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            endDuration.value =
                                endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            dayAndTime.value = "${startDuration.value}, ${endDuration.value}"
                        },
                        startDuration = startDuration,
                        endDuration = endDuration,
                        viewModel = viewModel
                    )
                }
            } else {

                Empty(stringResource(R.string.you_don_t_have_any_alarms_set_yet_add_one_now_to_stay_on_track_and_never_miss_an_important_moment))

                if (showBottomSheet) {
                    AlarmButtonDialogSheet(
                        onClose = {
                            showBottomSheet = false
                            floatingFlag.value = false
                        },
                        onOKey = { startTime, endTime ->
                            showBottomSheet = false
                            floatingFlag.value = false
                            startDuration.value =
                                startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            endDuration.value =
                                endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            dayAndTime.value = "${startDuration.value}, ${endDuration.value}"
                        },
                        startDuration = startDuration,
                        endDuration = endDuration,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}




