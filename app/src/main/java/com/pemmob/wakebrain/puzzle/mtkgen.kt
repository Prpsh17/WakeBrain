package com.pemmob.wakebrain.puzzle

import kotlin.random.Random

data class MathProblem(
    val questionText: String,
    val correctAnswer: Int
)

class MathGenerator {
    fun generate(difficulty: String = "EASY"): MathProblem {
        val random = Random.Default

        return when (difficulty.uppercase()) {
            "HARD" -> {
                val a = random.nextInt(6, 13)
                val b = random.nextInt(3, 10)
                val c = random.nextInt(5, 25)
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
                    val b = random.nextInt(10, a)
                    MathProblem(
                        questionText = "$a - $b = ?",
                        correctAnswer = a - b
                    )
                }
            }
            else -> {
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
