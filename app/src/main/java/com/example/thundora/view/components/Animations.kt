package com.example.thundora.view.components

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.thundora.R


@Composable
fun LoadingScreen() {
    val scale = remember { Animatable(0f) }
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.camel))
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )

    }
    Box(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.deep_blue)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition.value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(150.dp),
            renderMode = RenderMode.AUTOMATIC
        )
    }
}

@Preview
@Composable
fun Error() {
    val scale = remember { Animatable(0f) }
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )

    }

    Box(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white)),
        contentAlignment = Alignment.Center
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        val lottieSize = screenWidth * 0.7f

        LottieAnimation(
            composition = composition.value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(lottieSize),
            renderMode = RenderMode.AUTOMATIC
        )
    }
}


@Composable
fun Empty() {
    val scale = remember { Animatable(0f) }
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.panda))
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )

    }

    Box(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.deep_blue)),
        contentAlignment = Alignment.Center
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        val lottieSize = screenWidth * 0.7f

        LottieAnimation(
            composition = composition.value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(lottieSize),
            renderMode = RenderMode.AUTOMATIC
        )
    }
}


@Composable
fun AlarmLottie() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.alaram))


    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .size(54.dp)
            .offset(x = 8.dp),
        contentScale = ContentScale.Crop,
        renderMode = RenderMode.AUTOMATIC
    )
}
