package com.esandi.healthmate.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.theme.BrightBlue
import com.esandi.healthmate.ui.theme.Coral
import com.esandi.healthmate.ui.theme.DarkSurfaceContainer
import com.esandi.healthmate.ui.theme.HealthMateMotion
import com.esandi.healthmate.ui.theme.LightOnSurface
import com.esandi.healthmate.ui.theme.Orange
import com.esandi.healthmate.ui.theme.SoftViolet
import com.esandi.healthmate.ui.theme.StrongBlue
import com.esandi.healthmate.ui.theme.Tangerine
import com.esandi.healthmate.ui.theme.VividBlue
import kotlinx.coroutines.delay

/**
 * A message for the status banner: an optional metric (for its icon and colour) and text in
 * three parts, so the amount can be highlighted, e.g. "" + "250 ml" + " of water logged".
 * It implements SnackbarVisuals, so Material's SnackbarHostState still handles the timing,
 * queueing and the Undo result; only the look and position are custom.
 */
class BannerMessage(
    val metric: MetricType?,
    val highlight: String,
    val suffix: String = "",
    val prefix: String = "",
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Long,
) : SnackbarVisuals {
    /** Full sentence, also read out by screen readers. */
    override val message: String get() = prefix + highlight + suffix
    override val withDismissAction: Boolean get() = false
}

/**
 * Colours of the pill. It is the opposite of the page so it always stands out
 * (Material's "inverse surface" idea, mirrored like the Save/Cancel buttons):
 *  - light mode: dark pill #1C1C1E, white text, amount in the bright metric colour
 *    (O2 6.90:1, R1 5.24, BL2 5.35, P2 4.84 - all pass WCAG AA for text), Undo in BL2 5.35;
 *  - dark mode: white pill (21:1 against the black page), near-black text 18.85:1. The light
 *    metric shades are below 4.5:1 on white (O3 3.43, R1 3.25, B3 4.33, P2 3.51), so the amount
 *    is bold dark text and the colour stays in the icon (graphics need 3:1 - all pass). Undo B2 5.96.
 */
private class BannerColors(
    val container: Color,
    val text: Color,
    val action: Color,
    val accentFor: (MetricType) -> Color,
    val colourAmount: Boolean,
)

private val DarkPill = BannerColors(
    container = DarkSurfaceContainer,
    text = Color.White,
    action = BrightBlue,
    accentFor = {
        when (it) {
            MetricType.STEPS -> Orange
            MetricType.CALORIES -> Coral
            MetricType.WATER -> BrightBlue
            MetricType.SLEEP -> SoftViolet
        }
    },
    colourAmount = true,
)

private val LightPill = BannerColors(
    container = Color.White,
    text = LightOnSurface,
    action = StrongBlue,
    accentFor = {
        when (it) {
            MetricType.STEPS -> Tangerine
            MetricType.CALORIES -> Coral
            MetricType.WATER -> VividBlue
            MetricType.SLEEP -> SoftViolet
        }
    },
    colourAmount = false,
)

/**
 * Shows the current message as a pill that drops in from the top of the screen with a soft
 * spring and slides back up when it times out, like an iOS banner. Place it at the top of the
 * app, above everything else.
 */
@Composable
fun StatusBannerHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val current = hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current

    // Hide the banner after its time is up: 4 s for confirmations, 10 s when it offers Undo.
    // People using a screen reader may get longer, as Android recommends.
    LaunchedEffect(current) {
        if (current == null) return@LaunchedEffect
        val base = when (current.visuals.duration) {
            SnackbarDuration.Short -> 4_000L
            SnackbarDuration.Long -> 10_000L
            SnackbarDuration.Indefinite -> return@LaunchedEffect
        }
        val timeout = accessibilityManager?.calculateRecommendedTimeoutMillis(
            base,
            containsIcons = true,
            containsText = true,
            containsControls = current.visuals.actionLabel != null,
        ) ?: base
        delay(timeout)
        current.dismiss()
    }

    AnimatedContent(
        targetState = current,
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
        transitionSpec = {
            (slideInVertically(HealthMateMotion.snappy()) { full -> -full * 2 } + fadeIn(tween(160))) togetherWith
                (slideOutVertically(tween(240)) { full -> -full * 2 } + fadeOut(tween(200))) using
                SizeTransform(clip = false)
        },
        label = "statusBanner",
    ) { data ->
        if (data != null) StatusBanner(data) else Spacer(Modifier)
    }
}

/** One banner pill: metric badge, message with the amount highlighted, optional Undo. */
@Composable
private fun StatusBanner(data: SnackbarData) {
    val visuals = data.visuals as? BannerMessage
    val metric = visuals?.metric
    // Dark page -> light pill; light page -> dark pill.
    val colors = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) LightPill else DarkPill
    val accent = metric?.let(colors.accentFor) ?: colors.text
    var dragY by remember { mutableFloatStateOf(0f) }

    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.TopCenter) {
        Row(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .heightIn(min = 52.dp)
                .graphicsLayer { translationY = dragY }
                // Swipe the banner up to dismiss it early.
                .pointerInput(data) {
                    detectVerticalDragGestures(
                        onDragEnd = { if (dragY < -36.dp.toPx()) data.dismiss() else dragY = 0f },
                        onDragCancel = { dragY = 0f },
                    ) { change, dy ->
                        change.consume()
                        dragY = (dragY + dy).coerceAtMost(0f)
                    }
                }
                .shadow(elevation = 14.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.35f))
                .clip(CircleShape)
                .background(colors.container)
                .semantics { liveRegion = LiveRegionMode.Polite }
                .padding(start = 8.dp, end = if (data.visuals.actionLabel != null) 6.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (metric != null) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(accent.copy(alpha = if (colors.colourAmount) 0.22f else 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(metric.icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                }
            } else {
                Spacer(Modifier.size(4.dp))
            }

            Text(
                text = buildAnnotatedString {
                    if (visuals == null) {
                        append(data.visuals.message)
                    } else {
                        append(visuals.prefix)
                        val amountColor = if (colors.colourAmount) accent else colors.text
                        withStyle(SpanStyle(color = amountColor, fontWeight = FontWeight.SemiBold)) { append(visuals.highlight) }
                        append(visuals.suffix)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colors.text,
                maxLines = 2,
                modifier = Modifier.weight(1f, fill = false).padding(vertical = 10.dp),
            )

            data.visuals.actionLabel?.let { label ->
                TextButton(onClick = { data.performAction() }) {
                    Text(label, color = colors.action, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/** Banner wording for each metric: "of water", "of sleep"; steps and kcal already carry their unit. */
fun MetricType.bannerNoun(): String = when (this) {
    MetricType.STEPS, MetricType.CALORIES -> ""
    MetricType.WATER -> " of water"
    MetricType.SLEEP -> " of sleep"
}
