package com.advancedtictactoe.game.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDING_DONE   = booleanPreferencesKey("onboarding_done")
        val DARK_MODE         = booleanPreferencesKey("dark_mode")
        val SOUND_ENABLED     = booleanPreferencesKey("sound_enabled")
        val MUSIC_ENABLED     = booleanPreferencesKey("music_enabled")
        val HAPTIC_ENABLED    = booleanPreferencesKey("haptic_enabled")
        val MUSIC_VOLUME      = floatPreferencesKey("music_volume")
        val SFX_VOLUME        = floatPreferencesKey("sfx_volume")
        val SELECTED_THEME    = stringPreferencesKey("selected_theme")
        val GRAPHICS_QUALITY  = stringPreferencesKey("graphics_quality")
        val COLORBLIND_MODE   = booleanPreferencesKey("colorblind_mode")
        val LARGE_TEXT        = booleanPreferencesKey("large_text")
        val LANGUAGE          = stringPreferencesKey("language")
        val PARTICLES_ENABLED = booleanPreferencesKey("particles_enabled")
        val LAST_DAILY_CLAIM  = longPreferencesKey("last_daily_claim")
    }

    val onboardingDone: Flow<Boolean>   = context.dataStore.data.map { it[Keys.ONBOARDING_DONE]   ?: false }
    val darkMode: Flow<Boolean>         = context.dataStore.data.map { it[Keys.DARK_MODE]         ?: true  }
    val soundEnabled: Flow<Boolean>     = context.dataStore.data.map { it[Keys.SOUND_ENABLED]     ?: true  }
    val musicEnabled: Flow<Boolean>     = context.dataStore.data.map { it[Keys.MUSIC_ENABLED]     ?: true  }
    val hapticEnabled: Flow<Boolean>    = context.dataStore.data.map { it[Keys.HAPTIC_ENABLED]    ?: true  }
    val musicVolume: Flow<Float>        = context.dataStore.data.map { it[Keys.MUSIC_VOLUME]      ?: 0.5f  }
    val sfxVolume: Flow<Float>          = context.dataStore.data.map { it[Keys.SFX_VOLUME]        ?: 0.8f  }
    val selectedTheme: Flow<String>     = context.dataStore.data.map { it[Keys.SELECTED_THEME]    ?: "CYBERPUNK" }
    val graphicsQuality: Flow<String>   = context.dataStore.data.map { it[Keys.GRAPHICS_QUALITY]  ?: "HIGH" }
    val colorblindMode: Flow<Boolean>   = context.dataStore.data.map { it[Keys.COLORBLIND_MODE]   ?: false }
    val largeText: Flow<Boolean>        = context.dataStore.data.map { it[Keys.LARGE_TEXT]        ?: false }
    val language: Flow<String>          = context.dataStore.data.map { it[Keys.LANGUAGE]          ?: "en"  }
    val particlesEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.PARTICLES_ENABLED] ?: true  }
    val lastDailyClaim: Flow<Long>      = context.dataStore.data.map { it[Keys.LAST_DAILY_CLAIM]  ?: 0L    }

    suspend fun setOnboardingDone(done: Boolean) =
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    suspend fun setDarkMode(dark: Boolean) =
        context.dataStore.edit { it[Keys.DARK_MODE] = dark }
    suspend fun setSoundEnabled(v: Boolean) =
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = v }
    suspend fun setMusicEnabled(v: Boolean) =
        context.dataStore.edit { it[Keys.MUSIC_ENABLED] = v }
    suspend fun setHapticEnabled(v: Boolean) =
        context.dataStore.edit { it[Keys.HAPTIC_ENABLED] = v }
    suspend fun setMusicVolume(v: Float) =
        context.dataStore.edit { it[Keys.MUSIC_VOLUME] = v }
    suspend fun setSfxVolume(v: Float) =
        context.dataStore.edit { it[Keys.SFX_VOLUME] = v }
    suspend fun setSelectedTheme(theme: String) =
        context.dataStore.edit { it[Keys.SELECTED_THEME] = theme }
    suspend fun setGraphicsQuality(q: String) =
        context.dataStore.edit { it[Keys.GRAPHICS_QUALITY] = q }
    suspend fun setColorblindMode(v: Boolean) =
        context.dataStore.edit { it[Keys.COLORBLIND_MODE] = v }
    suspend fun setLargeText(v: Boolean) =
        context.dataStore.edit { it[Keys.LARGE_TEXT] = v }
    suspend fun setLanguage(lang: String) =
        context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    suspend fun setParticlesEnabled(v: Boolean) =
        context.dataStore.edit { it[Keys.PARTICLES_ENABLED] = v }
    suspend fun setLastDailyClaim(time: Long) =
        context.dataStore.edit { it[Keys.LAST_DAILY_CLAIM] = time }
}
