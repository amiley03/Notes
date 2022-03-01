package com.interview.notes.kotlin.viewmodel

import com.interview.notes.R
import com.interview.notes.kotlin.model.repo.NotesRepository
import com.interview.notes.kotlin.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID.randomUUID
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(private val repository: NotesRepository) : BaseViewModel() {

    var noteId: String? = null
    var title: String = ""
    var content: String = ""
    var screenNameId = R.string.new_note

    private var originalNote: Note? = null

    override fun loadData() {
        noteId?.let { id ->
            // Editing existing note
            loadAsyncAndUpdatUI {
                repository.fetchNote(id)?.let { note ->
                    originalNote = note
                    title = note.title
                    content = note.content
                    screenNameId = R.string.edit_note
                    updateUIState(UIState.Loaded)
                } ?: updateUIState(UIState.Error(R.string.error_note_fetch))
            }
        } ?: updateUIState(UIState.Loaded)
    }

    fun handleSaveNoteClicked() {
        when {
            title.isEmpty() -> updateUIState(UIState.Error(R.string.error_title))
            content.isEmpty() -> updateUIState(UIState.Error(R.string.error_content))
            else -> saveNote()
        }
    }

    private fun saveNote() {
        loadAsyncAndUpdatUI {
            val note = Note(title, content, noteId ?: randomUUID().toString())
            repository.saveNote(note)
            _uiState.postValue(UIState.Exit)
        }
    }

    // todo use to show the save button once the content or tile changes
    fun shouldSaveNote(): Boolean {
        return originalNote?.let { note ->
            title != note.title && content != note.content
        } ?: true
    }
}
