package com.esandi.healthmate.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring

/**
 * Shared motion settings so every animation in HealthMate feels the same: calm, physics-based
 * springs that settle without wobbling, similar to iOS. One place to tune the whole app's motion.
 */
object HealthMateMotion {
    /** Default for values that change after a tap (levels, arcs, sizes): smooth, almost no overshoot. */
    fun <T> gentle(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.85f, stiffness = 170f)

    /** Slightly quicker, for small UI pieces such as the segmented-control thumb. */
    fun <T> snappy(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.86f, stiffness = 420f)

    /** Slow and soft, for things that "walk" or fill over time (footprints). */
    fun <T> slow(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.95f, stiffness = 60f)

    // Durations of the gentle looping effects (milliseconds)
    const val WAVE_LOOP = 3200
    const val BUBBLE_LOOP = 4200
    const val FLAME_LOOP = 2400
    const val TWINKLE_LOOP = 3600
    const val BREATHE_LOOP = 2400
}
