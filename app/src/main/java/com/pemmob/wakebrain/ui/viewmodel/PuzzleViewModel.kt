package com.pemmob.wakebrain.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pemmob.wakebrain.data.repository.PuzzleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PuzzleViewModel(
    private val repository: PuzzleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PuzzleUiState>(PuzzleUiState.Loading)
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private var activePuzzleType: String = "Matematika"
    private var activeDifficulty: String = "EASY"
    private var currentStage: Int = 1
    private val totalStages: Int = 2

    /**
     * Memuat tantangan baru sesuai konfigurasi alarm
     * @param puzzleType "Matematika" atau "Trivia"
     * @param difficulty "EASY", "MEDIUM", atau "HARD"
     */
    fun loadPuzzle(puzzleType: String = "Matematika", difficulty: String = "EASY") {
        activePuzzleType = puzzleType
        activeDifficulty = difficulty
        currentStage = 1
        _uiState.value = PuzzleUiState.Loading

        startStage(currentStage)
    }

    private fun startStage(stage: Int) {
        currentStage = stage
        if (stage == 1) {
            // Tantangan 1: Matematika (atau sesuai puzzleType jika Trivia)
            if (activePuzzleType.equals("Trivia", ignoreCase = true) ||
                activePuzzleType.equals("Pengetahuan Umum", ignoreCase = true)
            ) {
                loadTriviaQuestion(stage)
            } else {
                loadMathQuestion(activeDifficulty, stage)
            }
        } else {
            // Tantangan 2: Trivia Pengetahuan Umum (atau Matematika jika sebelumnya Trivia)
            if (activePuzzleType.equals("Trivia", ignoreCase = true) ||
                activePuzzleType.equals("Pengetahuan Umum", ignoreCase = true)
            ) {
                loadMathQuestion(activeDifficulty, stage)
            } else {
                loadTriviaQuestion(stage)
            }
        }
    }

    private fun loadMathQuestion(difficulty: String, stage: Int, isError: Boolean = false, errorMessage: String? = null) {
        val problem = repository.getMathProblem(difficulty)
        _uiState.value = PuzzleUiState.MathActive(
            problem = problem,
            stage = stage,
            totalStages = totalStages,
            isError = isError,
            errorMessage = errorMessage
        )
    }

    private fun loadTriviaQuestion(stage: Int, isError: Boolean = false, errorMessage: String? = null) {
        viewModelScope.launch {
            try {
                val trivia = repository.getRandomTrivia()
                if (trivia != null) {
                    val options = listOf(trivia.optionA, trivia.optionB, trivia.optionC).shuffled()
                    _uiState.value = PuzzleUiState.TriviaActive(
                        question = trivia,
                        shuffledOptions = options,
                        stage = stage,
                        totalStages = totalStages,
                        isError = isError,
                        errorMessage = errorMessage
                    )
                } else {
                    // Fallback ke soal matematika jika bank soal kosong
                    loadMathQuestion("EASY", stage, isError, errorMessage ?: "Bank soal trivia kosong. Beralih ke Matematika.")
                }
            } catch (e: Exception) {
                _uiState.value = PuzzleUiState.Error(e.message ?: "Gagal memuat soal trivia")
            }
        }
    }

    /**
     * Memverifikasi jawaban pengguna
     */
    fun submitAnswer(userAnswer: String) {
        when (val state = _uiState.value) {
            is PuzzleUiState.MathActive -> {
                val parsedAnswer = userAnswer.trim().toIntOrNull()
                if (parsedAnswer != null && parsedAnswer == state.problem.correctAnswer) {
                    // JAWABAN BENAR
                    if (currentStage < totalStages) {
                        // Lanjut ke Tantangan 2
                        startStage(currentStage + 1)
                    } else {
                        // Semua tantangan selesai!
                        _uiState.value = PuzzleUiState.Solved
                    }
                } else {
                    // JAWABAN SALAH: Beri pesan error dan ganti soal
                    loadMathQuestion(
                        activeDifficulty,
                        currentStage,
                        isError = true,
                        errorMessage = "Jawaban belum tepat, coba hitung lagi!"
                    )
                }
            }
            is PuzzleUiState.TriviaActive -> {
                val isCorrect = userAnswer.trim().equals(state.question.correctAnswer.trim(), ignoreCase = true)
                if (isCorrect) {
                    // JAWABAN BENAR
                    if (currentStage < totalStages) {
                        // Lanjut ke Tantangan 2
                        startStage(currentStage + 1)
                    } else {
                        // Semua tantangan selesai!
                        _uiState.value = PuzzleUiState.Solved
                    }
                } else {
                    // JAWABAN SALAH: Tampilkan error & beri soal baru
                    loadTriviaQuestion(
                        currentStage,
                        isError = true,
                        errorMessage = "Jawaban salah! Alarm masih berbunyi. Coba lagi."
                    )
                }
            }
            else -> {
                // State lain diabaikan
            }
        }
    }
}

/**
 * Factory untuk inisialisasi PuzzleViewModel dengan parameter PuzzleRepository
 */
class PuzzleViewModelFactory(
    private val repository: PuzzleRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PuzzleViewModel::class.java)) {
            return PuzzleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
