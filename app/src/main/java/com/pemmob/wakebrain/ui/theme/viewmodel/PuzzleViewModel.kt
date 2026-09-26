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

    /**
     * Memuat tantangan baru sesuai konfigurasi alarm
     * @param puzzleType "Matematika" atau "Trivia"
     * @param difficulty "EASY", "MEDIUM", atau "HARD"
     */
    fun loadPuzzle(puzzleType: String, difficulty: String = "EASY") {
        activePuzzleType = puzzleType
        activeDifficulty = difficulty
        _uiState.value = PuzzleUiState.Loading

        if (puzzleType.equals("Trivia", ignoreCase = true) ||
            puzzleType.equals("Pengetahuan Umum", ignoreCase = true)
        ) {
            loadTriviaQuestion()
        } else {
            loadMathQuestion(difficulty)
        }
    }

    private fun loadMathQuestion(difficulty: String) {
        val problem = repository.getMathProblem(difficulty)
        _uiState.value = PuzzleUiState.MathActive(problem = problem)
    }

    private fun loadTriviaQuestion() {
        viewModelScope.launch {
            try {
                val trivia = repository.getRandomTrivia()
                if (trivia != null) {
                    // Acak urutan opsi jawaban A, B, C
                    val options = listOf(trivia.optionA, trivia.optionB, trivia.optionC).shuffled()
                    _uiState.value = PuzzleUiState.TriviaActive(
                        question = trivia,
                        shuffledOptions = options
                    )
                } else {
                    // Fallback jika bank soal kosong di Room
                    val fallbackProblem = repository.getMathProblem("EASY")
                    _uiState.value = PuzzleUiState.MathActive(
                        problem = fallbackProblem,
                        isError = true,
                        errorMessage = "Bank soal trivia kosong. Beralih ke Matematika."
                    )
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
                    // JAWABAN BENAR: Emit Solved State
                    _uiState.value = PuzzleUiState.Solved
                } else {
                    // JAWABAN SALAH: Beri pesan error dan ganti/refresh soal
                    val newProblem = repository.getMathProblem(activeDifficulty)
                    _uiState.value = PuzzleUiState.MathActive(
                        problem = newProblem,
                        isError = true,
                        errorMessage = "Jawaban salah! Coba hitung soal baru ini."
                    )
                }
            }
            is PuzzleUiState.TriviaActive -> {
                val isCorrect = userAnswer.trim().equals(state.question.correctAnswer.trim(), ignoreCase = true)
                if (isCorrect) {
                    // JAWABAN BENAR: Emit Solved State
                    _uiState.value = PuzzleUiState.Solved
                } else {
                    // JAWABAN SALAH: Tampilkan error & ambil soal trivia baru
                    viewModelScope.launch {
                        val newTrivia = repository.getRandomTrivia() ?: state.question
                        val options = listOf(newTrivia.optionA, newTrivia.optionB, newTrivia.optionC).shuffled()
                        _uiState.value = PuzzleUiState.TriviaActive(
                            question = newTrivia,
                            shuffledOptions = options,
                            isError = true,
                            errorMessage = "Jawaban salah! Alarm tetap berbunyi."
                        )
                    }
                }
            }
            else -> {
                // State lain (Loading, Solved, Error) diabaikan
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
