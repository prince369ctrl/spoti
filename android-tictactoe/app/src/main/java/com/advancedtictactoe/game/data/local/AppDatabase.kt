package com.advancedtictactoe.game.data.local

import androidx.room.*
import com.advancedtictactoe.game.data.model.*
import kotlinx.coroutines.flow.Flow

// ── DAOs ──────────────────────────────────────────────────────────────────────

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE uid = 'local_user' LIMIT 1")
    fun getProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE uid = 'local_user' LIMIT 1")
    suspend fun getProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfile)

    @Query("UPDATE user_profile SET xp = xp + :amount WHERE uid = 'local_user'")
    suspend fun addXp(amount: Int)

    @Query("UPDATE user_profile SET coins = coins + :amount WHERE uid = 'local_user'")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user_profile SET totalWins = totalWins + 1, winStreak = winStreak + 1 WHERE uid = 'local_user'")
    suspend fun incrementWins()

    @Query("UPDATE user_profile SET totalLosses = totalLosses + 1, winStreak = 0 WHERE uid = 'local_user'")
    suspend fun incrementLosses()

    @Query("UPDATE user_profile SET totalDraws = totalDraws + 1 WHERE uid = 'local_user'")
    suspend fun incrementDraws()

    @Query("UPDATE user_profile SET bestWinStreak = :streak WHERE uid = 'local_user' AND bestWinStreak < :streak")
    suspend fun updateBestStreak(streak: Int)
}

@Dao
interface MatchHistoryDao {
    @Query("SELECT * FROM match_history ORDER BY timestamp DESC")
    fun getAllMatches(): Flow<List<MatchRecord>>

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMatches(limit: Int = 20): List<MatchRecord>

    @Insert
    suspend fun insert(record: MatchRecord)

    @Query("DELETE FROM match_history WHERE id NOT IN (SELECT id FROM match_history ORDER BY timestamp DESC LIMIT 100)")
    suspend fun pruneOld()
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM unlocked_achievements")
    fun getUnlocked(): Flow<List<UnlockedAchievement>>

    @Query("SELECT * FROM unlocked_achievements")
    suspend fun getUnlockedOnce(): List<UnlockedAchievement>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(achievement: UnlockedAchievement)
}

// ── Database ──────────────────────────────────────────────────────────────────

@Database(
    entities = [UserProfile::class, MatchRecord::class, UnlockedAchievement::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun matchHistoryDao(): MatchHistoryDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val DATABASE_NAME = "tictactoe_db"
    }
}
