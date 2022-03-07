package com.interview.notes.kotlin.model.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.interview.notes.kotlin.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
