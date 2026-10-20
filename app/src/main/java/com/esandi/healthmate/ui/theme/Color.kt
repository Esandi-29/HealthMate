package com.esandi.healthmate.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * HealthMate colour palette, taken from the HealthMate visual guide (section 2, Colour Palette).
 * Codes in brackets (N1, B1, ...) match the guide so the report can reference them.
 *
 * Every brand and metric colour in the app comes from this palette. Each role uses the palette
 * shade that passes WCAG 2.1 contrast on its background (4.5:1 for text, 3:1 for icons and
 * graphics); the full contrast check is recorded in docs/DEV_LOG.md (DL-16).
 */

// Neutrals: backgrounds, surfaces, borders and subtle text
val MutedTeal = Color(0xFFB7D0D0)    // N1
val SoftSage = Color(0xFFD2E1DB)     // N2
val PaleMint = Color(0xFFC7EDD7)     // N3
val WarmBeige = Color(0xFFF4DCCB)    // N4
val Sand = Color(0xFFF0E7DF)         // N5

// Deep base: primary brand shades
val RoyalBlue = Color(0xFF0247BB)    // B1
val StrongBlue = Color(0xFF0760C9)   // B2
val VividBlue = Color(0xFF0A7BD9)    // B3

// Yellows & amber: energy, focus and highlights
val GoldenYellow = Color(0xFFFFC800) // Y1
val LemonYellow = Color(0xFFFFD91A)  // Y2

// Purples: wellness, balance and information
val Indigo = Color(0xFF6251D7)       // P1
val SoftViolet = Color(0xFF8A77F0)   // P2

// Oranges: activity, movement and energy
val Amber = Color(0xFFFDBF00)        // O1
val Orange = Color(0xFFF58A00)       // O2
val Tangerine = Color(0xFFF45400)    // O3

// Greens: health, success and positive feedback
val MintGreen = Color(0xFF11A88A)    // G1
val Olive = Color(0xFF689C4B)        // G2
val Lime = Color(0xFFA7CC51)         // G3

// Blues: hydration, tracking and information
val SkyBlue = Color(0xFF7DB3F6)      // BL1
val BrightBlue = Color(0xFF5D8BFF)   // BL2

// Pinks & magenta: motivation, goals and important alerts
val Coral = Color(0xFFF15D5D)        // R1
val Pink = Color(0xFFEF3D66)         // R2
val Magenta = Color(0xFFD70A8B)      // R3
val PurplePink = Color(0xFFB31EA0)   // R4

// Brand accent
val DeepPurple = Color(0xFF2F174F)   // A1

// Semantic colours (S1-S5)
val Success = Color(0xFF3CEFAE)      // S1
val Warning = GoldenYellow           // S2
val Error = Coral                    // S3
val Info = SoftViolet                // S4
val Link = BrightBlue                // S5

// Surfaces follow an iOS-style "grouped" look (user direction, 20 Oct): a light grey page with
// white cards in light mode, and pure black with elevated greys in dark mode.
// Cards are separated by colour alone - no borders or heavy shadows.

// Light theme surfaces
val LightBackground = Color(0xFFF2F2F7)        // grouped page background
val LightSurface = Color(0xFFF2F2F7)
val LightSurfaceLow = Color(0xFFF7F7FA)
val LightSurfaceContainer = Color(0xFFFFFFFF)   // cards
val LightSurfaceHigh = Color(0xFFE9E9EE)        // segmented-control track, chips
val LightSurfaceHighest = Color(0xFFDCDCE2)     // progress tracks
val LightOnSurface = Color(0xFF111114)
val LightOnSurfaceVariant = Color(0xFF66666C)   // secondary text (4.9:1 on the page)
val LightOutline = Color(0xFF8E8E93)
val LightOutlineVariant = Color(0xFFD5D5DA)     // hairline separators

// Dark theme surfaces: pure black so the bright metric colours stand out
// ("contrasting colours on dark mode" from the visual guide)
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF000000)
val DarkSurfaceLow = Color(0xFF111113)
val DarkSurfaceContainer = Color(0xFF1C1C1E)    // cards
val DarkSurfaceHigh = Color(0xFF2C2C2E)         // segmented-control track, chips
val DarkSurfaceHighest = Color(0xFF3A3A3C)      // progress tracks
val DarkOnSurface = Color(0xFFF5F5F7)
val DarkOnSurfaceVariant = Color(0xFFA1A1A8)    // secondary text
val DarkOutline = Color(0xFF6E6E73)
val DarkOutlineVariant = Color(0xFF38383A)      // hairline separators

/** Logo gradient: amber -> pink -> violet -> blue -> mint (used for hero accents). */
val BrandGradientColors = listOf(Amber, Pink, SoftViolet, BrightBlue, Success)
