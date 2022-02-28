package com.interview.notes.kotlin

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

}