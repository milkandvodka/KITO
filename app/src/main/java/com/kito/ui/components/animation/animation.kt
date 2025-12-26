package com.kito.ui.components.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun PageNotFoundAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("page_not_found.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )
}
@Composable
fun SlothSleepingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("sloth_sleeping.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )
}