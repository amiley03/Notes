package com.interview.notes.kotlin.model.repo


import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Abstraction of the data layer which can interact with local and remote data sources
 */
@Singleton
class NotesRepositoryImpl @Inject constructor(private val noteDao: NoteDao) : NotesRepository {

    override suspend fun fetchNote(noteId: String): Note {
        return noteDao.findById(noteId)
    }

    override suspend fun fetchNoteRange(start: Int, end: Int): List<Note>? {
        return noteDao.getNotes(start, end).singleOrNull()
    }

    override suspend fun saveNote(note: Note) {
        noteDao.insertNote(note)
    }

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().distinctUntilChanged()
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }
}
