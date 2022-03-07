package com.interview.notes.kotlin.model.repo

import com.interview.notes.kotlin.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun saveNote(note: Note)

    suspend fun fetchNote(noteId: String): Note

    suspend fun getAllNotes(): Flow<List<Note>>
}
