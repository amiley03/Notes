package com.interview.notes.java;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class NotesStoreImpl implements NotesStore {

    private static final String NOTES_KEY = "notes_key";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public NotesStoreImpl(Context applicationContext) {
        this.preferences = applicationContext.getSharedPreferences("NotePref", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        this.gson = new Gson();
    }

    @Override
    public List<Note> getNotes() {
        String notesJson = preferences.getString(NOTES_KEY, null);
        if (notesJson == null){
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Note>>(){}.getType();
        return gson.fromJson(notesJson, type);
    }

    @Override
    public void saveNote(Note note) {
        List<Note> notes = getNotes();
        notes.add(note);

        String jsonNotes = gson.toJson(notes);
        editor.putString(NOTES_KEY, jsonNotes);
        editor.apply();
    }


}
