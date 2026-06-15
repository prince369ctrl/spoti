package com.advancedtictactoe.game.ui.game.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.*
import kotlin.random.Random

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val color: Color,
    val alpha: Float,
    val life: Float,        // 0 → 1
    val maxLife: Float,
)

@Composable
fun ParticleExplosion(
    modifier: Modifier = Modifier,
    trigger: Boolean,
    color: Color = Color(0xFF00F5FF),
    secondColor: Color = Color(0xFFBF00FF),
    particleCount: Int = 60,
) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    val infiniteTransition = rememberInfiniteTransition(label = "particle")
    val tick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(tween(16, easing = LinearEasing)),
        label = "tick"
    )

    LaunchedEffect(trigger) {
        if (trigger) {
            particles = List(particleCount) {
                val angle = Random.nextFloat() * 2 * PI.toFloat()
                val speed = Random.nextFloat() * 12f + 4f
                Particle(
                    x       = 0.5f,
                    y       = 0.5f,
                    vx      = cos(angle) * speed,
                    vy      = sin(angle) * speed,
                    radius  = Random.nextFloat() * 6f + 2f,
                    color   = if (Random.nextBoolean()) color else secondColor,
                    alpha   = 1f,
                    life    = 0f,
                    maxLife = Random.nextFloat() * 0.5f + 0.5f,
                )
            }
        }
    }

    LaunchedEffect(tick) {
        if (particles.isNotEmpty()) {
            particles = particles.mapNotNull { p ->
                val newLife = p.life + 0.02f
                if (newLife >= p.maxLife) null
                else p.copy(
                    x     = p.x + p.vx * 0.003f,
                    y     = p.y + p.vy * 0.003f,
                    vy    = p.vy + 0.2f,       // gravity
                    life  = newLife,
                    alpha = 1f - (newLife / p.maxLife),
                )
            }
        }
    }

    Canvas(modifier = modifier) {
        particles.forEach { p ->
            drawCircle(
                color  = p.color.copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(p.x * size.width, p.y * size.height),
            )
        }
    }
}

@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00F5FF),
    count: Int = 20,
) {
    val particles = remember {
        List(count) {
            FloatArray(4) {
                when (it) {
                    0 -> Random.nextFloat()   // x
                    1 -> Random.nextFloat()   // y
                    2 -> Random.nextFloat() * 2 - 1  // vx
                    3 -> Random.nextFloat() * 2 - 1  // vy
                    else -> 0f
                }
            }
        }.toMutableList()
    }
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(tween(50, easing = LinearEasing)),
        label = "float_tick"
    )
    Canvas(modifier = modifier) {
        particles.forEach { p ->
            p[0] = (p[0] + p[2] * 0.001f + 1f) % 1f
            p[1] = (p[1] + p[3] * 0.001f + 1f) % 1f
            drawCircle(
                color  = color.copy(alpha = 0.3f),
                radius = 2f,
                center = Offset(p[0] * size.width, p[1] * size.height),
            )
        }
    }
}
