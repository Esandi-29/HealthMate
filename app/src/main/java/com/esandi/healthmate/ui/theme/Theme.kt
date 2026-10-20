package com.esandi.healthmate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E4F8),
    onPrimaryContainer = Color(0xFF00276B),
    secondary = Indigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6E1FB),
    onSecondaryContainer = Color(0xFF241562),
    tertiary = DeepMint,
    onTertiary = Color.White,
    tertiaryContainer = PaleMint,
    onTertiaryContainer = Color(0xFF00382C),
    error = DeepPink,
    onError = Color.White,
    errorContainer = Color(0xFFFFDDE3),
    onErrorContainer = Color(0xFF5C0019),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceHigh,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = LightSurfaceLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceHigh,
    surfaceContainerHighest = LightSurfaceHighest,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseSurface = DeepPurple,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = BrightBlue,
)

private val DarkColors = darkColorScheme(
    primary = BrightBlue,
    onPrimary = Color(0xFF001A4D),
    primaryContainer = RoyalBlue,
    onPrimaryContainer = Color(0xFFDCE6FF),
    secondary = SoftViolet,
    onSecondary = Color(0xFF1B0F4D),
    secondaryContainer = Indigo,
    onSecondaryContainer = Color(0xFFECE8FF),
    tertiary = Success,
    onTertiary = Color(0xFF003826),
    tertiaryContainer = DeepMint,
    onTertiaryContainer = Color(0xFFE0FFF3),
    error = Color(0xFFFF7A93),
    onError = Color(0xFF4A0016),
    errorContainer = Color(0xFF7A1230),
    onErrorContainer = Color(0xFFFFD9DF),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = Color(0xFF0D0717),
    surfaceContainerLow = DarkSurfaceLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = DarkSurfaceHighest,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkOnSurface,
    inverseOnSurface = DeepPurple,
    inversePrimary = RoyalBlue,
)

/**
 * Colours with a fixed meaning across HealthMate, on top of Material's colour scheme.
 * Each metric has a main colour (rings, icons, chart bars) and a soft container colour
 * (tile backgrounds), so the user recognises a metric by colour on every screen.
 * The brief's examples are followed: water = blue, calories = red.
 */
@Immutable
data class MetricColors(
    val steps: Color,
    val stepsContainer: Color,
    val calories: Color,
    val caloriesContainer: Color,
    val water: Color,
    val waterContainer: Color,
    val sleep: Color,
    val sleepContainer: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val brandGradient: List<Color>,
)

private val LightMetricColors = MetricColors(
    steps = Tangerine,               // O3: deeper orange so it stays visible on white
    stepsContainer = Color(0xFFFDE6D3),
    calories = Coral,                // R1
    caloriesContainer = Color(0xFFFCE1E1),
    water = StrongBlue,              // B2: deeper blue for contrast on white
    waterContainer = Color(0xFFDFE9FB),
    sleep = Indigo,                  // P1
    sleepContainer = Color(0xFFE6E1FB),
    success = DeepMint,
    warning = DeepAmber,
    info = Indigo,
    brandGradient = BrandGradientColors,
)

private val DarkMetricColors = MetricColors(
    steps = Orange,                  // O2
    stepsContainer = Color(0xFF3D2612),
    calories = Coral,                // R1
    caloriesContainer = Color(0xFF3F1C26),
    water = BrightBlue,              // BL2
    waterContainer = Color(0xFF1C2A52),
    sleep = SoftViolet,              // P2
    sleepContainer = Color(0xFF2B2156),
    success = Success,
    warning = GoldenYellow,
    info = SoftViolet,
    brandGradient = BrandGradientColors,
)

private val LocalMetricColors = staticCompositionLocalOf { LightMetricColors }
private val LocalNumberStyles = staticCompositionLocalOf { HealthMateNumberStyles }

/**
 * HealthMate's theme. Wraps Material 3 with the HealthMate colours, fonts and shapes,
 * and follows the phone's light/dark setting by default.
 *
 * Dynamic colour (Android 12 wallpaper colours) is deliberately not used, so the brand
 * palette from the visual guide always shows.
 */
@Composable
fun HealthMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMetricColors provides if (darkTheme) DarkMetricColors else LightMetricColors,
        LocalNumberStyles provides HealthMateNumberStyles,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = HealthMateTypography,
            shapes = HealthMateShapes,
            content = content,
        )
    }
}

/**
 * Access HealthMate-specific theme values, e.g. `HealthMateStyle.metrics.water`.
 * (Named differently from the HealthMateTheme { } function so the two never clash.)
 */
object HealthMateStyle {
    val metrics: MetricColors
        @Composable @ReadOnlyComposable get() = LocalMetricColors.current

    val numbers: NumberStyles
        @Composable @ReadOnlyComposable get() = LocalNumberStyles.current
}
