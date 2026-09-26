package com.pemmob.wakebrain.data.repository

import com.pemmob.wakebrain.data.Question
import com.pemmob.wakebrain.data.QuestionDao
import com.pemmob.wakebrain.puzzle.MathGenerator
import com.pemmob.wakebrain.puzzle.MathProblem

class PuzzleRepository(
    private val questionDao: QuestionDao,
    private val mathGenerator: MathGenerator = MathGenerator()
) {
    /**
     * Mengambil 1 soal matematika acak dari generator lokal
     */
    fun getMathProblem(difficulty: String = "EASY"): MathProblem {
        return mathGenerator.generate(difficulty)
    }

    /**
     * Mengambil 1 soal trivia acak dari Room Database
     */
    suspend fun getRandomTrivia(): Question? {
        return questionDao.getRandomQuestion()
    }
}
