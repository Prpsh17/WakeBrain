package com.pemmob.wakebrain.data.repository

import com.pemmob.wakebrain.data.local.QuestionDao
import com.pemmob.wakebrain.data.model.Question
import com.pemmob.wakebrain.puzzle.MathGenerator
import com.pemmob.wakebrain.puzzle.MathProblem

class PuzzleRepository(
    private val questionDao: QuestionDao,
    private val mathGenerator: MathGenerator = MathGenerator(),
) {
    /**
     * Mengambil 1 soal matematika acak dari generator lokal berdasarkan difficulty
     */
    fun getMathProblem(difficulty: String = "EASY"): MathProblem {
        return mathGenerator.generate(difficulty)
    }

    /**
     * Mengambil 1 soal trivia acak dari Room Database berdasarkan difficulty (dengan auto-seeding & fallback)
     */
    suspend fun getRandomTrivia(difficulty: String = "EASY"): Question? {
        try {
            if (questionDao.getCount() == 0) {
                val defaultQuestions = listOf(
                    // EASY
                    Question(questionText = "Mata uang negara Jepang adalah?", optionA = "Yen", optionB = "Won", optionC = "Yuan", correctAnswer = "Yen", difficulty = "EASY"),
                    Question(questionText = "Lagu kebangsaan Indonesia adalah?", optionA = "Indonesia Raya", optionB = "Garuda Pancasila", optionC = "Rayuan Pulau Kelapa", correctAnswer = "Indonesia Raya", difficulty = "EASY"),
                    Question(questionText = "Warna bendera negara Indonesia adalah?", optionA = "Merah Putih", optionB = "Putih Merah", optionC = "Merah Biru", correctAnswer = "Merah Putih", difficulty = "EASY"),

                    // MEDIUM
                    Question(questionText = "Planet terbesar di tata surya kita adalah?", optionA = "Mars", optionB = "Yupiter", optionC = "Saturnus", correctAnswer = "Yupiter", difficulty = "MEDIUM"),
                    Question(questionText = "Candi Borobudur terletak di provinsi?", optionA = "Jawa Barat", optionB = "Jawa Tengah", optionC = "Jawa Timur", correctAnswer = "Jawa Tengah", difficulty = "MEDIUM"),
                    Question(questionText = "Simbol matematika untuk perkalian adalah?", optionA = "+", optionB = "×", optionC = "÷", correctAnswer = "×", difficulty = "MEDIUM"),

                    // HARD
                    Question(questionText = "Apa ibu kota Australia?", optionA = "Sydney", optionB = "Canberra", optionC = "Perth", correctAnswer = "Canberra", difficulty = "HARD"),
                    Question(questionText = "Gunung tertinggi di dunia adalah?", optionA = "Gunung Everest", optionB = "Gunung Kilimanjaro", optionC = "Gunung Fuji", correctAnswer = "Gunung Everest", difficulty = "HARD"),
                    Question(questionText = "Unsur kimia dengan lambang 'Au' adalah?", optionA = "Perak", optionB = "Emas", optionC = "Tembaga", correctAnswer = "Emas", difficulty = "HARD"),
                )
                questionDao.insertAll(defaultQuestions)
            }
            val match = questionDao.getRandomQuestionByDifficulty(difficulty)
            if (match != null) return match
            return questionDao.getRandomQuestion()
        } catch (_: Exception) {
            return questionDao.getRandomQuestion()
        }
    }
}
