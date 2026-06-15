package com.advancedtictactoe.game.game.engine

import com.advancedtictactoe.game.data.model.*

object GameEngine {

    fun createBoard(config: BoardConfig): List<List<Player>> =
        List(config.size) { List(config.size) { Player.NONE } }

    fun applyMove(board: List<List<Player>>, row: Int, col: Int, player: Player): List<List<Player>> {
        if (board[row][col] != Player.NONE) return board
        return board.mapIndexed { r, rowList ->
            if (r == row) rowList.mapIndexed { c, cell -> if (c == col) player else cell }
            else rowList
        }
    }

    fun evaluate(board: List<List<Player>>, config: BoardConfig): GameResult {
        val size = config.size
        val win  = config.winCondition

        // Check all lines
        val allLines = buildList<List<Pair<Int, Int>>> {
            // Rows
            for (r in 0 until size) add((0 until size).map { c -> r to c })
            // Columns
            for (c in 0 until size) add((0 until size).map { r -> r to c })
            // Diagonals (top-left to bottom-right)
            for (start in -(size - win) until size) {
                val diag = (0 until size).mapNotNull { i ->
                    val r = i; val c = i - start
                    if (c in 0 until size) r to c else null
                }
                if (diag.size >= win) add(diag)
            }
            // Diagonals (top-right to bottom-left)
            for (start in (win - 1) until (2 * size - win)) {
                val antidiag = (0 until size).mapNotNull { i ->
                    val r = i; val c = start - i
                    if (c in 0 until size) r to c else null
                }
                if (antidiag.size >= win) add(antidiag)
            }
        }

        for (line in allLines) {
            val windowResult = checkWindows(board, line, win)
            if (windowResult != null) return windowResult
        }

        val isDraw = board.all { row -> row.all { it != Player.NONE } }
        return if (isDraw) GameResult.Draw else GameResult.Ongoing
    }

    private fun checkWindows(
        board: List<List<Player>>,
        line: List<Pair<Int, Int>>,
        win: Int
    ): GameResult.Winner? {
        for (i in 0..line.size - win) {
            val window = line.subList(i, i + win)
            val first = board[window[0].first][window[0].second]
            if (first != Player.NONE && window.all { (r, c) -> board[r][c] == first }) {
                return GameResult.Winner(first, window)
            }
        }
        return null
    }

    fun getAvailableMoves(board: List<List<Player>>): List<Pair<Int, Int>> =
        board.flatMapIndexed { r, row ->
            row.mapIndexedNotNull { c, cell -> if (cell == Player.NONE) r to c else null }
        }

    fun isTerminal(result: GameResult) = result !is GameResult.Ongoing

    fun xpForResult(result: String, difficulty: AIDifficulty, movesCount: Int): Int {
        val base = when (result) {
            "WIN"  -> 50
            "DRAW" -> 20
            else   -> 10
        }
        val diffMultiplier = when (difficulty) {
            AIDifficulty.EASY       -> 1.0f
            AIDifficulty.MEDIUM     -> 1.5f
            AIDifficulty.HARD       -> 2.0f
            AIDifficulty.EXPERT     -> 3.0f
            AIDifficulty.IMPOSSIBLE -> 5.0f
        }
        val speedBonus = maxOf(0, 30 - movesCount)
        return ((base * diffMultiplier) + speedBonus).toInt()
    }

    fun coinsForResult(result: String): Int = when (result) {
        "WIN"  -> 15
        "DRAW" -> 5
        else   -> 2
    }
}
