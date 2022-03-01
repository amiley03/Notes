package com.interview.notes.kotlin.model.data.local

import com.interview.notes.kotlin.model.Note

/**
 * Persistent storage for notes.
 * It allows Save and Retrieval of notes.
 */
interface NotesStore {

    /**
     * Returns list of notes present in persistent storage.
     */
    fun getNotes(): List<Note>

    /**
     * Save a new note in persistent storage.
     */
    fun saveNote(note: Note)

    /**
     * Return a specific note from persistent storage.
     */
    fun getNote(noteId: String): Note?

}
