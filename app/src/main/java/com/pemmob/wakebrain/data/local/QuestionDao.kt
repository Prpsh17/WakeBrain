package com.pemmob.wakebrain.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pemmob.wakebrain.data.model.Question

@Dao
interface QuestionDao {
    @Insert
    suspend fun insertAll(questions: List<Question>): List<Long>

    @Query("SELECT * FROM trivia_questions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(): Question?

    @Query("SELECT * FROM trivia_questions WHERE LOWER(difficulty) = LOWER(:difficulty) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestionByDifficulty(difficulty: String): Question?

    @Query("SELECT COUNT(*) FROM trivia_questions")
    suspend fun getCount(): Int
}
