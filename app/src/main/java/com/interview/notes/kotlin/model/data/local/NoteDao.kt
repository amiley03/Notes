package com.interview.notes.kotlin.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.interview.notes.kotlin.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timeStamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY timeStamp DESC LIMIT :start, :end")
    fun getNotes(start: Int = 0, end: Int): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Insert
    fun insertAll(vararg notes: Note)

    @Query("SELECT * FROM notes WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): Flow<Note>

    @Query("SELECT * FROM notes WHERE title LIKE :title")
    fun findByTitle(title: String): Flow<Note>

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE id IS :noteId LIMIT 1")
    fun findById(noteId: String): Note
}
