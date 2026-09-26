package com.pemmob.wakebrain.puzzle

import kotlin.random.Random

// Model data representasi soal matematika
data class MathProblem(
    val questionText: String,
    val correctAnswer: Int
)

class MathGenerator {
    /**
     * Menghasilkan soal matematika acak berdasarkan tingkat kesulitan.
     * Default: EASY (Penjumlahan puluhan)
     */
    fun generate(difficulty: String = "EASY"): MathProblem {
        val random = Random.Default

        return when (difficulty.uppercase()) {
            "HARD" -> {
                // Operasi campuran perkalian & penjumlahan/pengurangan: (A x B) ± C
                val a = random.nextInt(6, 13) // 6 .. 12
                val b = random.nextInt(3, 10) // 3 .. 9
                val c = random.nextInt(5, 25) // 5 .. 24
                val isAddition = random.nextBoolean()

                if (isAddition) {
                    MathProblem(
                        questionText = "$a × $b + $c = ?",
                        correctAnswer = (a * b) + c
                    )
                } else {
                    MathProblem(
                        questionText = "$a × $b - $c = ?",
                        correctAnswer = (a * b) - c
                    )
                }
            }
            "MEDIUM" -> {
                // Pilihan: Perkalian dasar atau Pengurangan positif
                val isMultiply = random.nextBoolean()
                if (isMultiply) {
                    val a = random.nextInt(3, 10)
                    val b = random.nextInt(3, 10)
                    MathProblem(
                        questionText = "$a × $b = ?",
                        correctAnswer = a * b
                    )
                } else {
                    val a = random.nextInt(30, 100)
                    val b = random.nextInt(10, a) // Menjamin hasil selalu positif
                    MathProblem(
                        questionText = "$a - $b = ?",
                        correctAnswer = a - b
                    )
                }
            }
            else -> { // EASY
                // Penjumlahan dua angka puluhan
                val a = random.nextInt(10, 50)
                val b = random.nextInt(5, 45)
                MathProblem(
                    questionText = "$a + $b = ?",
                    correctAnswer = a + b
                )
            }
        }
    }
}
