package com.esandi.healthmate.feature.record

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.theme.Amber
import com.esandi.healthmate.ui.theme.Coral
import com.esandi.healthmate.ui.theme.HealthMateMotion
import com.esandi.healthmate.ui.theme.Orange
import com.esandi.healthmate.ui.theme.Tangerine
import com.esandi.healthmate.util.Formatters
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * Small animated illustrations for the Add / Edit Record form (one per metric), drawn with
 * Compose Canvas - no image files or animation libraries. Motion follows an iOS feel:
 * physics-based springs that settle without wobbling (HealthMateMotion), slow and subtle loops,
 * and nothing that moves constantly for no reason. Everything respects Android's
 * "Remove animations" accessibility setting: loops stop and values change instantly.
 */

/** True when the user has turned animations off in Android's accessibility settings. */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/** A value looping smoothly from 0 to 1 forever (waves, flicker, twinkle); stays 0 with reduced motion. */
@Composable
private fun loopingPhase(durationMillis: Int, reducedMotion: Boolean, label: String): Float {
    if (reducedMotion) return 0f
    val transition = rememberInfiniteTransition(label = label)
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis, easing = LinearEasing)),
        label = label,
    )
    return phase
}

private const val TWO_PI = (2 * PI).toFloat()

// ---------------------------------------------------------------------------------------------
// Slider with a single-colour gradient (soft tint -> full metric colour), for Steps and Calories
// ---------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val inactive = MaterialTheme.colorScheme.surfaceContainerHighest
    val span = (valueRange.endInclusive - valueRange.start).takeIf { it > 0f } ?: 1f
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier,
        colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color, inactiveTrackColor = inactive),
        track = {
            Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                val radius = CornerRadius(size.height / 2)
                drawRoundRect(inactive, cornerRadius = radius)
                if (fraction > 0f) {
                    val activeWidth = size.width * fraction
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0.35f), color), startX = 0f, endX = activeWidth),
                        size = Size(activeWidth, size.height),
                        cornerRadius = radius,
                    )
                }
            }
        },
    )
}

// ---------------------------------------------------------------------------------------------
// Water: a bottle that fills smoothly; the surface sloshes while the level moves, then calms
// ---------------------------------------------------------------------------------------------

/**
 * @param fillFraction how full the bottle is (0..1). The bottle holds 1 L and shows this drink only,
 *   so it starts empty and 250 ml fills a quarter.
 */
