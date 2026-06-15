package com.advancedtictactoe.game

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TicTacToeApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
