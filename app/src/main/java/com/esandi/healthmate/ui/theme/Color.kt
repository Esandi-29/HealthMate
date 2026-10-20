package com.esandi.healthmate.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * HealthMate colour palette, taken from the HealthMate visual guide (section 2, Colour Palette).
 * Codes in brackets (N1, B1, ...) match the guide so the report can reference them.
 *
 * A few "deep" shades are derived from palette colours where the original is too light to
 * read on a white background (WCAG 2.1 contrast: 4.5:1 for text, 3:1 for icons and graphics).
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

// Derived deep shades for readable text and icons on light backgrounds
val DeepMint = Color(0xFF0B7F69)     // from G1
val DeepPink = Color(0xFFC4214A)     // from R2
val DeepAmber = Color(0xFF8A5A00)    // from Y1

// Light theme surfaces (tinted with Soft Sage N2)
val LightBackground = Color(0xFFF6F8F7)
val LightSurface = Color(0xFFFBFCFB)
val LightSurfaceLow = Color(0xFFF2F5F4)
val LightSurfaceContainer = Color(0xFFECF1EF)
val LightSurfaceHigh = Color(0xFFE5ECE9)
val LightSurfaceHighest = Color(0xFFDDE6E2)
val LightOnSurface = Color(0xFF151A1F)
val LightOnSurfaceVariant = Color(0xFF4F5B58)
val LightOutline = Color(0xFF7C8A86)
val LightOutlineVariant = Color(0xFFC9D6D3)

// Dark theme surfaces (tinted with Deep Purple A1)
val DarkBackground = Color(0xFF120A1F)
val DarkSurface = Color(0xFF160E24)
val DarkSurfaceLow = Color(0xFF1C1330)
val DarkSurfaceContainer = Color(0xFF221838)
val DarkSurfaceHigh = Color(0xFF2A2042)
val DarkSurfaceHighest = Color(0xFF33284D)
val DarkOnSurface = Color(0xFFECE8F4)
val DarkOnSurfaceVariant = Color(0xFFB9B0CC)
val DarkOutline = Color(0xFF8A809E)
val DarkOutlineVariant = Color(0xFF3B3154)

/** Logo gradient: amber -> pink -> violet -> blue -> mint (used for hero accents). */
val BrandGradientColors = listOf(Amber, Pink, SoftViolet, BrightBlue, Success)
