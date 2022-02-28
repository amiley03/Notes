package com.interview.notes.kotlin

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotesStoreImpl(applicationContext: Context) : NotesStore {

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
        notes.add(note)

        val jsonNotes = gson.toJson(notes)
        editor.putString(NOTES_KEY, jsonNotes)
        editor.apply()
    }
}