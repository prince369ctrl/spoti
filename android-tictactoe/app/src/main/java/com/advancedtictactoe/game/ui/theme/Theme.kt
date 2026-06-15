package com.advancedtictactoe.game.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary          = NeonCyan,
    onPrimary        = DarkBg,
    primaryContainer = Color(0xFF003B46),
    secondary        = NeonPurple,
    onSecondary      = TextPrimary,
    tertiary         = NeonPink,
    background       = DarkBg,
    surface          = DarkSurface,
    surfaceVariant   = DarkCard,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline          = GlassBorder,
    error            = NeonPink,
)

private val LightColorScheme = lightColorScheme(
    primary          = Color(0xFF006B82),
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFBAEAFF),
    secondary        = Color(0xFF8B00FF),
    onSecondary      = Color.White,
    tertiary         = Color(0xFFD4006E),
    background       = LightBg,
    surface          = LightSurface,
    surfaceVariant   = LightCard,
    onBackground     = TextOnLight,
    onSurface        = TextOnLight,
)

val LocalAppTheme = staticCompositionLocalOf { AppTheme.CYBERPUNK }

enum class AppTheme(val displayName: String, val bgStart: Color, val bgEnd: Color, val accent: Color) {
    CYBERPUNK("Cyberpunk",    CyberpunkStart, CyberpunkEnd,   NeonCyan),
    NEON_BLUE("Neon Blue",    DarkBg,         Color(0xFF001433), NeonCyan),
    GOLD_LUXURY("Gold Luxury",GoldStart,      GoldEnd,        RankGold),
    GALAXY("Galaxy",          GalaxyStart,    GalaxyEnd,      NeonPurple),
    SPACE("Space",            Color(0xFF000510), Color(0xFF001020), NeonCyan),
    FIRE("Fire",              FireStart,      FireEnd,        NeonOrange),
    ICE("Ice",                IceStart,       IceEnd,         Color(0xFF80DFFF)),
    ROYAL_PURPLE("Royal Purple", Color(0xFF0A0015), Color(0xFF1A0030), NeonPurple),
}

@Composable
fun AdvancedTicTacToeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.CYBERPUNK,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
