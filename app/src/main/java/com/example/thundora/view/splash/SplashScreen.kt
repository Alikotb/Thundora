package com.example.thundora.view.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.thundora.R
import kotlinx.coroutines.delay

@Composable
fun Splash(flag: MutableState<Boolean>, navToHome: () -> Unit) {
    flag.value=false
    val scale = remember { Animatable(0f) }
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash2))

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        delay(3500L)
        navToHome()

    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(R.color.blue_1200),
                        colorResource(R.color.blue_1100),
                        colorResource(R.color.blue_1000),
                        colorResource(R.color.blue_900)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
    ) {
        LottieAnimation(
            composition = composition.value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize(),
            renderMode = RenderMode.AUTOMATIC
        )
    }
}
