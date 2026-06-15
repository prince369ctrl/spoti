package com.advancedtictactoe.game.game.ai

import com.advancedtictactoe.game.data.model.*
import com.advancedtictactoe.game.game.engine.GameEngine
import kotlin.random.Random

object AIPlayer {

    fun getBestMove(
        board: List<List<Player>>,
        config: BoardConfig,
        aiPlayer: Player,
        difficulty: AIDifficulty,
    ): Pair<Int, Int> {
        val available = GameEngine.getAvailableMoves(board)
        if (available.isEmpty()) error("No moves available")

        return when (difficulty) {
            AIDifficulty.EASY       -> easyMove(board, config, aiPlayer, available)
            AIDifficulty.MEDIUM     -> mediumMove(board, config, aiPlayer, available)
            AIDifficulty.HARD       -> minimaxMove(board, config, aiPlayer, 4)
            AIDifficulty.EXPERT     -> minimaxMove(board, config, aiPlayer, 7)
            AIDifficulty.IMPOSSIBLE -> minimaxMove(board, config, aiPlayer, Int.MAX_VALUE)
        }
    }

    // ── Easy: ~70% random, ~30% winning / blocking ──────────────────────────

    private fun easyMove(
        board: List<List<Player>>,
        config: BoardConfig,
        aiPlayer: Player,
        available: List<Pair<Int, Int>>,
    ): Pair<Int, Int> {
        if (Random.nextFloat() < 0.3f) {
            val smart = winOrBlock(board, config, aiPlayer, available)
            if (smart != null) return smart
        }
        return available.random()
    }

    // ── Medium: always win if possible, block if needed, else random ─────────

    private fun mediumMove(
        board: List<List<Player>>,
        config: BoardConfig,
        aiPlayer: Player,
        available: List<Pair<Int, Int>>,
    ): Pair<Int, Int> = winOrBlock(board, config, aiPlayer, available)
        ?: preferCenter(board, config, available)
        ?: available.random()

    private fun winOrBlock(
        board: List<List<Player>>,
        config: BoardConfig,
        aiPlayer: Player,
        available: List<Pair<Int, Int>>,
    ): Pair<Int, Int>? {
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X
        // Try to win first
        for (move in available) {
            val newBoard = GameEngine.applyMove(board, move.first, move.second, aiPlayer)
            if (GameEngine.evaluate(newBoard, config) is GameResult.Winner) return move
        }
        // Try to block opponent
        for (move in available) {
            val newBoard = GameEngine.applyMove(board, move.first, move.second, opponent)
            if (GameEngine.evaluate(newBoard, config) is GameResult.Winner) return move
        }
        return null
    }

    private fun preferCenter(
        board: List<List<Player>>,
        config: BoardConfig,
        available: List<Pair<Int, Int>>,
    ): Pair<Int, Int>? {
        val center = config.size / 2
        return if (board[center][center] == Player.NONE) center to center else null
    }

    // ── Minimax with alpha-beta pruning ──────────────────────────────────────

    private fun minimaxMove(
        board: List<List<Player>>,
        config: BoardConfig,
        aiPlayer: Player,
        maxDepth: Int,
    ): Pair<Int, Int> {
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X
        var bestScore = Int.MIN_VALUE
        var bestMove  = GameEngine.getAvailableMoves(board).first()

        for (move in GameEngine.getAvailableMoves(board)) {
            val newBoard = GameEngine.applyMove(board, move.first, move.second, aiPlayer)
            val score = minimax(
                board    = newBoard,
                config   = config,
                depth    = maxDepth - 1,
                isMax    = false,
                aiPlayer = aiPlayer,
                opponent = opponent,
                alpha    = Int.MIN_VALUE,
                beta     = Int.MAX_VALUE,
            )
            if (score > bestScore) { bestScore = score; bestMove = move }
        }
        return bestMove
    }

    private fun minimax(
        board: List<List<Player>>,
        config: BoardConfig,
        depth: Int,
        isMax: Boolean,
        aiPlayer: Player,
        opponent: Player,
        alpha: Int,
        beta: Int,
    ): Int {
        val result = GameEngine.evaluate(board, config)
        if (result is GameResult.Winner) {
            return if (result.player == aiPlayer) 1000 + depth else -(1000 + depth)
        }
        if (result is GameResult.Draw || depth == 0) return heuristic(board, config, aiPlayer)

        val moves = GameEngine.getAvailableMoves(board)
        var a = alpha; var b = beta

        return if (isMax) {
            var best = Int.MIN_VALUE
            for (move in moves) {
                val nb = GameEngine.applyMove(board, move.first, move.second, aiPlayer)
                best = maxOf(best, minimax(nb, config, depth - 1, false, aiPlayer, opponent, a, b))
                a = maxOf(a, best)
                if (b <= a) break
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (move in moves) {
                val nb = GameEngine.applyMove(board, move.first, move.second, opponent)
                best = minOf(best, minimax(nb, config, depth - 1, true, aiPlayer, opponent, a, b))
                b = minOf(b, best)
                if (b <= a) break
            }
            best
        }
    }

    // ── Heuristic for depth-limited search ───────────────────────────────────

    private fun heuristic(
        board: List<List<Player>>,
        config: BoardConfig,
        aiPlayer: Player,
    ): Int {
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X
        var score = 0
        val size  = config.size

        // Center preference
        val center = size / 2
        if (board[center][center] == aiPlayer) score += 3
        else if (board[center][center] == opponent) score -= 3

        // Count threats
        score += countThreats(board, config, aiPlayer) * 2
        score -= countThreats(board, config, opponent) * 2

        return score
    }

    private fun countThreats(
        board: List<List<Player>>,
        config: BoardConfig,
        player: Player,
    ): Int {
        var threats = 0
        val size    = config.size
        val win     = config.winCondition
        val opponent = if (player == Player.X) Player.O else Player.X

        // Simplified: count rows/cols/diags with (win-1) same symbols and no opponent
        for (r in 0 until size) {
            val row = (0 until size).map { c -> board[r][c] }
            if (row.count { it == opponent } == 0 && row.count { it == player } == win - 1) threats++
        }
        for (c in 0 until size) {
            val col = (0 until size).map { r -> board[r][c] }
            if (col.count { it == opponent } == 0 && col.count { it == player } == win - 1) threats++
        }
        return threats
    }
}
