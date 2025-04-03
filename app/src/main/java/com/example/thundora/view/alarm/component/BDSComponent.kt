package com.example.thundora.view.alarm.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.thundora.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime


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
        timepicker(
            initialTime = pickedTime,
            is24HourClock = true,
            title = stringResource(R.string.pick_a_time)
        ) { pickedTime = it }
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