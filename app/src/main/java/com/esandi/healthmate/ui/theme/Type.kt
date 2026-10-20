package com.esandi.healthmate.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.esandi.healthmate.R

/*
 * Typography from the HealthMate visual guide (section 3):
 *  - Headers & titles (hero stats, daily greetings) -> Plus Jakarta Sans
 *  - Body & UI elements (settings, labels, insights) -> Roboto (Android's built-in system font)
 *  - Data & numeric displays (steps, calories)       -> Space Grotesk
 *  - Paragraphs or condensed sentences                -> Inter
 * Font files live in res/font and are licensed under the SIL Open Font License (docs/licenses).
 */

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

/** Roboto is the default font on Android, so it does not need to be bundled. */
val Roboto = FontFamily.Default

val HealthMateTypography = Typography(
    // Greetings, hero headings
    displayLarge = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 52.sp, lineHeight = 60.sp, letterSpacing = (-1).sp),
    displayMedium = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 52.sp, letterSpacing = (-0.5).sp),
    displaySmall = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.25).sp),
    headlineLarge = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    // Screen and card titles
    titleLarge = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall = TextStyle(fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    // Paragraphs and descriptions
    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    // Buttons, chips, field labels, navigation labels
    labelLarge = TextStyle(fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)

/**
 * Number styles for health data (steps, kcal, ml), set in Space Grotesk.
 * "tnum" (tabular figures) gives every digit the same width, so values line up and do not jump.
 */
@Immutable
data class NumberStyles(
    val hero: TextStyle,   // the big steps figure inside the ring
    val large: TextStyle,  // metric cards
    val medium: TextStyle, // list rows, details
    val small: TextStyle,  // captions such as "of 8,000"
)

val HealthMateNumberStyles = NumberStyles(
    hero = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 44.sp, lineHeight = 48.sp, letterSpacing = (-0.5).sp, fontFeatureSettings = "tnum"),
    large = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp, fontFeatureSettings = "tnum"),
    medium = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Medium, fontSize = 20.sp, lineHeight = 26.sp, fontFeatureSettings = "tnum"),
    small = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.sp, fontFeatureSettings = "tnum"),
)
