package com.interview.notes.kotlin.model.repo


import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NotesStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Abstraction of the data layer which can interact with local and remote data sources
 */
@Singleton
class NotesRepositoryImpl @Inject constructor(private val notesStore: NotesStore) : NotesRepository {

    override val notes: SharedFlow<Note> get() = _noteFlow
    override val noteList: SharedFlow<List<Note>> get() = _noteList
    private val _noteFlow = MutableSharedFlow<Note>()
    private val _noteList = MutableSharedFlow<List<Note>>()

    override suspend fun fetchNote(noteId: String): Note? {
        return notesStore.getNote(noteId)
    }

    override suspend fun saveNote(note: Note) {
        notesStore.saveNote(note)
        //todo only emit on successful save to DB
        _noteFlow.emit(note)
    }

    override suspend fun loadNotes() {
        _noteList.emit(notesStore.getNotes())
    }
}
