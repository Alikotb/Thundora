package com.example.thundora.view.alarm.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thundora.R
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.services.AlarmScheduler
import com.example.thundora.view.alarm.alarmviewmodel.AlarmViewModel
import com.example.thundora.view.components.AlarmLottie
import com.example.thundora.view.components.SwipeToDeleteContainer
import com.example.thundora.view.components.getRandomGradient
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmList(
    data: List<AlarmEntity>,
    viewModel: AlarmViewModel,
    startDuration: MutableState<String>,
    endDuration: MutableState<String>
) {
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
                ) { AlarmCard(startDuration,endDuration) }
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

@Composable
fun AlarmCard(
    startDuration: MutableState<String>,
    endDuration: MutableState<String>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(getRandomGradient())
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.start_at) +startDuration.value+ stringResource(R.string.to) + stringResource(
                        R.string.end_at
                    )+endDuration.value,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

            }
            AlarmLottie()
        }
    }
}
