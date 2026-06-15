package com.advancedtictactoe.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// ── Enumerations ──────────────────────────────────────────────────────────────

enum class Player { X, O, NONE }

enum class GameMode {
    SINGLE_PLAYER, LOCAL_MULTIPLAYER, ONLINE_MULTIPLAYER, TOURNAMENT, QUICK_MATCH
}

enum class AIDifficulty(val label: String, val depth: Int) {
    EASY("Easy", 1),
    MEDIUM("Medium", 3),
    HARD("Hard", 5),
    EXPERT("Expert", 7),
    IMPOSSIBLE("Impossible", Int.MAX_VALUE)
}

enum class Rank(val label: String, val minXp: Int, val color: Long) {
    BRONZE("Bronze",       0,      0xFFCD7F32L),
    SILVER("Silver",       500,    0xFFC0C0C0L),
    GOLD("Gold",           1500,   0xFFFFD700L),
    PLATINUM("Platinum",   3000,   0xFFE5E4E2L),
    DIAMOND("Diamond",     6000,   0xFF00BFFFL),
    MASTER("Master",       10000,  0xFFBF00FFL),
    GRANDMASTER("Grandmaster", 18000, 0xFFFF4500L),
    LEGEND("Legend",       30000,  0xFFFFD700L);

    companion object {
        fun fromXp(xp: Int): Rank = values().lastOrNull { xp >= it.minXp } ?: BRONZE
    }
}

// ── Board / Cell ──────────────────────────────────────────────────────────────

data class Cell(
    val row: Int,
    val col: Int,
    val player: Player = Player.NONE,
)

data class BoardConfig(
    val size: Int = 3,
    val winCondition: Int = 3,
) {
    companion object {
        val Classic  = BoardConfig(3, 3)
        val Advanced = BoardConfig(4, 4)
        val Large    = BoardConfig(5, 5)
        val XLarge   = BoardConfig(6, 6)
    }
}

// ── Game state ────────────────────────────────────────────────────────────────

sealed class GameResult {
    data class Winner(val player: Player, val winningCells: List<Pair<Int, Int>>) : GameResult()
    object Draw   : GameResult()
    object Ongoing: GameResult()
}

data class GameState(
    val board: List<List<Player>> = emptyList(),
    val currentPlayer: Player = Player.X,
    val result: GameResult = GameResult.Ongoing,
    val moveHistory: List<Pair<Int, Int>> = emptyList(),
    val redoStack: List<Pair<Int, Int>> = emptyList(),
    val boardConfig: BoardConfig = BoardConfig.Classic,
    val mode: GameMode = GameMode.SINGLE_PLAYER,
    val difficulty: AIDifficulty = AIDifficulty.MEDIUM,
    val isAiThinking: Boolean = false,
    val moveCount: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
) {
    fun cellAt(row: Int, col: Int): Player =
        if (board.isEmpty()) Player.NONE else board[row][col]
}

// ── User / Profile ────────────────────────────────────────────────────────────

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String = "local_user",
    val username: String = "Player",
    val avatarIndex: Int = 0,
    val xp: Int = 0,
    val coins: Int = 100,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val totalDraws: Int = 0,
    val winStreak: Int = 0,
    val bestWinStreak: Int = 0,
    val lastLoginDate: Long = System.currentTimeMillis(),
    val loginStreakDays: Int = 1,
    val selectedTheme: String = "CYBERPUNK",
)

val UserProfile.rank: Rank get() = Rank.fromXp(xp)
val UserProfile.totalGames: Int get() = totalWins + totalLosses + totalDraws
val UserProfile.winRate: Float
    get() = if (totalGames == 0) 0f else totalWins.toFloat() / totalGames

// ── Match history ─────────────────────────────────────────────────────────────

@Entity(tableName = "match_history")
data class MatchRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String,
    val difficulty: String,
    val boardSize: Int,
    val result: String,           // "WIN" | "LOSS" | "DRAW"
    val durationSeconds: Long,
    val movesCount: Int,
    val xpEarned: Int,
    val coinsEarned: Int,
)

// ── Achievements ──────────────────────────────────────────────────────────────

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
)

@Entity(tableName = "unlocked_achievements")
data class UnlockedAchievement(
    @PrimaryKey val achievementId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
)

// ── Leaderboard ───────────────────────────────────────────────────────────────

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val xp: Int,
    val totalWins: Int,
    val avatarIndex: Int,
    val rankTier: Rank,
)
