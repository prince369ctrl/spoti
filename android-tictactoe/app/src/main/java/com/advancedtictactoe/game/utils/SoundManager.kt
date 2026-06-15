package com.advancedtictactoe.game.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null

    private val soundMap = mutableMapOf<String, Int>()

    var soundEnabled = true
    var musicEnabled = true
    var musicVolume  = 0.5f
    var sfxVolume    = 0.8f

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(attrs)
            .build()
    }

    fun playClick() = playSound("click")
    fun playWin()   = playSound("win")
    fun playLose()  = playSound("lose")
    fun playDraw()  = playSound("draw")
    fun playPlace() = playSound("place")
    fun playUndo()  = playSound("undo")

    private fun playSound(name: String) {
        if (!soundEnabled) return
        val id = soundMap[name] ?: return
        soundPool?.play(id, sfxVolume, sfxVolume, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
