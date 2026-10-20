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

// Light mode: palette colours on the iOS-style grey page with white cards.
private val LightColors = lightColorScheme(
    primary = StrongBlue,                       // B2 - white text on it 5.96:1
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6EFFA),       // 10% B2 tint
    onPrimaryContainer = StrongBlue,            // 5.13:1
    secondary = Indigo,                         // P1 - white text on it 5.71:1
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFEEFB),     // 10% P1 tint
    onSecondaryContainer = Indigo,              // 4.97:1
    tertiary = MintGreen,                       // G1
    onTertiary = LightOnSurface,                // 6.99:1
    tertiaryContainer = Color(0xFFE7F6F3),      // 10% G1 tint
    onTertiaryContainer = LightOnSurface,
    error = Magenta,                            // R3 "important alerts" - 4.88:1 on white
    onError = Color.White,
    errorContainer = Color(0xFFFBE6F3),         // 10% R3 tint
    onErrorContainer = PurplePink,              // R4
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceHigh,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = LightSurfaceLow,
    surfaceContainer = LightSurfaceContainer,   // cards
    surfaceContainerHigh = LightSurfaceHigh,
    surfaceContainerHighest = LightSurfaceHighest,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseSurface = DarkSurfaceHigh,           // snackbars
    inverseOnSurface = DarkOnSurface,
    inversePrimary = BrightBlue,
)

// Dark mode: the brighter palette shades on pure black with elevated grey cards.
private val DarkColors = darkColorScheme(
    primary = BrightBlue,                       // BL2 - 5.35:1 on cards
    onPrimary = Color.Black,                    // 6.60:1
    primaryContainer = Color(0xFF2C3754),       // 24% BL2 tint on the card colour
    onPrimaryContainer = DarkOnSurface,
    secondary = SoftViolet,                     // P2 - 4.84:1 on cards
    onSecondary = Color.Black,                  // 5.98:1
    // Secondary (tonal) buttons mirror light mode: a faint tint with coloured text, so the solid
    // primary button stays clearly dominant. Primary vs secondary = 5.40:1 (light mode: 5.19:1).
    secondaryContainer = Color(0xFF1C1830),     // P2 Soft Violet at 20% on black
    onSecondaryContainer = SoftViolet,          // P2 text on the tint, 4.89:1
    tertiary = Success,                         // S1
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF244F41),      // 24% S1 tint
    onTertiaryContainer = DarkOnSurface,
    error = Pink,                               // R2 - 5.53:1 on black
    onError = Color.Black,
    errorContainer = Color(0xFF4F242F),         // 24% R2 tint
    onErrorContainer = DarkOnSurface,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = DarkSurfaceLow,
    surfaceContainer = DarkSurfaceContainer,    // cards
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = DarkSurfaceHighest,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkOnSurface,
    inverseOnSurface = DarkBackground,
    inversePrimary = StrongBlue,
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

// Metric colours: vivid palette shades, with soft tints of the same colour for badges and tiles
// (as Apple Health does). Light tints are 10% colour on white; dark tints are 24% on the card grey.
private val LightMetricColors = MetricColors(
    steps = Tangerine,                   // O3 - 3.43:1 on white
    stepsContainer = Color(0xFFFEEEE6),
    calories = Coral,                    // R1 - "calories = red" from the brief
    caloriesContainer = Color(0xFFFEEFEF),
    water = VividBlue,                   // B3 - "water = blue", 4.33:1 on white
    waterContainer = Color(0xFFE6F2FB),
    sleep = SoftViolet,                  // P2 - 3.51:1 on white
    sleepContainer = Color(0xFFF3F1FE),
    success = MintGreen,                 // G1
    warning = GoldenYellow,              // Y1 - use as a fill behind dark text, not as text
    info = Indigo,                       // P1
    brandGradient = BrandGradientColors,
)

private val DarkMetricColors = MetricColors(
    steps = Orange,                      // O2 - 6.90:1 on cards
    stepsContainer = Color(0xFF503617),
    calories = Coral,                    // R1 - 5.24:1
    caloriesContainer = Color(0xFF4F2C2D),
    water = BrightBlue,                  // BL2 - 5.35:1
    waterContainer = Color(0xFF2C3754),
    sleep = SoftViolet,                  // P2 - 4.84:1
    sleepContainer = Color(0xFF363250),
    success = Success,                   // S1
    warning = GoldenYellow,              // Y1
    info = SoftViolet,                   // P2
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
