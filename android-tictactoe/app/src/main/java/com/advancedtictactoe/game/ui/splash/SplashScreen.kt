package com.advancedtictactoe.game.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.advancedtictactoe.game.data.preferences.AppPreferences
import com.advancedtictactoe.game.ui.game.components.FloatingParticles
import com.advancedtictactoe.game.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    object None        : SplashDestination()
    object Onboarding  : SplashDestination()
    object Home        : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.None)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2200)
            val done = prefs.onboardingDone.first()
            _destination.value = if (done) SplashDestination.Home else SplashDestination.Onboarding
        }
    }
}

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    vm: SplashViewModel = hiltViewModel(),
) {
    val destination by vm.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Home       -> { delay(500); onNavigateToHome() }
            SplashDestination.Onboarding -> { delay(500); onNavigateToOnboarding() }
            SplashDestination.None       -> {}
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val glow by infiniteTransition.animateFloat(
        0.6f, 1f,
        infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val fadeOut by animateFloatAsState(
        targetValue = if (destination != SplashDestination.None) 0f else 1f,
        animationSpec = tween(500),
        label = "fade"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(Color(0xFF001A2E), DarkBg),
                radius = 1200f,
            ))
            .alpha(fadeOut),
        contentAlignment = Alignment.Center,
    ) {
        FloatingParticles(Modifier.fillMaxSize(), NeonCyan, 30)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo glow ring
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        drawCircle(NeonCyan.copy(alpha = 0.15f * glow), radius = size.minDimension * 0.7f)
                        drawCircle(NeonCyan.copy(alpha = 0.08f * glow), radius = size.minDimension * 1.0f)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("✕⊙", fontSize = 56.sp, color = NeonCyan.copy(alpha = glow))
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "ADVANCED",
                color = NeonCyan.copy(alpha = glow),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 6.sp,
            )
            Text(
                "TIC-TAC-TOE",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Premium 3D Edition",
                color = TextSecondary,
                fontSize = 13.sp,
                letterSpacing = 3.sp,
            )

            Spacer(Modifier.height(60.dp))
            Text("⬡", color = NeonCyan.copy(alpha = 0.4f + 0.6f * glow), fontSize = 12.sp)
        }
    }
}
