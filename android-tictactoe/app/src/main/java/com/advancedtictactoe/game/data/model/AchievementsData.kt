package com.advancedtictactoe.game.data.model

object AchievementsData {
    val all: List<Achievement> = listOf(
        Achievement("first_win",        "First Victory",       "Win your first game",                    "🏆", 50,  10),
        Achievement("wins_10",          "Getting Good",        "Win 10 games",                           "⭐", 100, 20),
        Achievement("wins_50",          "Champion",            "Win 50 games",                           "🥇", 300, 60),
        Achievement("wins_100",         "Legend",              "Win 100 games",                          "👑", 750, 150),
        Achievement("wins_500",         "Unstoppable",         "Win 500 games",                          "🔥", 2000,400),
        Achievement("first_draw",       "Draw Artist",         "Get your first draw",                    "🤝", 20,  5),
        Achievement("streak_3",         "Hot Streak",          "Win 3 games in a row",                   "🎯", 75,  15),
        Achievement("streak_5",         "On Fire",             "Win 5 games in a row",                   "💥", 150, 30),
        Achievement("streak_10",        "Legendary Streak",    "Win 10 games in a row",                  "⚡", 500, 100),
        Achievement("easy_win",         "Baby Steps",          "Beat the Easy AI",                       "😊", 25,  5),
        Achievement("medium_win",       "Getting Serious",     "Beat the Medium AI",                     "💪", 50,  10),
        Achievement("hard_win",         "Hard Mode",           "Beat the Hard AI",                       "🔥", 100, 20),
        Achievement("expert_win",       "Expert Slayer",       "Beat the Expert AI",                     "⚔️", 200, 40),
        Achievement("impossible_win",   "The Impossible",      "Beat the Impossible AI",                 "🌟", 1000,200),
        Achievement("perfect_3x3",      "Perfect Game",        "Win a 3x3 game without letting AI play", "✨", 150, 30),
        Achievement("big_board",        "Board Master",        "Win on a 6x6 board",                     "🗺️", 200, 40),
        Achievement("custom_board",     "Rule Maker",          "Play on a custom board",                 "🛠️", 50,  10),
        Achievement("speed_win",        "Speed Demon",         "Win in under 10 moves",                  "⚡", 100, 20),
        Achievement("undo_master",      "Time Traveler",       "Use undo 10 times",                      "⏪", 50,  10),
        Achievement("daily_1",          "Daily Player",        "Claim 1 daily reward",                   "📅", 30,  5),
        Achievement("daily_7",          "Weekly Regular",      "Claim 7 daily rewards",                  "📆", 150, 30),
        Achievement("daily_30",         "Monthly Grinder",     "Claim 30 daily rewards",                 "🗓️", 500, 100),
        Achievement("login_streak_3",   "Consistent",          "Log in 3 days in a row",                 "🌅", 50,  10),
        Achievement("login_streak_7",   "Dedicated",           "Log in 7 days in a row",                 "💎", 200, 40),
        Achievement("login_streak_30",  "Devoted",             "Log in 30 days in a row",                "🏅", 1000,200),
        Achievement("xp_1000",          "Level Up",            "Earn 1,000 XP",                          "📈", 50,  10),
        Achievement("xp_10000",         "High Scorer",         "Earn 10,000 XP",                         "🚀", 200, 40),
        Achievement("coins_500",        "Coin Collector",      "Earn 500 coins",                         "💰", 50,  10),
        Achievement("rank_silver",      "Silver Rank",         "Reach Silver rank",                      "🥈", 100, 20),
        Achievement("rank_gold",        "Gold Rank",           "Reach Gold rank",                        "🥇", 250, 50),
        Achievement("rank_platinum",    "Platinum Rank",       "Reach Platinum rank",                    "💠", 500, 100),
        Achievement("rank_diamond",     "Diamond Rank",        "Reach Diamond rank",                     "💎", 1000,200),
        Achievement("rank_master",      "Master Rank",         "Reach Master rank",                      "🎖️", 2000,400),
        Achievement("rank_legend",      "Legend Rank",         "Reach Legend rank",                      "👑", 5000,1000),
        Achievement("theme_change",     "Style Changer",       "Switch to a new theme",                  "🎨", 25,  5),
        Achievement("all_themes",       "Fashionista",         "Try all 8 themes",                       "🌈", 500, 100),
        Achievement("games_10",         "Casual Player",       "Play 10 games",                          "🎮", 30,  5),
        Achievement("games_50",         "Regular",             "Play 50 games",                          "🕹️", 100, 20),
        Achievement("games_100",        "Veteran",             "Play 100 games",                         "⚔️", 300, 60),
        Achievement("tournament_win",   "Tournament Champ",    "Win a tournament",                       "🏆", 500, 100),
        Achievement("multiplayer_win",  "Social Gamer",        "Win a local multiplayer game",           "👥", 75,  15),
        Achievement("comeback",         "Comeback King",       "Win after being close to losing",        "🔄", 200, 40),
        Achievement("diagonal_win",     "Diagonal Master",     "Win on a diagonal",                      "↗️", 50,  10),
        Achievement("corner_win",       "Corner Strategist",   "Win using all 4 corners",                "📐", 150, 30),
        Achievement("night_owl",        "Night Owl",           "Play a game after midnight",             "🦉", 50,  10),
        Achievement("early_bird",       "Early Bird",          "Play a game before 7am",                 "🐦", 50,  10),
        Achievement("profile_setup",    "Identity",            "Set a custom username",                  "👤", 25,  5),
        Achievement("games_100_ai",     "AI Fighter",          "Play 100 games against AI",              "🤖", 400, 80),
        Achievement("win_rate_80",      "High Win Rate",       "Achieve 80% win rate (min 20 games)",    "📊", 500, 100),
        Achievement("perfect_impossible","Impossible Perfection","Beat Impossible AI without undo",      "🌟", 2000,500),
    )

