package com.interview.notes.java;

import java.util.List;

/**
 * Persistent storage for notes.
 * It allows Save and Retrieval of notes.
 */
public interface NotesStore {

    /**
     * Returns list of notes present in persistent storage.
     */
    List<Note> getNotes();

    /**
     * Save a new note in persistent storage.
     */
    void saveNote(Note note);

}
