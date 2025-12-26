package com.kito.ui.components

import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.animation.core.Easing
import androidx.core.view.animation.PathInterpolatorCompat

object ExpressiveEasing {
    val Emphasized: Easing = Easing {
        val pathInterpolator = PathInterpolatorCompat.create(
            Path().apply {
                moveTo(0f, 0f)
                cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
                cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
            } as Path
        )
        pathInterpolator.getInterpolation(it)
    }

    // ⏩ Emphasized Accelerate: starts slow, ends fast
    val EmphasizedAccelerate: Easing = Easing {
        PathInterpolator(0.3f, 0f, 0.8f, 0.15f).getInterpolation(it)
    }

    // 🛑 Emphasized Decelerate: starts fast, ends slow
    val EmphasizedDecelerate: Easing = Easing {
        PathInterpolator(0.05f, 0.7f, 0.1f, 1f).getInterpolation(it)
    }
}