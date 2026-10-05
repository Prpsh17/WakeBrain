package com.pemmob.wakebrain.ui.activealarm

import com.pemmob.wakebrain.data.model.Question
import com.pemmob.wakebrain.puzzle.MathProblem

sealed interface PuzzleUiState {
    data object Loading : PuzzleUiState

    data class MathActive(
        val problem: MathProblem,
        val stage: Int = 1,
        val totalStages: Int = 2,
        val isError: Boolean = false,
        val errorMessage: String? = null
    ) : PuzzleUiState

    data class TriviaActive(
        val question: Question,
        val shuffledOptions: List<String>,
        val stage: Int = 2,
        val totalStages: Int = 2,
        val isError: Boolean = false,
        val errorMessage: String? = null
    ) : PuzzleUiState

    data object Solved : PuzzleUiState

    data class Error(val message: String) : PuzzleUiState
}
