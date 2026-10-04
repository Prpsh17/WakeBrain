package com.pemmob.wakebrain.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pemmob.wakebrain.data.model.Alarm
import com.pemmob.wakebrain.data.model.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Question::class, Alarm::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao

    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wakebrain_database",
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.questionDao()

                        val initialQuestions = listOf(
                            Question(
                                questionText = "Apa ibu kota Australia?",
                                optionA = "Sydney",
                                optionB = "Canberra",
                                optionC = "Perth",
                                correctAnswer = "Canberra",
                            ),
                            Question(
                                questionText = "Berapa hasil dari 8 x 7?",
                                optionA = "54",
                                optionB = "56",
                                optionC = "64",
                                correctAnswer = "56",
                            ),
                            Question(
                                questionText = "Gunung tertinggi di dunia adalah?",
                                optionA = "Gunung Everest",
                                optionB = "Gunung Kilimanjaro",
                                optionC = "Gunung Fuji",
                                correctAnswer = "Gunung Everest",
                            ),
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
                                days = "Sen • Sel • Rab • Kam • Jum",
                                ringtone = "Nada 1",
                            ),
                            Alarm(
                                hour = 5,
                                minute = 30,
                                isActive = true,
                                puzzleType = "Trivia",
                                difficulty = "MEDIUM",
                                label = "Bangun Subuh",
                                days = "Setiap Hari (Sen - Min)",
                                ringtone = "Nada 2",
                            ),
                            Alarm(
                                hour = 9,
                                minute = 0,
                                isActive = false,
                                puzzleType = "Matematika",
                                difficulty = "EASY",
                                label = "Weekend Santai",
                                days = "Sab • Min",
                                ringtone = "Nada 3",
                            ),
                        )
                        initialAlarms.forEach { alarmDao.insertAlarm(it) }
                    }
                }
            }
        }
    }
}
