package com.advancedtictactoe.game.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    var hapticEnabled = true

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun light()   = vibrate(30L)
    fun medium()  = vibrate(60L)
    fun heavy()   = vibrate(120L)
    fun success() = vibrate(longArrayOf(0, 50, 50, 100))
    fun error()   = vibrate(longArrayOf(0, 80, 40, 80))
    fun win()     = vibrate(longArrayOf(0, 100, 50, 100, 50, 200))

    private fun vibrate(ms: Long) {
        if (!hapticEnabled) return
        vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun vibrate(pattern: LongArray) {
        if (!hapticEnabled) return
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }
}
