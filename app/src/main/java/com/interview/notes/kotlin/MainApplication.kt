package com.interview.notes.kotlin

import android.app.Application

class MainApplication : Application() {

    private var notesStore: NotesStore? = null

    fun getNotesStore(): NotesStore? {
        if (notesStore == null) {
            notesStore = NotesStoreImpl(applicationContext)
        }
        return notesStore
    }

}
