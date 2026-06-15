package com.advancedtictactoe.game.ui.navigation

sealed class Screen(val route: String) {
    object Splash        : Screen("splash")
    object Onboarding    : Screen("onboarding")
    object Home          : Screen("home")
    object Game          : Screen("game/{mode}/{boardSize}/{difficulty}") {
        fun createRoute(mode: String, boardSize: Int, difficulty: String) =
            "game/$mode/$boardSize/$difficulty"
    }
    object Profile       : Screen("profile")
    object Achievements  : Screen("achievements")
    object Leaderboard   : Screen("leaderboard")
    object Settings      : Screen("settings")
    object ThemeSelector : Screen("theme_selector")
    object Tournament    : Screen("tournament")
    object Multiplayer   : Screen("multiplayer")
}
