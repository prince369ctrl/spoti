package com.advancedtictactoe.game.data.repository

import com.advancedtictactoe.game.data.local.*
import com.advancedtictactoe.game.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val db: AppDatabase
) {
    val profile: Flow<UserProfile?> = db.userProfileDao().getProfile()
    val matchHistory: Flow<List<MatchRecord>> = db.matchHistoryDao().getAllMatches()
    val unlockedAchievements: Flow<List<UnlockedAchievement>> = db.achievementDao().getUnlocked()

    suspend fun ensureProfile() {
        val existing = db.userProfileDao().getProfileOnce()
        if (existing == null) db.userProfileDao().upsert(UserProfile())
    }

    suspend fun updateProfile(profile: UserProfile) = db.userProfileDao().upsert(profile)

    suspend fun recordMatch(
        mode: GameMode,
        difficulty: AIDifficulty,
        boardSize: Int,
        result: String,
        durationSeconds: Long,
        movesCount: Int,
    ) {
        val xp    = GameEngine_xpFor(result, difficulty, movesCount)
        val coins = GameEngine_coinsFor(result)

        db.matchHistoryDao().insert(
            MatchRecord(
                mode            = mode.name,
                difficulty      = difficulty.name,
                boardSize       = boardSize,
                result          = result,
                durationSeconds = durationSeconds,
                movesCount      = movesCount,
                xpEarned        = xp,
                coinsEarned     = coins,
            )
        )
        db.userProfileDao().addXp(xp)
        db.userProfileDao().addCoins(coins)

        when (result) {
            "WIN"  -> {
                db.userProfileDao().incrementWins()
                val p = db.userProfileDao().getProfileOnce()
                if (p != null) db.userProfileDao().updateBestStreak(p.winStreak)
            }
            "LOSS" -> db.userProfileDao().incrementLosses()
            "DRAW" -> db.userProfileDao().incrementDraws()
        }

        db.matchHistoryDao().pruneOld()
    }

    suspend fun unlockAchievement(id: String) {
        db.achievementDao().unlock(UnlockedAchievement(achievementId = id))
    }

    suspend fun getUnlockedIds(): Set<String> =
        db.achievementDao().getUnlockedOnce().map { it.achievementId }.toSet()

    private fun GameEngine_xpFor(result: String, diff: AIDifficulty, moves: Int): Int {
        val base = when (result) { "WIN" -> 50; "DRAW" -> 20; else -> 10 }
        val mul  = when (diff) {
            AIDifficulty.EASY -> 1f; AIDifficulty.MEDIUM -> 1.5f
            AIDifficulty.HARD -> 2f; AIDifficulty.EXPERT -> 3f
            AIDifficulty.IMPOSSIBLE -> 5f
        }
        return ((base * mul) + maxOf(0, 30 - moves)).toInt()
    }

    private fun GameEngine_coinsFor(result: String) =
        when (result) { "WIN" -> 15; "DRAW" -> 5; else -> 2 }
}
