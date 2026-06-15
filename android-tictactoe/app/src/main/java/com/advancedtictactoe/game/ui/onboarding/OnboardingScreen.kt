package com.advancedtictactoe.game.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advancedtictactoe.game.data.preferences.AppPreferences
import com.advancedtictactoe.game.ui.game.components.NeonButton
import com.advancedtictactoe.game.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val bgColor: Color,
    val accentColor: Color,
)

val onboardingPages = listOf(
    OnboardingPage("✕⊙","Welcome to\nAdvanced Tic-Tac-Toe","Premium 3D Edition 2026",
        "Experience the ultimate Tic-Tac-Toe with stunning 3D visuals, particle effects, and advanced AI opponents.",
        Color(0xFF001020), NeonCyan),
    OnboardingPage("🎮","How to Play","Simple rules, deep strategy",
        "Tap a cell to place your mark. Get 3 (or more on bigger boards) in a row — horizontally, vertically, or diagonally — to win!",
        Color(0xFF0A001A), NeonPurple),
    OnboardingPage("🤖","Game Modes","From casual to competitive",
        "Play against AI (Easy → Impossible), challenge a friend locally, or compete in online multiplayer and tournaments.",
        Color(0xFF1A0005), NeonPink),
    OnboardingPage("🏆","Rewards & Achievements","Unlock 50+ achievements",
        "Earn XP, level up, climb the ranks from Bronze to Legend, and collect daily rewards by logging in every day.",
        Color(0xFF1A1000), RankGold),
    OnboardingPage("🌐","Online Multiplayer","Play with friends worldwide",
        "Create a private room, share your code with a friend, and battle in real-time from anywhere in the world.",
        Color(0xFF001A10), NeonGreen),
    OnboardingPage("♟️","AI Difficulty","5 levels of challenge",
        "Easy AI makes mistakes. Impossible AI uses perfect minimax — can you beat it? Unlock achievements for defeating each level.",
        Color(0xFF000F1A), NeonCyan),
    OnboardingPage("🎨","Custom Boards & Themes","Make it yours",
        "Play on 3×3 to 6×6 boards or create a custom board. Choose from 8 stunning themes: Cyberpunk, Galaxy, Fire, Ice, and more!",
        Color(0xFF100010), NeonPurple),
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: AppPreferences
) : ViewModel() {
    fun complete() = viewModelScope.launch { prefs.setOnboardingDone(true) }
}

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    vm: OnboardingViewModel = hiltViewModel(),
) {
    var page by remember { mutableIntStateOf(0) }
    val current = onboardingPages[page]

    Box(modifier = Modifier.fillMaxSize().background(current.bgColor)) {
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            },
            label = "page"
        ) { p ->
            val pg = onboardingPages[p]
            OnboardingPageContent(pg)
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                onboardingPages.forEachIndexed { idx, _ ->
                    val isActive = idx == page
                    val size by animateDpAsState(if (isActive) 24.dp else 8.dp, label = "dot")
                    Box(
                        Modifier.height(8.dp).width(size)
                            .clip(CircleShape)
                            .background(if (isActive) current.accentColor else Color.White.copy(0.3f))
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            if (page < onboardingPages.lastIndex) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.material3.TextButton(onClick = {
                        vm.complete(); onFinish()
                    }) {
                        Text("Skip", color = TextSecondary, fontSize = 14.sp)
                    }
                    NeonButton("Next →", { page++ },
                        color = current.accentColor, modifier = Modifier.width(160.dp))
                }
            } else {
                NeonButton(
                    "Let's Play! 🚀",
                    onClick = { vm.complete(); onFinish() },
                    color = current.accentColor,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val infiniteTransition = rememberInfiniteTransition(label = "ob")
    val glow by infiniteTransition.animateFloat(
        0.6f, 1f,
        infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "g"
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(160.dp).drawBehind {
                drawCircle(page.accentColor.copy(alpha = 0.12f * glow), radius = size.minDimension * 0.8f)
                drawCircle(page.accentColor.copy(alpha = 0.06f * glow), radius = size.minDimension * 1.1f)
            },
            contentAlignment = Alignment.Center,
        ) {
            Text(page.emoji, fontSize = 72.sp)
        }
        Spacer(Modifier.height(40.dp))
        Text(
            page.title, color = Color.White, fontSize = 28.sp,
            fontWeight = FontWeight.Black, textAlign = TextAlign.Center, lineHeight = 36.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            page.subtitle, color = page.accentColor, fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            page.description, color = TextSecondary, fontSize = 15.sp,
            textAlign = TextAlign.Center, lineHeight = 24.sp,
        )
    }
}
