package com.interview.notes.kotlin.model.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.interview.notes.kotlin.model.Note
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesStoreImpl @Inject constructor(@ApplicationContext applicationContext: Context) : NotesStore {

    private val NOTES_KEY = "notes_key"

    private val preferences = applicationContext.getSharedPreferences("NotePref", Context.MODE_PRIVATE)
    private val editor = preferences.edit()
    private val gson: Gson = Gson()

    override fun getNotes(): MutableList<Note> {
        val notesJson = preferences.getString(NOTES_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<MutableList<Note>>() {}.type
        return gson.fromJson(notesJson, type)
    }

    override fun saveNote(note: Note) {
        val notes = getNotes()

        // check for existing note
        val index = notes.indexOfFirst { it.id == note.id }

        if (index > -1) {
            // update existing note
            notes.removeAt(index)
            notes.add(index, note)
        } else {
            notes.add(note)
        }

        val jsonNotes = gson.toJson(notes)
        editor.putString(NOTES_KEY, jsonNotes)
        editor.apply()
    }

    override fun getNote(noteId: String): Note? {
        val notes = getNotes()
        return notes.find { it.id == noteId }
    }
}

