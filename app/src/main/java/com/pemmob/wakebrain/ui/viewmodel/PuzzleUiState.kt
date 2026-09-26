package com.pemmob.wakebrain.ui.viewmodel

import com.pemmob.wakebrain.data.Question
import com.pemmob.wakebrain.puzzle.MathProblem

sealed interface PuzzleUiState {
    // 1. Sedang memuat soal (misal membaca dari Room Database)
    data object Loading : PuzzleUiState

    // 2. Soal Matematika aktif di layar
    data class MathActive(
        val problem: MathProblem,
        val stage: Int = 1,
        val totalStages: Int = 2,
        val isError: Boolean = false,
        val errorMessage: String? = null
    ) : PuzzleUiState

    // 3. Soal Trivia (Pilihan Ganda) aktif di layar
    data class TriviaActive(
        val question: Question,
        val shuffledOptions: List<String>, // Opsi A, B, C diacak agar tidak mudah ditebak
        val stage: Int = 2,
        val totalStages: Int = 2,
        val isError: Boolean = false,
        val errorMessage: String? = null
    ) : PuzzleUiState

    // 4. Jawaban BENAR! Status ini menginstruksikan Frontend untuk mematikan audio alarm
    data object Solved : PuzzleUiState

    // 5. Gagal memuat data lokal
    data class Error(val message: String) : PuzzleUiState
}
