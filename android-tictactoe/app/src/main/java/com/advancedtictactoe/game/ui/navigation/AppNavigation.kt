package com.advancedtictactoe.game.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.*
import androidx.navigation.compose.*
import com.advancedtictactoe.game.ui.achievements.AchievementsScreen
import com.advancedtictactoe.game.ui.game.GameScreen
import com.advancedtictactoe.game.ui.home.HomeScreen
import com.advancedtictactoe.game.ui.leaderboard.LeaderboardScreen
import com.advancedtictactoe.game.ui.onboarding.OnboardingScreen
import com.advancedtictactoe.game.ui.profile.ProfileScreen
import com.advancedtictactoe.game.ui.settings.SettingsScreen
import com.advancedtictactoe.game.ui.splash.SplashScreen
import com.advancedtictactoe.game.ui.theme.ThemeSelectorScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(400)
            ) + fadeIn(tween(400))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(400)
            ) + fadeOut(tween(400))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(400)
            ) + fadeIn(tween(400))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(400)
            ) + fadeOut(tween(400))
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToGame = { mode, boardSize, difficulty ->
                    navController.navigate(
                        Screen.Game.createRoute(mode, boardSize, difficulty)
                    )
                },
                onNavigateToProfile     = { navController.navigate(Screen.Profile.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToLeaderboard  = { navController.navigate(Screen.Leaderboard.route) },
                onNavigateToSettings    = { navController.navigate(Screen.Settings.route) },
                onNavigateToTheme       = { navController.navigate(Screen.ThemeSelector.route) },
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("mode")       { type = NavType.StringType },
                navArgument("boardSize")  { type = NavType.IntType },
                navArgument("difficulty") { type = NavType.StringType },
            )
        ) { backStack ->
            val mode       = backStack.arguments?.getString("mode") ?: "singleplayer"
            val boardSize  = backStack.arguments?.getInt("boardSize") ?: 3
            val difficulty = backStack.arguments?.getString("difficulty") ?: "MEDIUM"
            GameScreen(
                mode       = mode,
                boardSize  = boardSize,
                difficulty = difficulty,
                onBack     = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ThemeSelector.route) {
            ThemeSelectorScreen(onBack = { navController.popBackStack() })
        }
    }
}
