package com.pemmob.wakebrain.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Question::class, Alarm::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao

    // Tambahkan baris ini agar sistem mengenali kueri alarm
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wakebrain_database"
                )
                    .addCallback(DatabaseCallback()) // Memanggil fungsi pre-populate
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // Fungsi Callback untuk memasukkan data saat database pertama kali dibuat
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.questionDao()

                        // Bank Soal Default yang disisipkan saat instalasi
                        val initialQuestions = listOf(
                            Question(
                                questionText = "Apa ibu kota Australia?",
                                optionA = "Sydney",
                                optionB = "Canberra",
                                optionC = "Perth",
                                correctAnswer = "Canberra"
                            ),
                            Question(
                                questionText = "Berapa hasil dari 8 x 7?",
                                optionA = "54",
                                optionB = "56",
                                optionC = "64",
                                correctAnswer = "56"
                            ),
                            Question(
                                questionText = "Gunung tertinggi di dunia adalah?",
                                optionA = "Gunung Everest",
                                optionB = "Gunung Kilimanjaro",
                                optionC = "Gunung Fuji",
                                correctAnswer = "Gunung Everest"
                            )
                        )
                        dao.insertAll(initialQuestions)

                        val alarmDao = database.alarmDao()
                        val initialAlarms = listOf(
                            Alarm(
                                hour = 7,
                                minute = 0,
                                isActive = true,
                                puzzleType = "Matematika",
                                difficulty = "EASY",
                                label = "Kuliah Pagi",
                                days = "Sen • Sel • Rab • Kam • Jum"
                            ),
                            Alarm(
                                hour = 5,
                                minute = 30,
                                isActive = true,
                                puzzleType = "Trivia",
                                difficulty = "MEDIUM",
                                label = "Bangun Subuh",
                                days = "Setiap Hari (Sen - Min)"
                            ),
                            Alarm(
                                hour = 9,
                                minute = 0,
                                isActive = false,
                                puzzleType = "Matematika",
                                difficulty = "EASY",
                                label = "Weekend Santai",
                                days = "Sab • Min"
                            )
                        )
                        initialAlarms.forEach { alarmDao.insertAlarm(it) }
                    }
                }
            }
        }
    }
}