    fun checkUnlocks(
        profile: UserProfile,
        result: String,
        difficulty: AIDifficulty,
        boardSize: Int,
        mode: GameMode,
        movesCount: Int,
        unlockedIds: Set<String>,
    ): List<Achievement> {
        val toUnlock = mutableListOf<Achievement>()
        fun check(id: String) {
            if (id !in unlockedIds) all.find { it.id == id }?.let { toUnlock.add(it) }
        }

        if (result == "WIN") {
            check("first_win")
            if (profile.totalWins + 1 >= 10)  check("wins_10")
            if (profile.totalWins + 1 >= 50)  check("wins_50")
            if (profile.totalWins + 1 >= 100) check("wins_100")
            if (profile.totalWins + 1 >= 500) check("wins_500")
            if (profile.winStreak + 1 >= 3)  check("streak_3")
            if (profile.winStreak + 1 >= 5)  check("streak_5")
            if (profile.winStreak + 1 >= 10) check("streak_10")
            if (movesCount <= 10) check("speed_win")
            when (difficulty) {
                AIDifficulty.EASY       -> check("easy_win")
                AIDifficulty.MEDIUM     -> check("medium_win")
                AIDifficulty.HARD       -> check("hard_win")
                AIDifficulty.EXPERT     -> check("expert_win")
                AIDifficulty.IMPOSSIBLE -> check("impossible_win")
            }
        }
        if (result == "DRAW") check("first_draw")

        val newTotal = profile.totalGames + 1
        if (newTotal >= 10)  check("games_10")
        if (newTotal >= 50)  check("games_50")
        if (newTotal >= 100) check("games_100")

        if (boardSize == 6) check("big_board")
        if (mode == GameMode.LOCAL_MULTIPLAYER && result == "WIN") check("multiplayer_win")
        if (mode == GameMode.TOURNAMENT && result == "WIN") check("tournament_win")

        val newXp = profile.xp + (if (result == "WIN") 50 else 10)
        if (newXp >= 1000)  check("xp_1000")
        if (newXp >= 10000) check("xp_10000")

        val newRank = Rank.fromXp(newXp)
        when (newRank) {
            Rank.SILVER      -> check("rank_silver")
            Rank.GOLD        -> check("rank_gold")
            Rank.PLATINUM    -> check("rank_platinum")
            Rank.DIAMOND     -> check("rank_diamond")
            Rank.MASTER      -> check("rank_master")
            Rank.LEGEND      -> check("rank_legend")
            else -> {}
        }

        return toUnlock
    }
}
