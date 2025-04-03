package com.example.thundora.view.alarm.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thundora.R
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.services.AlarmScheduler
import com.example.thundora.ui.theme.DarkBlue
import com.example.thundora.view.alarm.alarmviewmodel.AlarmViewModel
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmButtonDialogSheet(
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
        Text(stringResource(R.string.add_new_alert), style = MaterialTheme.typography.titleLarge, color = Color.White)

        Text(stringResource(R.string.start_time), color = Color.White)
        ClickableOutlinedTextField(
            startDuration.value,
            interactionSource,
            { showStartTimePicker = true },
            Icons.Default.AccessTime
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.end_time1), color = Color.White)
        ClickableOutlinedTextField(
            endDuration.value,
            interactionSource2,
            { showEndTimePicker = true },
            Icons.Default.Timer
        )

        Spacer(modifier = Modifier.height(16.dp))
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
                            errorMessage =
                                context.getString(R.string.start_time_must_be_in_the_future)

                        }

                        else -> {
                            errorMessage = null
                            val alarmEntity = AlarmEntity(
                                id = System.currentTimeMillis().toInt(),
                                time = startTime,
                                duration = Duration.between(startTime, endTime)
                                    .toMinutes().toInt() * 60,

                                label = context.getString(R.string.thundora_app),
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