@Composable
fun WaterBottle(
    fillFraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    width: Dp = 92.dp,
    height: Dp = 148.dp,
) {
    val reduced = rememberReducedMotion()
    val target = fillFraction.coerceIn(0f, 1f)
    val level by animateFloatAsState(
        targetValue = target,
        animationSpec = if (reduced) snap() else HealthMateMotion.gentle(),
        label = "bottleLevel",
    )
    val wave = loopingPhase(HealthMateMotion.WAVE_LOOP, reduced, "bottleWave")
    val bubbles = loopingPhase(HealthMateMotion.BUBBLE_LOOP, reduced, "bottleBubbles")
    val glass = MaterialTheme.colorScheme.surfaceContainerHigh
    val outline = MaterialTheme.colorScheme.outline

    Canvas(
        modifier
            .size(width, height)
            .semantics { contentDescription = "Water bottle, ${(target * 100).roundToInt()}% full" },
    ) {
        val w = size.width
        val h = size.height
        val capH = h * 0.07f
        val neckW = w * 0.40f
        val bodyTop = capH + h * 0.12f
        val bodyRadius = w * 0.24f

        val neck = Path().apply {
            addRoundRect(RoundRect(left = (w - neckW) / 2, top = capH, right = (w + neckW) / 2, bottom = bodyTop + bodyRadius, cornerRadius = CornerRadius(neckW * 0.18f)))
        }
        val body = Path().apply {
            addRoundRect(RoundRect(left = 0f, top = bodyTop, right = w, bottom = h, cornerRadius = CornerRadius(bodyRadius)))
        }
        val bottle = Path().apply { op(neck, body, PathOperation.Union) }

        drawRoundRect(color, topLeft = Offset((w - neckW * 1.15f) / 2, 0f), size = Size(neckW * 1.15f, capH * 1.1f), cornerRadius = CornerRadius(capH * 0.4f))
        drawPath(bottle, glass)

        clipPath(bottle) {
            val waterTop = h - (h - capH) * level
            // A calm ripple at rest; the surface "sloshes" more while the level is still moving.
            val moving = min(abs(target - level) * 6f, 1f)
            val edgeFade = if (level in 0.01f..0.98f) 1f else 0f
            val amplitude = (1.5f + 6f * moving).dp.toPx() * edgeFade
            drawPath(wavePath(w, h, waterTop, amplitude, wave + 0.5f), color.copy(alpha = 0.30f))
            drawPath(
                wavePath(w, h, waterTop + 2.dp.toPx(), amplitude * 0.8f, wave),
                Brush.verticalGradient(listOf(color.copy(alpha = 0.70f), color), startY = waterTop, endY = h),
            )
            // Two slow, soft bubbles
            if (!reduced && level > 0.08f) {
                listOf(0.34f to 0f, 0.66f to 0.5f).forEach { (xFraction, offset) ->
                    val t = (bubbles + offset) % 1f
                    val y = h - (h - waterTop) * t
                    val fade = sin(PI.toFloat() * t)           // fades in, then out near the surface
                    drawCircle(Color.White.copy(alpha = 0.45f * fade), radius = 2.6.dp.toPx(), center = Offset(w * xFraction, y))
                }
            }
            // Soft highlight down the left side, like light on plastic
            drawRoundRect(
                Color.White.copy(alpha = 0.18f),
                topLeft = Offset(w * 0.12f, bodyTop + bodyRadius * 0.6f),
                size = Size(w * 0.08f, (h - bodyTop) * 0.55f),
                cornerRadius = CornerRadius(w * 0.04f),
            )
        }
        listOf(0.25f, 0.5f, 0.75f).forEach { mark ->
            val y = h - (h - capH) * mark
            drawLine(outline.copy(alpha = 0.40f), Offset(w * 0.72f, y), Offset(w * 0.86f, y), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        }
        drawPath(bottle, outline.copy(alpha = 0.45f), style = Stroke(1.2.dp.toPx()))
    }
}

private fun wavePath(width: Float, height: Float, top: Float, amplitude: Float, phase: Float) = Path().apply {
    moveTo(0f, height)
    lineTo(0f, top)
    var x = 0f
    while (x <= width) {
        lineTo(x, top + amplitude * sin(TWO_PI * (x / width + phase)))
        x += 3f
    }
    lineTo(width, height)
    close()
}

// ---------------------------------------------------------------------------------------------
// Steps: footprints that appear one by one (fade + gentle scale-in) as the step count grows
// ---------------------------------------------------------------------------------------------

/**
 * @param progress 0..1 towards the daily step goal (today's steps plus this entry).
 */
@Composable
fun FootprintTrail(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    prints: Int = 12,
) {
    val reduced = rememberReducedMotion()
    val clamped = progress.coerceIn(0f, 1f)
    val shown by animateFloatAsState(
        targetValue = clamped * prints,
        animationSpec = if (reduced) snap() else HealthMateMotion.slow(),
        label = "footprints",
    )
    val breathe = loopingPhase(HealthMateMotion.BREATHE_LOOP, reduced, "footBreathe")
    val empty = MaterialTheme.colorScheme.surfaceContainerHighest

    Canvas(
        modifier
            .fillMaxWidth()
            .height(46.dp)
            .semantics { contentDescription = "${(clamped * 100).roundToInt()}% of the daily step goal" },
    ) {
        val gap = size.width / prints
        val latest = shown.toInt().coerceAtMost(prints - 1)
        for (i in 0 until prints) {
            val isLeft = i % 2 == 0
            val center = Offset(gap * (i + 0.5f), size.height / 2 + (if (isLeft) -1f else 1f) * size.height * 0.17f)
            val appear = (shown - i).coerceIn(0f, 1f)
            val eased = appear * appear * (3f - 2f * appear)           // smoothstep: soft start and finish
            val trailAlpha = 0.45f + 0.55f * ((i + 1f) / prints)          // later prints are stronger
            val breathing = if (!reduced && i == latest && appear > 0f) 1f + 0.03f * sin(TWO_PI * breathe) else 1f
            // Empty slot underneath, then the print fading and scaling in on top
            footprint(center, gap * 0.30f, if (isLeft) -12f else 12f, empty)
            if (eased > 0f) {
                footprint(center, gap * 0.30f * (0.75f + 0.25f * eased) * breathing, if (isLeft) -12f else 12f, color.copy(alpha = trailAlpha * eased))
            }
        }
    }
}

private fun DrawScope.footprint(center: Offset, width: Float, angle: Float, color: Color) {
    rotate(angle, pivot = center) {
        val soleH = width * 1.45f
        drawOval(color, topLeft = Offset(center.x - width / 2, center.y - soleH * 0.45f), size = Size(width, soleH * 0.72f))
        drawOval(color, topLeft = Offset(center.x - width * 0.38f, center.y + soleH * 0.30f), size = Size(width * 0.76f, soleH * 0.38f))
        val toeR = width * 0.14f
        listOf(-0.30f, 0f, 0.30f).forEachIndexed { index, dx ->
            drawCircle(color, radius = toeR * (1f - index * 0.12f), center = Offset(center.x + dx * width, center.y - soleH * 0.60f))
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Calories: a flame (shape traced from the HealthMate flame artwork) that grows with the value
// and flickers gently, with an inner flame and a soft glow
// ---------------------------------------------------------------------------------------------

/**
 * @param intensity 0..1 - how big this calorie entry is (e.g. kcal / 800).
 */
@Composable
fun CalorieFlame(
    intensity: Float,
    modifier: Modifier = Modifier,
    width: Dp = 76.dp,
    height: Dp = 94.dp,
) {
    val reduced = rememberReducedMotion()
    val clamped = intensity.coerceIn(0f, 1f)
    val grow by animateFloatAsState(
        targetValue = 0.50f + 0.50f * clamped,
        animationSpec = if (reduced) snap() else HealthMateMotion.gentle(),
        label = "flameSize",
    )
    val t = loopingPhase(HealthMateMotion.FLAME_LOOP, reduced, "flameFlicker")
    val outerTop = lerp(Amber, Coral, clamped)          // small burns amber, big burns coral

    Canvas(
        modifier
            .size(width, height)
            .semantics { contentDescription = "Flame showing a ${if (clamped > 0.66f) "large" else if (clamped > 0.33f) "medium" else "small"} calorie burn" },
    ) {
        // Two blended sine waves give an organic flicker instead of a mechanical pulse.
        val flicker = 0.65f * sin(TWO_PI * t) + 0.35f * sin(TWO_PI * 2f * t + 1.3f)
        val base = Offset(size.width * 0.52f, size.height)
        drawCircle(
            Brush.radialGradient(listOf(outerTop.copy(alpha = 0.22f * grow), Color.Transparent), center = Offset(size.width / 2, size.height * 0.64f), radius = size.width * 0.64f),
            radius = size.width * 0.64f,
            center = Offset(size.width / 2, size.height * 0.64f),
        )
        scale(scaleX = grow * (1f - 0.012f * flicker), scaleY = grow * (1f + 0.03f * flicker), pivot = base) {
            rotate(degrees = 1.6f * flicker, pivot = base) {
                drawPath(smoothClosedPath(FLAME_OUTER, size), Brush.verticalGradient(listOf(outerTop, Amber)))
                // The inner flame moves slightly against the outer one, which reads as "alive".
                rotate(degrees = -2.4f * flicker, pivot = base) {
                    drawPath(smoothClosedPath(FLAME_INNER, size), Brush.verticalGradient(listOf(Orange, Tangerine)))
                }
            }
        }
    }
}

/*
 * Flame outlines as (x, y) points from 0 to 1, traced from the HealthMate flame image.
 * They are joined with smooth curves by [smoothClosedPath].
 */
private val FLAME_OUTER = floatArrayOf(
    0.361f, 0.000f, 0.312f, 0.069f, 0.286f, 0.151f, 0.279f, 0.232f, 0.286f, 0.349f, 0.275f, 0.395f,
    0.260f, 0.419f, 0.242f, 0.434f, 0.219f, 0.443f, 0.190f, 0.443f, 0.138f, 0.422f, 0.108f, 0.398f,
    0.082f, 0.364f, 0.067f, 0.328f, 0.048f, 0.325f, 0.037f, 0.334f, 0.007f, 0.461f, 0.000f, 0.550f,
    0.007f, 0.639f, 0.026f, 0.714f, 0.063f, 0.786f, 0.119f, 0.852f, 0.208f, 0.919f, 0.323f, 0.970f,
    0.442f, 0.996f, 0.530f, 1.000f, 0.617f, 0.994f, 0.729f, 0.973f, 0.814f, 0.940f, 0.870f, 0.910f,
    0.926f, 0.864f, 0.978f, 0.792f, 1.000f, 0.700f, 0.993f, 0.608f, 0.981f, 0.560f, 0.963f, 0.518f,
    0.929f, 0.467f, 0.874f, 0.407f, 0.758f, 0.322f, 0.591f, 0.226f, 0.506f, 0.169f, 0.428f, 0.093f,
    0.394f, 0.036f, 0.372f, 0.000f,
)

private val FLAME_INNER = floatArrayOf(
    0.394f, 0.232f, 0.364f, 0.277f, 0.346f, 0.340f, 0.346f, 0.500f, 0.338f, 0.527f, 0.327f, 0.545f,
    0.301f, 0.566f, 0.242f, 0.569f, 0.208f, 0.554f, 0.182f, 0.533f, 0.156f, 0.491f, 0.149f, 0.491f,
    0.123f, 0.608f, 0.126f, 0.711f, 0.145f, 0.777f, 0.175f, 0.831f, 0.227f, 0.889f, 0.268f, 0.919f,
    0.312f, 0.943f, 0.375f, 0.967f, 0.442f, 0.982f, 0.524f, 0.990f, 0.639f, 0.976f, 0.736f, 0.943f,
    0.784f, 0.916f, 0.822f, 0.886f, 0.844f, 0.858f, 0.870f, 0.807f, 0.877f, 0.771f, 0.874f, 0.696f,
    0.851f, 0.630f, 0.799f, 0.557f, 0.717f, 0.491f, 0.520f, 0.373f, 0.442f, 0.307f, 0.409f, 0.259f,
    0.401f, 0.235f,
)

/** Closed path through the midpoints of the given points, using each point as a curve control. */
private fun smoothClosedPath(points: FloatArray, size: Size): Path {
    val n = points.size / 2
    fun x(i: Int) = points[(i % n) * 2] * size.width
    fun y(i: Int) = points[(i % n) * 2 + 1] * size.height
    return Path().apply {
        moveTo((x(0) + x(1)) / 2, (y(0) + y(1)) / 2)
        for (i in 1..n) {
            quadraticTo(x(i), y(i), (x(i) + x(i + 1)) / 2, (y(i) + y(i + 1)) / 2)
        }
        close()
    }
}

// ---------------------------------------------------------------------------------------------
// Sleep: an iOS-style 24-hour dial with the sleep window, moon and sun knobs, twinkling stars
// ---------------------------------------------------------------------------------------------

@Composable
fun SleepDial(
    bedtime: LocalTime,
    wakeTime: LocalTime,
    color: Color,
    modifier: Modifier = Modifier,
    diameter: Dp = 150.dp,
) {
    val reduced = rememberReducedMotion()
    val startTarget = shortestAngle(minutesOfDay(bedtime) / 1440f * 360f)
    val start by animateFloatAsState(startTarget, if (reduced) snap() else HealthMateMotion.gentle(), label = "dialStart")
    val sweep by animateFloatAsState(
        RecordValidator.sleepMinutes(bedtime, wakeTime) / 1440f * 360f,
        if (reduced) snap() else HealthMateMotion.gentle(),
        label = "dialSweep",
    )
    val twinkle = loopingPhase(HealthMateMotion.TWINKLE_LOOP, reduced, "stars")

    val moon = rememberVectorPainter(Icons.Rounded.Bedtime)
    val sun = rememberVectorPainter(Icons.Rounded.WbSunny)
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val tickColor = MaterialTheme.colorScheme.onSurfaceVariant
    val knobColor = MaterialTheme.colorScheme.surfaceContainerLowest
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = tickColor)
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier
            .size(diameter)
            .semantics { contentDescription = "Sleep from ${Formatters.time(bedtime)} to ${Formatters.time(wakeTime)}" },
    ) {
        val stroke = 16.dp.toPx()
        val inset = stroke / 2 + 1.dp.toPx()
        val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
        val topLeft = Offset(inset, inset)
        val ringRadius = arcSize.width / 2

        drawArc(track, 0f, 360f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(stroke))
        // Sleep window; Canvas angles start at 3 o'clock, so -90 puts midnight at the top.
        drawArc(color, start - 90f, sweep, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round))

        val tickOuter = ringRadius - stroke / 2 - 4.dp.toPx()
        for (hour in 0 until 24) {
            val angle = hour / 24f * TWO_PI - (PI / 2).toFloat()
            val length = (if (hour % 6 == 0) 6.dp else 3.dp).toPx()
            drawLine(tickColor.copy(alpha = 0.45f), polar(center, tickOuter, angle), polar(center, tickOuter - length, angle), strokeWidth = 1.dp.toPx(), cap = StrokeCap.Round)
        }
        listOf(0 to "12AM", 6 to "6AM", 12 to "12PM", 18 to "6PM").forEach { (hour, text) ->
            val layout = textMeasurer.measure(text, labelStyle)
            val angle = hour / 24f * TWO_PI - (PI / 2).toFloat()
            val p = polar(center, tickOuter - 16.dp.toPx(), angle)
            drawText(layout, topLeft = Offset(p.x - layout.size.width / 2f, p.y - layout.size.height / 2f))
        }
        // Slow, soft twinkle
        listOf(Offset(0.44f, 0.42f), Offset(0.58f, 0.47f), Offset(0.49f, 0.58f)).forEachIndexed { index, f ->
            val alpha = if (reduced) 0.6f else 0.35f + 0.55f * (0.5f + 0.5f * sin(TWO_PI * (twinkle + index / 3f)))
            drawCircle(color.copy(alpha = alpha), radius = 1.8.dp.toPx(), center = Offset(size.width * f.x, size.height * f.y))
        }
        // Moon and sun "knobs" at each end of the arc, like the iOS sleep schedule
        val knobRadius = stroke / 2 - 1.5.dp.toPx()
        val iconSize = 10.dp.toPx()
        listOf(moon to start, sun to (start + sweep)).forEach { (painter, degrees) ->
            val p = polar(center, ringRadius, degrees / 360f * TWO_PI - (PI / 2).toFloat())
            drawCircle(knobColor, radius = knobRadius, center = p)
            translate(p.x - iconSize / 2, p.y - iconSize / 2) {
                with(painter) { draw(Size(iconSize, iconSize), colorFilter = ColorFilter.tint(color)) }
            }
        }
    }
}

/**
 * Keeps the bedtime angle continuous across midnight (e.g. 359° -> 361° instead of 359° -> 1°),
 * so the arc always animates the short way round instead of spinning backwards.
 */
@Composable
private fun shortestAngle(target: Float): Float {
    val last = remember { mutableFloatStateOf(target) }
    var adjusted = target
    while (adjusted - last.floatValue > 180f) adjusted -= 360f
    while (last.floatValue - adjusted > 180f) adjusted += 360f
    SideEffect { last.floatValue = adjusted }
    return adjusted
}

private fun minutesOfDay(time: LocalTime) = time.hour * 60 + time.minute

private fun polar(center: Offset, radius: Float, angle: Float) =
    Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
