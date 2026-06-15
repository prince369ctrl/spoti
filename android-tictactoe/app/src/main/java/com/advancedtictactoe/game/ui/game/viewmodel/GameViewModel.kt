package com.advancedtictactoe.game.ui.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advancedtictactoe.game.data.model.*
import com.advancedtictactoe.game.data.repository.GameRepository
import com.advancedtictactoe.game.game.ai.AIPlayer
import com.advancedtictactoe.game.game.engine.GameEngine
import com.advancedtictactoe.game.utils.HapticManager
import com.advancedtictactoe.game.utils.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class GameUiState(
    val gameState: GameState = GameState(),
    val winningCells: List<Pair<Int, Int>> = emptyList(),
    val newAchievements: List<Achievement> = emptyList(),
    val showResultDialog: Boolean = false,
    val xpEarned: Int = 0,
    val coinsEarned: Int = 0,
    val hintCell: Pair<Int, Int>? = null,
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val soundManager: SoundManager,
    private val hapticManager: HapticManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aiJob: Job? = null
    private var gameStartTime = System.currentTimeMillis()

    fun initGame(mode: String, boardSize: Int, difficultyStr: String) {
        val config = BoardConfig(boardSize, minOf(boardSize, if (boardSize <= 3) 3 else boardSize - 1))
        val difficulty = try { AIDifficulty.valueOf(difficultyStr) } catch (e: Exception) { AIDifficulty.MEDIUM }
        val gameMode = try { GameMode.valueOf(mode.uppercase()) } catch (e: Exception) { GameMode.SINGLE_PLAYER }

        val initialBoard = GameEngine.createBoard(config)
        val newState = GameState(
            board       = initialBoard,
            boardConfig = config,
            mode        = gameMode,
            difficulty  = difficulty,
        )
        _uiState.update { it.copy(gameState = newState, winningCells = emptyList(), showResultDialog = false) }
        gameStartTime = System.currentTimeMillis()
        viewModelScope.launch { repository.ensureProfile() }
    }

    fun onCellClick(row: Int, col: Int) {
        val state = _uiState.value.gameState
        if (state.result !is GameResult.Ongoing) return
        if (state.board[row][col] != Player.NONE) return
        if (state.isAiThinking) return

        makeMove(row, col, state.currentPlayer)
    }

    private fun makeMove(row: Int, col: Int, player: Player) {
        val current = _uiState.value.gameState
        val newBoard = GameEngine.applyMove(current.board, row, col, player)
        val result   = GameEngine.evaluate(newBoard, current.boardConfig)
        val history  = current.moveHistory + (row to col)
        val nextPlayer = if (player == Player.X) Player.O else Player.X

        soundManager.playPlace()
        hapticManager.light()

        val newState = current.copy(
            board         = newBoard,
            currentPlayer = nextPlayer,
            result        = result,
            moveHistory   = history,
            redoStack     = emptyList(),
            moveCount     = current.moveCount + 1,
        )

        val winCells = if (result is GameResult.Winner) result.winningCells else emptyList()

        _uiState.update { it.copy(gameState = newState, winningCells = winCells, hintCell = null) }

        if (result !is GameResult.Ongoing) {
            handleGameEnd(result, newState)
        } else if (newState.mode == GameMode.SINGLE_PLAYER && nextPlayer == Player.O) {
            triggerAiMove(newState)
        }
    }

    private fun triggerAiMove(state: GameState) {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            _uiState.update { it.copy(gameState = it.gameState.copy(isAiThinking = true)) }
            delay(when (state.difficulty) {
                AIDifficulty.EASY   -> 300L
                AIDifficulty.MEDIUM -> 500L
                AIDifficulty.HARD   -> 700L
                else                -> 900L
            })
            if (!isActive) return@launch
            val (r, c) = withContext(Dispatchers.Default) {
                AIPlayer.getBestMove(state.board, state.boardConfig, Player.O, state.difficulty)
            }
            _uiState.update { it.copy(gameState = it.gameState.copy(isAiThinking = false)) }
            makeMove(r, c, Player.O)
        }
    }

    private fun handleGameEnd(result: GameResult, state: GameState) {
        val resultStr = when {
            result is GameResult.Winner && result.player == Player.X -> "WIN"
            result is GameResult.Winner && result.player == Player.O ->
                if (state.mode == GameMode.SINGLE_PLAYER) "LOSS" else "WIN_O"
            else -> "DRAW"
        }
        val canonicalResult = when (resultStr) { "WIN_O" -> "WIN"; else -> resultStr }

        when (result) {
            is GameResult.Winner -> { soundManager.playWin(); hapticManager.win() }
            is GameResult.Draw   -> { soundManager.playDraw(); hapticManager.medium() }
            else                 -> {}
        }

        viewModelScope.launch {
            val profile = repository.profile.firstOrNull() ?: UserProfile()
            val unlockedIds = repository.getUnlockedIds()
            val duration = (System.currentTimeMillis() - gameStartTime) / 1000

            repository.recordMatch(
                mode            = state.mode,
                difficulty      = state.difficulty,
                boardSize       = state.boardConfig.size,
                result          = canonicalResult,
                durationSeconds = duration,
                movesCount      = state.moveCount,
            )

            val newAchievements = AchievementsData.checkUnlocks(
                profile      = profile,
                result       = canonicalResult,
                difficulty   = state.difficulty,
                boardSize    = state.boardConfig.size,
                mode         = state.mode,
                movesCount   = state.moveCount,
                unlockedIds  = unlockedIds,
            )
            newAchievements.forEach { repository.unlockAchievement(it.id) }

            val xp    = GameEngine.xpForResult(canonicalResult, state.difficulty, state.moveCount)
            val coins = GameEngine.coinsForResult(canonicalResult)

            _uiState.update {
                it.copy(
                    newAchievements  = newAchievements,
                    showResultDialog = true,
                    xpEarned         = xp,
                    coinsEarned      = coins,
                )
            }
        }
    }

    fun undoMove() {
        val state = _uiState.value.gameState
        if (state.moveHistory.isEmpty()) return

        aiJob?.cancel()

        val lastMove   = state.moveHistory.last()
        val newHistory = state.moveHistory.dropLast(1)
        val newRedo    = listOf(lastMove) + state.redoStack

        // If AI was Player.O, undo two moves
        val movesToUndo = if (state.mode == GameMode.SINGLE_PLAYER && newHistory.isNotEmpty()) 2 else 1
        val finalHistory = if (movesToUndo == 2 && newHistory.size >= 1)
            newHistory.dropLast(1) else newHistory
        val finalRedo = if (movesToUndo == 2 && newHistory.isNotEmpty())
            listOf(newHistory.last()) + newRedo else newRedo

        val freshBoard = GameEngine.createBoard(state.boardConfig)
        val rebuiltBoard = finalHistory.foldIndexed(freshBoard) { idx, board, move ->
            GameEngine.applyMove(board, move.first, move.second, if (idx % 2 == 0) Player.X else Player.O)
        }
        soundManager.playUndo()
        hapticManager.light()

        _uiState.update {
            it.copy(
                gameState = state.copy(
                    board         = rebuiltBoard,
                    currentPlayer = Player.X,
                    result        = GameResult.Ongoing,
                    moveHistory   = finalHistory,
                    redoStack     = finalRedo,
                    isAiThinking  = false,
                    moveCount     = finalHistory.size,
                ),
                winningCells = emptyList(),
                showResultDialog = false,
            )
        }
    }

    fun getHint() {
        val state = _uiState.value.gameState
        viewModelScope.launch(Dispatchers.Default) {
            val hint = AIPlayer.getBestMove(state.board, state.boardConfig, Player.X, AIDifficulty.HARD)
            _uiState.update { it.copy(hintCell = hint) }
        }
    }

    fun dismissResult() = _uiState.update { it.copy(showResultDialog = false) }

    fun restartGame() {
        val current = _uiState.value.gameState
        initGame(current.mode.name, current.boardConfig.size, current.difficulty.name)
    }
}
