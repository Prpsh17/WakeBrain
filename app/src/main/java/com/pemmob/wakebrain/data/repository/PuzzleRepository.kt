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
     * Mengambil 1 soal matematika acak dari generator lokal
     */
    fun getMathProblem(difficulty: String = "EASY"): MathProblem {
        return mathGenerator.generate(difficulty)
    }

    /**
     * Mengambil 1 soal trivia acak dari Room Database (dengan auto-seeding jika kosong)
     */
    suspend fun getRandomTrivia(): Question? {
        if (questionDao.getCount() == 0) {
            val defaultQuestions = listOf(
                Question(questionText = "Apa ibu kota Australia?", optionA = "Sydney", optionB = "Canberra", optionC = "Perth", correctAnswer = "Canberra"),
                Question(questionText = "Berapa hasil dari 8 x 7?", optionA = "54", optionB = "56", optionC = "64", correctAnswer = "56"),
                Question(questionText = "Gunung tertinggi di dunia adalah?", optionA = "Gunung Everest", optionB = "Gunung Kilimanjaro", optionC = "Gunung Fuji", correctAnswer = "Gunung Everest"),
                Question(questionText = "Planet terbesar di tata surya kita adalah?", optionA = "Mars", optionB = "Yupiter", optionC = "Saturnus", correctAnswer = "Yupiter"),
                Question(questionText = "Candi Borobudur terletak di provinsi?", optionA = "Jawa Barat", optionB = "Jawa Tengah", optionC = "Jawa Timur", correctAnswer = "Jawa Tengah"),
                Question(questionText = "Mata uang negara Jepang adalah?", optionA = "Yen", optionB = "Won", optionC = "Yuan", correctAnswer = "Yen"),
                Question(questionText = "Lagu kebangsaan Indonesia adalah?", optionA = "Indonesia Raya", optionB = "Garuda Pancasila", optionC = "Rayuan Pulau Kelapa", correctAnswer = "Indonesia Raya"),
            )
            questionDao.insertAll(defaultQuestions)
        }
        return questionDao.getRandomQuestion()
    }
}
