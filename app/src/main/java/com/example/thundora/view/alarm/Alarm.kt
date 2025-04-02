package com.example.thundora.view.alarm


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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarDuration
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarHost
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarHostState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarResult
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thundora.R
import com.example.thundora.data.local.source.LocalDataSource
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.Response
import com.example.thundora.data.remote.api.ApiClient
import com.example.thundora.data.remote.remotedatasource.RemoteDataSource
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.services.AlarmScheduler
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.ui.theme.DarkBlue
import com.example.thundora.view.alarm.alarmviewmodel.AlarmFactory
import com.example.thundora.view.alarm.alarmviewmodel.AlarmViewModel
import com.example.thundora.view.favorite.SwipeToDeleteContainer
import com.example.thundora.view.components.AlarmLottie
import com.example.thundora.view.components.Empty
import com.example.thundora.view.components.LoadingScreen
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
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
                    PrintAlarms(alarmList, viewModel)
                }

                if (showBottomSheet) {
                    SettingsBDS(
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

                Empty()

                if (showBottomSheet) {
                    SettingsBDS(
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrintAlarms(data: List<AlarmEntity>, viewModel: AlarmViewModel) {
    val ctx = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val alarmScheduler = AlarmScheduler(LocalContext.current)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.deep_blue))
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
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.alarm_schedule),
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
            items(data) { fav ->
                SwipeToDeleteContainer(
                    item = fav,
                    onDelete = {
                        alarmScheduler.cancelAlarm(fav.id)
                        viewModel.deleteAlarmById(fav.id)
                        coroutineScope.launch {
                            val result = snackBarHostState.showSnackbar(
                                message = ctx.getString(R.string.alarm_deleted),
                                actionLabel = ctx.getString(R.string.undo),
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.addAlarm(fav)
                            }
                        }
                    },
                    onRestore = { viewModel.addAlarm(fav) },
                    snackBarHostState = snackBarHostState
                ) { AlarmCard(time = fav.time.toString()) }
            }
            item {
                Spacer(Modifier.height(200.dp))
            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 200.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsBDS(
    onClose: () -> Unit,
    onOKey: (LocalTime, LocalTime) -> Unit,
    startDuration: MutableState<String>,
    endDuration: MutableState<String>,
    viewModel: AlarmViewModel
) {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onClose,
            sheetState = sheetState,
            containerColor = DarkBlue,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            modifier = Modifier.wrapContentHeight()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkBlue),
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                AddNewAlertBottomSheet(onClose, onOKey, startDuration, endDuration, viewModel)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddNewAlertBottomSheet(
    onClose: () -> Unit,
    onOKey: (LocalTime, LocalTime) -> Unit,
    startDuration: MutableState<String>,
    endDuration: MutableState<String>,
    alarmViewModel: AlarmViewModel
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableStateOf(context.getString(R.string.alarm)) }
    val interactionSource = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(LocalTime.now().plusMinutes(1)) }
    var endTime by remember { mutableStateOf(LocalTime.now().plusMinutes(2)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val alarmScheduler = AlarmScheduler(context)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                showStartTimePicker = true
            }
        }
    }
    LaunchedEffect(interactionSource2) {
        interactionSource2.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                showEndTimePicker = true
            }
        }
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Add New Alert", style = MaterialTheme.typography.titleLarge, color = Color.White)

        Text("Start Time", color = Color.White)
        ClickableOutlinedTextField(
            startDuration.value,
            interactionSource,
            { showStartTimePicker = true },
            Icons.Default.AccessTime
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("End Time", color = Color.White)
        ClickableOutlinedTextField(
            endDuration.value,
            interactionSource2,
            { showEndTimePicker = true },
            Icons.Default.Timer
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Notify me by", fontSize = 14.sp, color = Color.White)
            Row {
                RadioButton(
                    selected = selectedOption == context.getString(R.string.alarm),
                    onClick = { selectedOption = context.getString(R.string.alarm) })
                Text("Alarm", color = Color.White)

                Spacer(Modifier.width(8.dp))

                RadioButton(
                    selected = selectedOption == context.getString(R.string.notification),
                    onClick = { selectedOption = context.getString(R.string.notification) })
                Text(context.getString(R.string.notification), color = Color.White)
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    when {
                        startTime.isBefore(LocalTime.now()) -> {
                            errorMessage =
                                context.getString(R.string.start_time_must_be_in_the_future)
                        }

                        endTime.isBefore(startTime) -> {
                            errorMessage = context.getString(R.string.start_time_must_be_in_the_future)

                        }

                        else -> {
                            errorMessage = null
                            val alarmEntity = AlarmEntity(
                                id = System.currentTimeMillis().toInt(),
                                time = startTime,
                                duration = Duration.between(startTime, endTime)
                                    .toMinutes().toInt() * 60,

                                label = context.getString(R.string.thundora_app)
                            )
                            alarmViewModel.addAlarm(alarmEntity)
                            alarmScheduler.scheduleAlarm(alarmEntity)
                            onOKey(startTime, endTime)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.save), color = Color.White)
            }

            Spacer(Modifier.width(16.dp))

            Button(
                onClick = { onClose() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.cancel), color = Color.White)
            }
        }
    }

    if (showStartTimePicker) {
        TimePickerExample(onDismiss = { showStartTimePicker = false }, selectedTime = startTime) {
            startTime = it
            startDuration.value = it.format(DateTimeFormatter.ofPattern("hh:mm a"))
        }
    }

    if (showEndTimePicker) {
        TimePickerExample(onDismiss = { showEndTimePicker = false }, selectedTime = endTime) {
            endTime = it
            endDuration.value = it.format(DateTimeFormatter.ofPattern("hh:mm a"))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerExample(
    onDismiss: () -> Unit,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    val dialogState = rememberMaterialDialogState()
    var pickedTime by remember { mutableStateOf(selectedTime) }

    LaunchedEffect(Unit) { dialogState.show() }

    MaterialDialog(dialogState = dialogState, buttons = {
        positiveButton(stringResource(R.string.ok)) {
            onTimeSelected(pickedTime)
            onDismiss()
        }
        negativeButton(stringResource(R.string.cancel)) { onDismiss() }
    }) {
        timepicker(initialTime = pickedTime, title = stringResource(R.string.pick_a_time)) { pickedTime = it }
    }
}


@Composable
fun ClickableOutlinedTextField(
    value: String,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        interactionSource = interactionSource,
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Color.White) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            disabledTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = Color.White
        ),
    )
}



@Composable
fun AlarmCard(time: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.dark_blue)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.starting_time) + time.substringBefore(","),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.end_time) +time.substringAfter(","),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.offset(x = 12.dp)
            ) {
                AlarmLottie()
            }
        }
    }
}

