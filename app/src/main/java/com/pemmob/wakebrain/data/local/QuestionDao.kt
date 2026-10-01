package com.pemmob.wakebrain.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pemmob.wakebrain.data.model.Question

@Dao
interface QuestionDao {
    // Memasukkan daftar soal ke database
    @Insert
    suspend fun insertAll(questions: List<Question>): List<Long>

    // Mengambil 1 soal trivia secara acak saat alarm berbunyi
    @Query("SELECT * FROM trivia_questions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(): Question?
}
