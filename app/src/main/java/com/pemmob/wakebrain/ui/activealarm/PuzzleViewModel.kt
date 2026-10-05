package com.pemmob.wakebrain.ui.activealarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pemmob.wakebrain.data.repository.PuzzleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class PuzzleViewModel(
    private val repository: PuzzleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PuzzleUiState>(PuzzleUiState.Loading)
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private var activeDifficulty: String = "EASY"
    private var currentStage: Int = 1
    private val totalStages: Int = 2

    fun loadPuzzle(difficulty: String = "EASY") {
        activeDifficulty = difficulty
        currentStage = 1
        _uiState.value = PuzzleUiState.Loading

        startStage(currentStage)
    }

    private fun startStage(stage: Int) {
        currentStage = stage
        // Jenis soal dipilih ulang pada setiap tahap.
        val isMath = Random.nextBoolean()
        if (isMath) {
            loadMathQuestion(activeDifficulty, stage)
        } else {
            loadTriviaQuestion(stage)
        }
    }

    private fun loadMathQuestion(difficulty: String, stage: Int, isError: Boolean = false, errorMessage: String? = null) {
        val currentProblem = (_uiState.value as? PuzzleUiState.MathActive)?.problem
        var newProblem = repository.getMathProblem(difficulty)

        // Jangan langsung mengulang soal yang baru saja dijawab salah.
        if (isError && currentProblem != null) {
            var attempts = 0
            while (newProblem.questionText == currentProblem.questionText && attempts < 10) {
                newProblem = repository.getMathProblem(difficulty)
                attempts++
            }
        }

        _uiState.value = PuzzleUiState.MathActive(
            problem = newProblem,
            stage = stage,
            totalStages = totalStages,
            isError = isError,
            errorMessage = errorMessage,
        )
    }

    private fun loadTriviaQuestion(stage: Int, isError: Boolean = false, errorMessage: String? = null) {
        viewModelScope.launch {
            try {
                val currentQuestion = (_uiState.value as? PuzzleUiState.TriviaActive)?.question
                var trivia = repository.getRandomTrivia()

                // Jangan langsung mengulang pertanyaan yang baru saja dijawab salah.
                if (isError && currentQuestion != null) {
                    var attempts = 0
                    while (trivia != null && trivia.id == currentQuestion.id && attempts < 10) {
                        trivia = repository.getRandomTrivia()
                        attempts++
                    }
                }

                if (trivia != null) {
                    val options = listOf(trivia.optionA, trivia.optionB, trivia.optionC).shuffled()
                    _uiState.value = PuzzleUiState.TriviaActive(
                        question = trivia,
                        shuffledOptions = options,
                        stage = stage,
                        totalStages = totalStages,
                        isError = isError,
                        errorMessage = errorMessage,
                    )
                } else {
                    loadMathQuestion("EASY", stage, isError, errorMessage ?: "Bank soal trivia kosong. Beralih ke Matematika.")
                }
            } catch (e: Exception) {
                _uiState.value = PuzzleUiState.Error(e.message ?: "Gagal memuat soal trivia")
            }
        }
    }

    fun submitAnswer(userAnswer: String) {
        when (val state = _uiState.value) {
            is PuzzleUiState.MathActive -> {
                val parsedAnswer = userAnswer.trim().toIntOrNull()
                if (parsedAnswer != null && parsedAnswer == state.problem.correctAnswer) {
                    if (currentStage < totalStages) {
                        startStage(currentStage + 1)
                    } else {
                        _uiState.value = PuzzleUiState.Solved
                    }
                } else {
                    loadMathQuestion(
                        activeDifficulty,
                        currentStage,
                        isError = true,
                        errorMessage = "Jawaban belum tepat, coba hitung lagi!",
                    )
                }
            }
            is PuzzleUiState.TriviaActive -> {
                val isCorrect = userAnswer.trim().equals(state.question.correctAnswer.trim(), ignoreCase = true)
                if (isCorrect) {
                    if (currentStage < totalStages) {
                        startStage(currentStage + 1)
                    } else {
                        _uiState.value = PuzzleUiState.Solved
                    }
                } else {
                    loadTriviaQuestion(
                        currentStage,
                        isError = true,
                        errorMessage = "Jawaban salah! Alarm masih berbunyi. Coba lagi.",
                    )
                }
            }
            else -> {}
        }
    }
}

class PuzzleViewModelFactory(
    private val repository: PuzzleRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PuzzleViewModel::class.java)) {
            return PuzzleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
